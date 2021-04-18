package pl.ppiwd.exerciseanalyst.activities.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.functions.Function;
import pl.ppiwd.exerciseanalyst.BuildConfig;
import pl.ppiwd.exerciseanalyst.common.Constants;
import pl.ppiwd.exerciseanalyst.persistence.MeasurementSelectCommand;
import pl.ppiwd.exerciseanalyst.persistence.dao.MeasurementsDao;
import pl.ppiwd.exerciseanalyst.persistence.entities.SessionWithMeasurements;
import pl.ppiwd.exerciseanalyst.utils.Command;

public class ServerConnection {
    private MeasurementsDao measurementsDao;
    private CompletableFuture currentRequest;
    private Runnable onSuccess;
    private Runnable onError;

    public ServerConnection(MeasurementsDao dao
            , Runnable onSuccess
            , Runnable onError) {
        this.measurementsDao = dao;
        this.currentRequest = null;
        this.onSuccess = onSuccess;
        this.onError = onError;
    }

    public void sendMeasurements(Context context) {
        if (currentRequest == null || currentRequest.isDone()) {
            CompletableFuture dbQuery = new MeasurementSelectCommand(measurementsDao).execute();
            sendRequest(dbQuery, context);
        } else {
            /**
             * TODO:
             * There's no particular reason behind not being able to query
             * multiple requests. With the current context it's a bit pointless,
             * as you're going to be sending the same data over and over
             * (MeasurementSelectCommand always queries the last row).
             */
            Log.e("ServerConnection", "Request is already in progress.");
        }
    }

    public void cancelRequest() {
        if (currentRequest != null && !currentRequest.isDone()) {
            currentRequest.cancel(true);
        }
    }

    private void sendRequest(CompletableFuture callbackChain, Context context) {
        this.currentRequest = callbackChain.thenApply(param -> {
            Log.i("ServerConnection", "Querying local data.");
            SessionWithMeasurements measurements = ((Single<SessionWithMeasurements>) param).blockingGet();
            Log.i("ServerConnection", "Local data queried, serializing.");
            SessionSerializer serializer = new SessionSerializer(measurements);
            String json = serializer.serialize();

            RequestQueue queue = Volley.newRequestQueue(context);
            StringRequest stringRequest = new StringRequest(
                    Request.Method.POST,
                    BuildConfig.SERVER_URL,
                    response -> {
                        try { onSuccess.run(); } catch(Throwable e) { }
                        Log.i("ServerConnection", "Received response: " + response);
                    },
                    error -> {
                        // Volley misinterprets response code 204 (No content) - below workaround
                        // status code can't be checked as error's networkResponse field is null
                        if(error instanceof TimeoutError) {
                            Log.i(
                                    "ServerConnection",
                                    "Error timeout, let's hope it comes from misinterpreted 204." +
                                            " Workaround applied. Data uploaded to server!"
                            );
                            try { onSuccess.run(); } catch(Throwable e) { }
                        }
                        else {
                            try { onError.run(); } catch(Throwable e) { }
                            Log.e("ServerConnection", error.toString());
                        }
                    }) {

                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    Log.i("ServerConnection", "Response status code: " + response.statusCode);
                    return super.parseNetworkResponse(response);
                }

                @Override
                protected VolleyError parseNetworkError(VolleyError volleyError) {
                    NetworkResponse networkResponse = volleyError.networkResponse;
                    if(networkResponse != null) {
                        String body = new String(networkResponse.data, StandardCharsets.UTF_8);
                        Log.i("ServerConnection",
                                "Error status code: " + networkResponse.statusCode + "; Body: "
                                        + body);
                    }

                    return super.parseNetworkError(volleyError);
                }

                @Override
                public byte[] getBody() {
                    return json.getBytes();
                }

                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public Map<String, String> getHeaders() {
                    HashMap<String, String> headers = new HashMap<>();
                    return headers;
                }
            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    0,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            ));

            Log.i("ServerConnection",
                    "Sending measurements to " + BuildConfig.SERVER_URL);
            queue.add(stringRequest);
            return null;
        });
    }
}
