package pl.ppiwd.exerciseanalyst.services.metamotion.helpers;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.util.Log;

import com.mbientlab.metawear.MetaWearBoard;
import com.mbientlab.metawear.android.BtleService;

import bolts.Continuation;

public class MetaWearConnectionEstablisher {
    private static final int MAX_TRIALS = 3;
    private static final int CONNECTION_DELAY_MS = 1500;
    private MetaWearBoard board;
    private String deviceMacAddress;
    private ConnectionEventListener connectionEventListener;
    private Context context;
    private BtleService.LocalBinder serviceBinder;
    private BluetoothDevice remoteDevice;
    private int trial;

    public MetaWearConnectionEstablisher(
            String deviceMacAddress,
            ConnectionEventListener connectionEventListener,
            Context context,
            BtleService.LocalBinder serviceBinder) {
        this.board = null;
        this.deviceMacAddress = deviceMacAddress;
        this.connectionEventListener = connectionEventListener;
        this.context = context;
        this.serviceBinder = serviceBinder;
        this.remoteDevice = null;
        trial = 0;
    }

    public void connectAsync() {
        Log.i("MetaWearConnectionEstablisher",
                "Attempting connection with MetaWear device: " + deviceMacAddress);
        BluetoothManager btManager = (BluetoothManager) context.getSystemService(
                Context.BLUETOOTH_SERVICE
        );
        remoteDevice = btManager.getAdapter().getRemoteDevice(deviceMacAddress);
        trial = 0;
        doConnectAsync();
    }

    private void doConnectAsync() {
        board = serviceBinder.getMetaWearBoard(remoteDevice);
        board.connectAsync(CONNECTION_DELAY_MS).continueWith((Continuation<Void, Void>) task -> {
            if (task.isFaulted())
                onConnectionFailed();
            else
                onConnectionSuccessful();

            return null;
        });
    }

    private void onConnectionFailed() {
        Log.i("MetaWearConnectionEstablisher", "Failed to connect. Trial=" + trial);
        trial++;
        if (trial >= MAX_TRIALS) {
            connectionEventListener.onConnectionFailed();
        } else
            doConnectAsync();
    }

    private void onConnectionSuccessful() {
        Log.i("MetaWearConnectionEstablisher", "Connected. Trial=" + trial);
        connectionEventListener.onConnectionSuccessful(board);
    }
}
