package pl.ppiwd.exerciseanalyst.utils;

import com.google.gson.JsonObject;

public class RequestOperationResult extends OperationResult {
    private boolean success;
    private JsonObject responseJson;
    private int responseStatusCode;

    public RequestOperationResult(boolean success, JsonObject responseJson, int responseStatusCode) {
        this.success = success;
        this.responseJson = responseJson;
        this.responseStatusCode = responseStatusCode;
    }

    @Override
    public boolean isSuccessful() {
        return success;
    }

    public JsonObject getResponseJson() {
        return responseJson;
    }

    public int getResponseStatusCode() {
        return responseStatusCode;
    }
}
