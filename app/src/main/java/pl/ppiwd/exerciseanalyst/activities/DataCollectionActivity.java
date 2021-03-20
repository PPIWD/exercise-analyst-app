package pl.ppiwd.exerciseanalyst.activities;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;
import pl.ppiwd.exerciseanalyst.R;
import pl.ppiwd.exerciseanalyst.activities.utils.MetaMotionServiceConnection;
import pl.ppiwd.exerciseanalyst.common.BroadcastMsgs;
import pl.ppiwd.exerciseanalyst.common.Constants;
import pl.ppiwd.exerciseanalyst.services.metamotion.MetaMotionService;
import pl.ppiwd.exerciseanalyst.utils.WipeDatabase;

@RuntimePermissions
public class DataCollectionActivity extends AppCompatActivity {
    private LocalBroadcastManager localBroadcastManager;
    private boolean isMetaMotionServiceAlive;
    private MetaMotionServiceConnection metaMotionServiceConnection;

    private EditText etDeviceMacAddress;
    private Button btnStartMetaMotionService;
    private Button btnStopMetaMotionService;
    private Button btnWipeDb;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.i("DataCollectionActivity", "Received msg with action: " + action);
            switch (action) {
                case BroadcastMsgs.METAMOTION_SERVICE_ALIVE_RESP:
                    isMetaMotionServiceAlive = true;
                    break;
                case BroadcastMsgs.ACTIVITY_BROADCAST_INTENT_ACTION:
                    handleMsgFromService(intent.getStringExtra(BroadcastMsgs.MSG_KEY));
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("DataCollectionActivity()", "onCreate()");
        setContentView(R.layout.activity_main);
        initViews();
        init();
    }

