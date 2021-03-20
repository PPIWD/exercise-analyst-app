package pl.ppiwd.exerciseanalyst.common;

import android.content.Intent;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class ServiceActivityMsgSender {
    private final LocalBroadcastManager localBroadcastManager;

    public ServiceActivityMsgSender(LocalBroadcastManager localBroadcastManager) {
        this.localBroadcastManager = localBroadcastManager;
    }

    public void send(String msg) {
        Intent intent = new Intent(BroadcastMsgs.ACTIVITY_BROADCAST_INTENT_ACTION);
        intent.putExtra(BroadcastMsgs.MSG_KEY, msg);
        localBroadcastManager.sendBroadcast(intent);
    }
}
