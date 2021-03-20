package pl.ppiwd.exerciseanalyst.services.utils;

import android.os.Handler;
import android.os.Looper;

public class MainUiHandler {
    private Handler handler;

    public MainUiHandler() {
        handler = new Handler(Looper.getMainLooper());
    }

    public void postOnUI(Runnable runnable) {
        handler.post(runnable);
    }
}
