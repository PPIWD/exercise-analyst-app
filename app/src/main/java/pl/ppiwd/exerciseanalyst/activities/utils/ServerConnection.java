package pl.ppiwd.exerciseanalyst.activities.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.util.concurrent.CompletableFuture;

import io.reactivex.rxjava3.core.Single;
import pl.ppiwd.exerciseanalyst.common.auth.TokenStore;
import pl.ppiwd.exerciseanalyst.persistence.MeasurementSelectCommand;
import pl.ppiwd.exerciseanalyst.persistence.dao.MeasurementsDao;
import pl.ppiwd.exerciseanalyst.persistence.entities.SessionWithMeasurements;
import pl.ppiwd.exerciseanalyst.services.utils.MainUiHandler;
import pl.ppiwd.exerciseanalyst.utils.OperationResult;
import pl.ppiwd.exerciseanalyst.utils.RequestOperationResult;
import pl.ppiwd.exerciseanalyst.utils.VolleyJsonBodyRequest;

public class ServerConnection {
    private final Context context;
    private final MeasurementsDao measurementsDao;
    private CompletableFuture currentRequest;

    public ServerConnection(MeasurementsDao dao, Context context) {
        this.measurementsDao = dao;
        this.currentRequest = null;
        this.context = context;
    }

    public void sendModelTrainingData(String url) {
        Log.i("ServerConnection", "Sending data to the server");
        if (currentRequest == null || currentRequest.isDone()) {
            CompletableFuture dbQuery = new MeasurementSelectCommand(measurementsDao).execute();
            sendRequest(dbQuery, context, url);
        } else {
            Log.e("ServerConnection", "Request is already in progress.");
        }
    }

    public void cancelRequest() {
        if (currentRequest != null && !currentRequest.isDone()) {
            currentRequest.cancel(true);
        }
    }

    private void sendRequest(CompletableFuture callbackChain, Context context, String url) {
        this.currentRequest = callbackChain.thenApply(param -> {
            Log.i("ServerConnection", "Querying local data.");
            SessionWithMeasurements measurements = ((Single<SessionWithMeasurements>) param).blockingGet();
            Log.i("ServerConnection", "Local data queried, serializing.");
            SessionSerializer serializer = new SessionSerializer(measurements);
            String json = serializer.serialize();

            VolleyJsonBodyRequest.Builder builder = new VolleyJsonBodyRequest.Builder(context);
            VolleyJsonBodyRequest req = builder
                    .setUrl(url)
                    .setOnOperationResult(this::logUploadStatus)
                    .setJsonBody(json)
                    .withSecurityToken(TokenStore.getInstance().getAccessToken())
                    .build();

            req.execute();

            return null;
        });
    }

    private void logUploadStatus(OperationResult operationResult) {
        RequestOperationResult result = (RequestOperationResult) operationResult;
        if(result.isSuccessful()) {
            showToast("Data synced with remote.", Toast.LENGTH_SHORT);
        }
        else {
            showToast("Could not sync with remote. Try again.", Toast.LENGTH_LONG);
        }
    }

    private void showToast(String s, int lengthLong) {
        new MainUiHandler().postOnUI(() -> Toast.makeText(context, s, lengthLong).show());
    }

    public void sendDataToRecognize(String recognizeDataUrl) {

    }
}
