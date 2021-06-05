package pl.ppiwd.exerciseanalyst.activities.utils;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import pl.ppiwd.exerciseanalyst.utils.Callback;

public class BluetoothDeviceScanner {
    private BluetoothAdapter blAdapter;
    private Context appContext;
    private Callback<String> deviceFoundCallback;
    Boolean cbFired = false;

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.i("BluetoothDeviceScanner", "received broadcast message" + action);
            switch (action) {
                case BluetoothDevice.ACTION_FOUND: {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    String name = device.getName();
                    String mac = device.getAddress();
                    Log.i("BluetoothDeviceScanner", "Detected device\n name: "
                            + name + "\n MAC: " + mac);

                    // This bit is slightly hacky, but we want to auto-connect
                    // to the first MetaWear device that we find.
                    if (device.getName().equals("MetaWear")) {
                        cbFired = true;
                        deviceFoundCallback.run(mac);
                        BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    }
                    break;
                }
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED: {
                    if (!cbFired) {
                        deviceFoundCallback.run(null);
                    }
                    break;
                }
            }
        }
    };

    public BluetoothDeviceScanner(Context context, Callback<String> deviceFound) {
        appContext = context;
        deviceFoundCallback = deviceFound;
        blAdapter = BluetoothAdapter.getDefaultAdapter();
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        appContext.registerReceiver(receiver, filter);
        blAdapter.startDiscovery();
        Log.i("BluetoothDeviceScanner", "starting discovery");
    }

    public void close() {
        Log.i("BluetoothDeviceScanner", "close()");
        appContext.unregisterReceiver(receiver);
        blAdapter.cancelDiscovery();
    }
}
