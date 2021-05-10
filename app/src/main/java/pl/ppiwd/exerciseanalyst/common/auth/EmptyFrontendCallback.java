package pl.ppiwd.exerciseanalyst.common.auth;

public class EmptyFrontendCallback implements FrontendCallback {
    @Override
    public void onSignInSuccess() {}

    @Override
    public void onSignInFailed() {}

    @Override
    public void onSignUpSuccess() {}

    @Override
    public void onSignUpFailed() {}
}
