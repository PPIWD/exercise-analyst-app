package pl.ppiwd.exerciseanalyst.common.auth;

import android.content.Context;

import com.google.gson.Gson;

import pl.ppiwd.exerciseanalyst.BuildConfig;
import pl.ppiwd.exerciseanalyst.utils.OnOperationResult;
import pl.ppiwd.exerciseanalyst.utils.VolleyJsonBodyRequest;

public class SignIn {
    private final SignInCredentials credentials;
    private final OnOperationResult onOperationResult;
    private final Context context;

    public SignIn(
            SignInCredentials credentials,
            OnOperationResult onOperationResult,
            Context context) {
        this.credentials = credentials;
        this.onOperationResult = onOperationResult;
        this.context = context;
    }

    public void execute() {
        String credentialsJson = new Gson().toJson(credentials);
        VolleyJsonBodyRequest.Builder reqBuilder = new VolleyJsonBodyRequest.Builder(context);
        VolleyJsonBodyRequest req = reqBuilder.setUrl(BuildConfig.SIGN_IN_URL)
                .setOnOperationResult(onOperationResult)
                .setJsonBody(credentialsJson)
                .build();
        req.execute();
    }
}
