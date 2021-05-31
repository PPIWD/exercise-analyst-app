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
import pl.ppiwd.exerciseanalyst.common.auth.TokenStore;
import pl.ppiwd.exerciseanalyst.persistence.MeasurementSelectCommand;
import pl.ppiwd.exerciseanalyst.persistence.dao.MeasurementsDao;
import pl.ppiwd.exerciseanalyst.persistence.entities.SessionWithMeasurements;
import pl.ppiwd.exerciseanalyst.utils.Command;
import pl.ppiwd.exerciseanalyst.utils.OnOperationResult;
import pl.ppiwd.exerciseanalyst.utils.VolleyJsonBodyRequest;

public class ServerConnection {
    private MeasurementsDao measurementsDao;
    private CompletableFuture currentRequest;
    private OnOperationResult onOperationResult;

    public ServerConnection(MeasurementsDao dao, OnOperationResult onOperationResult) {
        this.measurementsDao = dao;
        this.currentRequest = null;
        this.onOperationResult = onOperationResult;
    }

    public void sendMeasurements(Context context) {
        Log.i("ServerConnection", "Sending data to the server");
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

            VolleyJsonBodyRequest.Builder builder = new VolleyJsonBodyRequest.Builder(context);
            VolleyJsonBodyRequest req = builder
                    .setUrl(BuildConfig.SERVER_URL)
                    .setOnOperationResult(onOperationResult)
                    .setJsonBody(json)
                    .withSecurityToken(TokenStore.getInstance().getAccessToken())
                    .build();

            req.execute();

            return null;
        });
    }
}
