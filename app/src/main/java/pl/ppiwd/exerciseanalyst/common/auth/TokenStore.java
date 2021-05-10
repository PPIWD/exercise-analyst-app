package pl.ppiwd.exerciseanalyst.common.auth;

public final class TokenStore {
    private static final Object lock = new Object();
    private static TokenStore instance = null;

    private String accessToken;

    public static TokenStore getInstance() {
        TokenStore tokenStore = instance;
        if (tokenStore == null) {
            synchronized (lock) {    // While we were waiting for the lock, another
                tokenStore = instance;        // thread may have instantiated the object.
                if (tokenStore == null) {
                    tokenStore = new TokenStore();
                    instance = tokenStore;
                }
            }
        }
        return tokenStore;
    }

    private TokenStore() {
        this.accessToken = "";
    }

    public void storeToken(final String accessToken) {
        synchronized (lock) {
            this.accessToken = accessToken;
        }
    }

    public String getAccessToken() {
        synchronized (lock) {
            return accessToken;
        }
    }

    public void wipeToken() {
        synchronized (lock) {
            this.accessToken = "";
        }
    }
}
