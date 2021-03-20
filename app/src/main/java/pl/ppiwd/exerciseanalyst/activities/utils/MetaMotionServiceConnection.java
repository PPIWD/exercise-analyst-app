package pl.ppiwd.exerciseanalyst.activities.utils;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

public class MetaMotionServiceConnection implements ServiceConnection {
    private boolean serviceBound;

    public MetaMotionServiceConnection() {
        serviceBound = false;
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        Log.i("DataCollectionActivity", "MetaMotionService connected");
        serviceBound = true;
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        Log.i("DataCollectionActivity", "MetaMotionService disconnected");
        serviceBound = false;
    }

    public boolean isServiceBound() {
        return serviceBound;
    }
}
