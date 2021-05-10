package pl.ppiwd.exerciseanalyst.utils;

import android.content.Context;
import android.util.Log;

import com.android.volley.BuildConfig;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;


public class VolleyJsonBodyRequest {
    public static class Builder {
        private VolleyJsonBodyRequest request;

        public Builder(Context context) {
            this.request = new VolleyJsonBodyRequest(context);
        }

        public Builder setOnOperationResult(OnOperationResult onOperationResult) {
            this.request.setOnOperationResult(onOperationResult);
            return this;
        }

        public Builder setUrl(String url) {
            this.request.setUrl(url);
            return this;
        }

        public Builder setMethodType(int methodType) {
            this.request.setMethodType(methodType);
            return this;
        }

        public Builder setJsonBody(String jsonBody) {
            request.setJsonBody(jsonBody);
            return this;
        }

        public Builder setGetParams(Map<String, String> getParams) {
            request.setGetParams(getParams);
            return this;
        }

        public Builder setHeaders(Map<String, String> headers) {
            request.setHeaders(headers);
            return this;
        }

        public Builder withSecurityToken(String accessToken) {
            request.setSecurityToken(accessToken);
            return this;
        }

        public VolleyJsonBodyRequest build() {
            request.verify();
            return request;
        }
    }

    private Context context;
    private OnOperationResult onOperationResult;
    private String url;
    private String effectiveUrl;
    private int methodType;
    private String jsonBody;
    private Map<String, String> getParams;
    private Map<String, String> headers;
    private int responseCode;
    private String errorBody;

    public VolleyJsonBodyRequest(Context context) {
        this.context = context;
        this.methodType = Request.Method.POST;
        this.jsonBody = "";
        this.getParams = new HashMap<>();
        this.headers = new HashMap<>();
        this.errorBody = "";
    }

    private void setOnOperationResult(OnOperationResult onOperationResult) {
        this.onOperationResult = onOperationResult;
    }

    private void setUrl(String url) {
        this.url = url;
        this.effectiveUrl = this.url;
    }

    private void setMethodType(int methodType) {
        this.methodType = methodType;
    }

    private void setJsonBody(String jsonBody) {
        this.jsonBody = jsonBody;
    }

    private void setGetParams(Map<String, String> getParams) {
        this.getParams = getParams;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers.putAll(headers);
    }

    private void setSecurityToken(String accessToken) {
        this.headers.put("Authorization", "Bearer " + accessToken);
    }

    private void verify() {
        if(onOperationResult == null)
            throw new RuntimeException("VolleyJsonBodyRequest: onOperationResult must be set");
        if(url == null || url.isEmpty())
            throw new RuntimeException("VolleyJsonBodyRequest: url can't be empty");
    }

    public void execute() {
        modifyUrlWithParams();
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(
                methodType,
                effectiveUrl,
                this::onReqSuccess,
                this::onReqError) {

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                Log.d("VolleyJsonBodyRequest", "Response status code: " + response.statusCode);
                responseCode = response.statusCode;
                return super.parseNetworkResponse(response);
            }

            @Override
            protected VolleyError parseNetworkError(VolleyError volleyError) {
                NetworkResponse networkResponse = volleyError.networkResponse;
                if(networkResponse != null) {
                    errorBody = new String(networkResponse.data, StandardCharsets.UTF_8);
                    responseCode = networkResponse.statusCode;
                    Log.e("VolleyJsonBodyRequest",
                            "Error status code: " + networkResponse.statusCode + "; Body: "
                                    + errorBody);
                }

                return super.parseNetworkError(volleyError);
            }

            @Override
            public byte[] getBody() {
                if(jsonBody != null && !jsonBody.isEmpty())
                    return jsonBody.getBytes();
                else
                    return null;
            }

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public Map<String, String> getHeaders() {
                return headers;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        Log.d("VolleyJsonBodyRequest", "Sending request: " + stringRequest);
        queue.add(stringRequest);
    }

    private void onReqSuccess(String response) {
        Log.d("VolleyJsonBodyRequest", "Received response: " + response);
        try {
            RequestOperationResult result = new RequestOperationResult(
                    true,
                    parseJson(response),
                    responseCode
            );
            onOperationResult.onOperationResult(result);
        } catch(Throwable e) {
            Log.e("VolleyJsonBodyRequest",
                    "Response success callback threw an exception: " + e);
        }
    }

    private void onReqError(VolleyError error) {
        Log.e("VolleyJsonBodyRequest", "Response error: " + error);
        try {
            RequestOperationResult result = new RequestOperationResult(
                    false,
                    parseJson(errorBody),
                    responseCode
            );
            onOperationResult.onOperationResult(result);
        } catch(Throwable e) {
            Log.e("VolleyJsonBodyRequest",
                    "Response error callback threw an exception: " + e);
        }
    }

    private void modifyUrlWithParams() {
        this.effectiveUrl = this.url;
        if(getParams.isEmpty())
            return;

        StringBuilder strBuilder = new StringBuilder(effectiveUrl);
        strBuilder.append('?');
        for(Map.Entry<String, String> param : getParams.entrySet())  {
            strBuilder.append(param.getKey());
            strBuilder.append('=');
            strBuilder.append(param.getValue());
            strBuilder.append('&');
        }
        effectiveUrl = strBuilder.substring(0, strBuilder.length() - 1);
    }

    private JsonObject parseJson(String jsonString) {
        try {
            JsonElement respBody = JsonParser.parseString(jsonString);
            return respBody.isJsonObject()? respBody.getAsJsonObject() : null;
        } catch(JsonSyntaxException e) {
            Log.d("VolleyJsonBodyRequest", "Body is not valid json: " + e);
            return null;
        }
    }
}