    private void init() {
        Log.i("DataCollectionActivity", "init()");
        isMetaMotionServiceAlive = false;
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BroadcastMsgs.METAMOTION_SERVICE_ALIVE_RESP);
        intentFilter.addAction(BroadcastMsgs.ACTIVITY_BROADCAST_INTENT_ACTION);
        localBroadcastManager.registerReceiver(broadcastReceiver, intentFilter);
    }

    private void initViews() {
        etDeviceMacAddress = findViewById(R.id.et_device_mac_address);
        fillDeviceMacAddressEditTextFromSharedPrefs();
        btnStartMetaMotionService = findViewById(R.id.btn_start_meta_motion);
        btnStopMetaMotionService = findViewById(R.id.btn_stop_meta_motion);
        btnWipeDb = findViewById(R.id.btn_wipe_db);
        btnStartMetaMotionService.setOnClickListener(
                view -> DataCollectionActivityPermissionsDispatcher
                        .startMetaMotionServiceWithPermissionCheck(this)
        );
        btnStopMetaMotionService.setOnClickListener(view -> stopMetaMotionService());
        btnWipeDb.setOnClickListener(view -> WipeDatabase.wipe(this, Constants.ROOM_DB_NAME));
    }

    private void fillDeviceMacAddressEditTextFromSharedPrefs() {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        String macAddress = sharedPref.getString(Constants.DEVICE_MAC_ADDRESS_SHARED_PREFS_KEY, "");
        etDeviceMacAddress.setText(macAddress);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("DataCollectionActivity()", "onResume()");
        if (isMetaMotionServiceRunning())
            bindToMetaMotionService();
    }

    @Override
    public void onPause() {
        Log.i("DataCollectionActivity()", "onPause()");
        unbindMetaMotionService();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        Log.i("DataCollectionActivity()", "onDestroy()");
        localBroadcastManager.unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }

    private boolean isMetaMotionServiceRunning() {
        isMetaMotionServiceAlive = false;
        // tricky, below broadcast is synchronized. It will set isMetaMotionServiceAlive=true
        // if the service is alive i.e. the service responds to this broadcast
        localBroadcastManager.sendBroadcastSync(new Intent(BroadcastMsgs.CHECK_METAMOTION_SERVICE_ALIVE));
        Log.i("DataCollectionActivity", "isMetaMotionServiceRunning() " + isMetaMotionServiceAlive);
        return isMetaMotionServiceAlive;
    }

    private void unbindMetaMotionService() {
        if (metaMotionServiceConnection != null) {
            Log.i("DataCollectionActivity()", "unbindMetaMotionService() unbinding");
            unbindService(metaMotionServiceConnection);
            metaMotionServiceConnection = null;
        }
        isMetaMotionServiceAlive = false;
    }

    private void handleMsgFromService(String msg) {
        Log.i("DataCollectionActivity", "handleMsgFromService() MSG: " + msg);
        switch (msg) {
            case BroadcastMsgs.METAMOTION_SERIVCE_TERMINATION:
                onMetaMotionServiceTermination();
                break;
        }
    }

    private void onMetaMotionServiceTermination() {
        Log.i("DataCollectionActivity", "onMetaMotionServiceTermination()");
        unbindMetaMotionService();
    }

    @NeedsPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    public void startMetaMotionService() {
        if (isMetaMotionServiceRunning()) {
            Toast.makeText(this, "Service is already running", Toast.LENGTH_SHORT).show();
            return;
        }

        String deviceMacAddress = etDeviceMacAddress.getText().toString();
        if (deviceMacAddress.isEmpty()) {
            Toast.makeText(this, "Provide device mac address", Toast.LENGTH_LONG).show();
            return;
        }

        storeDeviceMacAddressToSharedPrefs(deviceMacAddress);

        Toast.makeText(
                this,
                "Attempting connection with MetaWear device, please wait",
                Toast.LENGTH_LONG
        ).show();

        Intent serviceIntent = new Intent(this, MetaMotionService.class);
        serviceIntent.putExtra(Constants.DEVICE_MAC_ADDRESS_KEY, deviceMacAddress);
        ContextCompat.startForegroundService(this, serviceIntent);
    }

    private void storeDeviceMacAddressToSharedPrefs(String deviceMacAddress) {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(Constants.DEVICE_MAC_ADDRESS_SHARED_PREFS_KEY, deviceMacAddress);
        editor.apply();
    }

    private void bindToMetaMotionService() {
        Intent serviceIntent = new Intent(this, MetaMotionService.class);
        metaMotionServiceConnection = new MetaMotionServiceConnection();
        bindService(serviceIntent, metaMotionServiceConnection, Context.BIND_AUTO_CREATE);
    }

    private void stopMetaMotionService() {
        Log.i("DataCollectionActivity", "stopMetaMotionService()");
        Intent serviceIntent = new Intent(this, MetaMotionService.class);
        unbindMetaMotionService();
        stopService(serviceIntent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // NOTE: delegate the permission handling to generated method
        DataCollectionActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @OnShowRationale(Manifest.permission.ACCESS_FINE_LOCATION)
    public void showRationaleForBackgroundLocation(PermissionRequest request) {
        showRationaleDialog(R.string.permissions_dialog_rationale, request);
    }

    private void showRationaleDialog(@StringRes int msgResId, PermissionRequest request) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder
                .setPositiveButton(R.string.permissions_dialog_allow, (dialog, which) -> request.proceed())
                .setNegativeButton(R.string.permissions_dialog_deny, (dialog, which) -> request.cancel())
                .setCancelable(false)
                .setMessage(msgResId)
                .show();
    }

    @OnPermissionDenied(Manifest.permission.ACCESS_FINE_LOCATION)
    public void onBackgroundLocationDenied() {
        Toast.makeText(this, "Background location denied", Toast.LENGTH_LONG).show();
    }

    @OnNeverAskAgain(Manifest.permission.ACCESS_FINE_LOCATION)
    public void onCameraNeverAskAgain() {
        Toast.makeText(
                this,
                "App will never ask again for background location permissions",
                Toast.LENGTH_SHORT
        ).show();
    }
}
