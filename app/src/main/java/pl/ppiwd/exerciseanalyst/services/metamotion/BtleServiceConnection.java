package pl.ppiwd.exerciseanalyst.services.metamotion;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.mbientlab.metawear.android.BtleService;

public class BtleServiceConnection implements ServiceConnection {
    private boolean serviceBound;
    private BtleService.LocalBinder serviceBinder;
    private MetaMotionCtrl metaMotionCtrl;

    public BtleServiceConnection(MetaMotionCtrl metaMotionCtrl) {
        this.metaMotionCtrl = metaMotionCtrl;
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        Log.i("BtleServiceConnection", "MetaMotionService connected");
        serviceBound = true;
        serviceBinder = (BtleService.LocalBinder) service;
        metaMotionCtrl.connectAsync(serviceBinder);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        Log.i("BtleServiceConnection", "MetaMotionService disconnected");
        serviceBound = false;
    }

    public boolean isServiceBound() {
        return serviceBound;
    }
}
