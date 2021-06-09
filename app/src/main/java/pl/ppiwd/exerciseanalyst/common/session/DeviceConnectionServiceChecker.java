package pl.ppiwd.exerciseanalyst.common.session;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import pl.ppiwd.exerciseanalyst.activities.utils.MetaMotionServiceConnection;
import pl.ppiwd.exerciseanalyst.common.BroadcastMsgs;

public class DeviceConnectionServiceChecker {

    private final LocalBroadcastManager broadcastManager;
    private boolean isDeviceConnected;
    private final BroadcastReceiver broadcastReceiver;

    public DeviceConnectionServiceChecker(AppCompatActivity activityClass) {
        this.broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                final String action = intent.getAction();
                Log.i("DataCollectionActivity", "Received msg with action: " + action);
                switch (action) {
                    case BroadcastMsgs.METAMOTION_SERVICE_ALIVE_RESP:
                        isDeviceConnected = true;
                        break;
                    case BroadcastMsgs.ACTIVITY_BROADCAST_INTENT_ACTION:
                        handleMsgFromService(intent.getStringExtra(BroadcastMsgs.MSG_KEY));
                        break;
                }
            }
        };
        this.broadcastManager = LocalBroadcastManager.getInstance(activityClass);
        this.broadcastManager.registerReceiver(broadcastReceiver, getIntentFilter());
    }

    public boolean isServiceRunning() {
        isDeviceConnected = false;
        broadcastManager.sendBroadcastSync(new Intent(BroadcastMsgs.CHECK_METAMOTION_SERVICE_ALIVE));
        return isDeviceConnected;
    }

    public void unregister() {
        this.broadcastManager.unregisterReceiver(broadcastReceiver);
    }

    private IntentFilter getIntentFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BroadcastMsgs.METAMOTION_SERVICE_ALIVE_RESP);
        filter.addAction(BroadcastMsgs.ACTIVITY_BROADCAST_INTENT_ACTION);
        return filter;
    }

    private void handleMsgFromService(String msg) {
        Log.i("DataCollectionActivity", "handleMsgFromService() MSG: " + msg);
        if (BroadcastMsgs.METAMOTION_SERIVCE_TERMINATION.equals(msg)) {
            isDeviceConnected = false;
        }
    }
}
