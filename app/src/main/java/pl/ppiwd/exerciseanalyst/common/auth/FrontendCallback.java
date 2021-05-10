package pl.ppiwd.exerciseanalyst.common.auth;

public interface FrontendCallback {
    void onSignInSuccess();
    void onSignInFailed();
    void onSignUpSuccess();
    void onSignUpFailed();
}
