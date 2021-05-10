package pl.ppiwd.exerciseanalyst.common.auth;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import pl.ppiwd.exerciseanalyst.R;
import pl.ppiwd.exerciseanalyst.common.Constants;
import pl.ppiwd.exerciseanalyst.utils.OperationResult;
import pl.ppiwd.exerciseanalyst.utils.RequestOperationResult;

public class UserAccountManager {
    private Context context;
    private FrontendCallback frontendCallback;

    public UserAccountManager(Context context, FrontendCallback frontendCallback) {
        this.context = context;
        this.frontendCallback = frontendCallback;
    }

    public static boolean isSignedIn(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.access_token_shared_preferences), Context.MODE_PRIVATE);
        String accessToken = sharedPref.getString(Constants.ACCESS_TOKEN_SHARED_PREFS_KEY, "");
        boolean signedIn = !accessToken.isEmpty();
        TokenStore tokenStore = TokenStore.getInstance();
        if(signedIn) {
            Log.i("UserAccountManager", "isSignedIn() TRUE");
            tokenStore.storeToken(accessToken);
        }
        else {
            Log.i("UserAccountManager", "isSignedIn() FALSE");
            tokenStore.wipeToken();
        }
        return signedIn;
    }

    public static void wipeToken(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.access_token_shared_preferences), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove(Constants.ACCESS_TOKEN_SHARED_PREFS_KEY);
        editor.apply();

        TokenStore.getInstance().wipeToken();
    }

    public void signIn(SignInCredentials credentials) {
        SignIn signInReq = new SignIn(credentials, this::onSignInResult, context);
        signInReq.execute();
    }

    private void onSignInResult(OperationResult operationResult) {
        RequestOperationResult result = (RequestOperationResult) operationResult;
        if(!errorsInResponse(result.getResponseJson()) && result.isSuccessful()) {
            try {
                storeAccessToken(result.getResponseJson());
                frontendCallback.onSignInSuccess();
            } catch(InvalidDataException e) {
                Log.e("UserAccountManager", "invalid data in SignIn response: " + e);
                frontendCallback.onSignInFailed();
            }
        }
        else {
            TokenStore.getInstance().wipeToken();
            frontendCallback.onSignInFailed();
        }
    }

    public void signUp(SignUpData signUpData) {
        SignUp signUpReq = new SignUp(signUpData, this::onSignUpResult, context);
        signUpReq.execute();
    }

    private void onSignUpResult(OperationResult operationResult) {
        RequestOperationResult result = (RequestOperationResult) operationResult;
        if(!errorsInResponse(result.getResponseJson()) && result.isSuccessful()) {
            try {
                storeAccessToken(result.getResponseJson());
                frontendCallback.onSignUpSuccess();
            } catch(InvalidDataException e) {
                Log.e("UserAccountManager", "invalid data in SignUp response: " + e);
                frontendCallback.onSignUpFailed();
            }
        }
        else {
            TokenStore.getInstance().wipeToken();
            frontendCallback.onSignUpFailed();
        }
    }

    private void storeAccessToken(JsonObject responseJson) {
        if(responseJson == null)
            throw new InvalidDataException("Response Json is null");

        final String accessToken = getAccessToken(responseJson);
        if(accessToken == null || accessToken.isEmpty())
            throw new InvalidDataException("accessToken is empty");

        Log.i("UserAccountManager", "Storing access token");
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.access_token_shared_preferences), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(Constants.ACCESS_TOKEN_SHARED_PREFS_KEY, accessToken);
        editor.apply();

        TokenStore.getInstance().storeToken(accessToken);
    }

    private String getAccessToken(JsonObject json) {
        JsonElement payloadElement = json.get("payload");
        if(payloadElement == null || !payloadElement.isJsonObject())
            throw new InvalidDataException("payload not found in response body or not a json object");

        JsonObject payload = json.getAsJsonObject("payload");
        if(payload == null)
            throw new InvalidDataException("payload not found in response body");

        JsonElement accessTokenElement = payload.get("accessToken");
        if(accessTokenElement == null)
            throw new InvalidDataException("accessToken is null");

        try {
            return accessTokenElement.getAsString();
        } catch(ClassCastException e) {
            throw new InvalidDataException("accessToken is not a string");
        }
    }

    private boolean errorsInResponse(JsonObject responseJson) {
        if(responseJson == null)
            return false;

        JsonElement errorsArrayElement = responseJson.get("errors");
        if(errorsArrayElement == null || !errorsArrayElement.isJsonArray())
            return false;

        JsonArray errorsArray = responseJson.getAsJsonArray("errors");
        if(errorsArray.size() == 0)
            return false;

        for(JsonElement arrayElement : errorsArray) {
            if(arrayElement != null) {
                try {
                    String errorString = arrayElement.getAsString();
                    Log.e("UserAccountManager", "error: " + errorString);
                } catch(ClassCastException e) {
                    Log.e("UserAccountManager", "error array element is not a string");
                }
            }
        }

        return true;
    }
}
