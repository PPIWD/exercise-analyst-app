package pl.ppiwd.exerciseanalyst.services;

import android.os.Handler;
import android.widget.TextView;

public class Timer implements Runnable {

    private final TextView timerTextView;
    private final Handler timerHandler;
    private final long startTime;

    public Timer(TextView timerTextView, Handler timerHandler) {
        this.timerTextView = timerTextView;
        this.timerHandler = timerHandler;
        this.startTime = 0;
    }

    @Override
    public void run() {
        long millis = System.currentTimeMillis() - startTime;
        int seconds = (int) (millis / 1000);
        int minutes = seconds / 60;
        seconds = seconds % 60;

        timerTextView.setText(String.format("%d:%02d", minutes, seconds));

        timerHandler.postDelayed(this, 500);
    }
}
