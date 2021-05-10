package pl.ppiwd.exerciseanalyst.common.auth;

import android.content.Context;

import com.google.gson.Gson;

import pl.ppiwd.exerciseanalyst.BuildConfig;
import pl.ppiwd.exerciseanalyst.utils.OnOperationResult;
import pl.ppiwd.exerciseanalyst.utils.VolleyJsonBodyRequest;

public class SignUp {
    private final SignUpData signUpData;
    private final OnOperationResult onOperationResult;
    private final Context context;

    public SignUp(
            SignUpData signUpData,
            OnOperationResult onOperationResult,
            Context context) {
        this.signUpData = signUpData;
        this.onOperationResult = onOperationResult;
        this.context = context;
    }

    public void execute() {
        String signUpDataJson = new Gson().toJson(signUpData);
        VolleyJsonBodyRequest.Builder reqBuilder = new VolleyJsonBodyRequest.Builder(context);
        VolleyJsonBodyRequest req = reqBuilder.setUrl(BuildConfig.SIGN_UP_URL)
                .setOnOperationResult(onOperationResult)
                .setJsonBody(signUpDataJson)
                .build();
        req.execute();
    }
}
