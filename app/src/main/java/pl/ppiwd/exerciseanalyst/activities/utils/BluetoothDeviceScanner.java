package pl.ppiwd.exerciseanalyst.activities.utils;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

import pl.ppiwd.exerciseanalyst.utils.Callback;

public class BluetoothDeviceScanner {
    private BluetoothAdapter blAdapter;
    private Context appContext;
    private Callback<String> deviceFoundCallback;

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.i("BluetoothDeviceScanner", "received broadcast message");
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String name = device.getName();
                String mac = device.getAddress();
                Log.i("BluetoothDeviceScanner", "Detected device\n name: "
                        + name + "\n MAC: " + mac);
                if (device.getName().equals("MetaWear")) {
                    deviceFoundCallback.run(mac);
                }
            }
        }
    };

    public BluetoothDeviceScanner(Context context, Callback<String> deviceFound) {
        appContext = context;
        deviceFoundCallback = deviceFound;
        blAdapter = BluetoothAdapter.getDefaultAdapter();
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        appContext.registerReceiver(receiver, filter);
        Log.i("BluetoothDeviceScanner", "starting discovery");
    }

    public void close() {
        appContext.unregisterReceiver(receiver);
        blAdapter.cancelDiscovery();
    }
}
