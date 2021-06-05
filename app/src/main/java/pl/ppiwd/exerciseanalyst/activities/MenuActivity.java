package pl.ppiwd.exerciseanalyst.activities;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import pl.ppiwd.exerciseanalyst.R;
import pl.ppiwd.exerciseanalyst.activities.utils.BluetoothDeviceScanner;
import pl.ppiwd.exerciseanalyst.common.BroadcastMsgs;
import pl.ppiwd.exerciseanalyst.common.Constants;
import pl.ppiwd.exerciseanalyst.utils.WipeDatabase;

public class MenuActivity extends AppCompatActivity {

    private Button gatherDataButton;
    private Button openWebViewButton;
    private boolean isDebugActive;
    private Button startTrainingButton;
    private CardView cardViewSessionDetails;
    private CardView cardViewDeviceDetails;
    private TextView deviceName;
    private TextView deviceMacUi;
    private TextView deviceStatus;
    private ProgressBar pbDeviceScan;
    private BluetoothDeviceScanner blHandler;
    private String deviceMac;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.isDebugActive = false;
        super.onCreate(savedInstanceState);
        Log.i("MenuActivity", "onCreate()");
        setContentView(R.layout.activity_main);
        initViews();
        init();
    }

    private void init() {
        Log.i("MenuActivity", "init()");
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BroadcastMsgs.METAMOTION_SERVICE_ALIVE_RESP);
        intentFilter.addAction(BroadcastMsgs.ACTIVITY_BROADCAST_INTENT_ACTION);
    }

    private void initViews() {
        gatherDataButton = findViewById(R.id.btn_start_meta_motion);
        openWebViewButton = findViewById(R.id.btn_open_webview);
        startTrainingButton = findViewById(R.id.btn_start_training);
        deviceName = findViewById(R.id.tv_device_name);
        deviceMacUi = findViewById(R.id.tv_mac_address);
        deviceStatus = findViewById(R.id.tv_device_status);
        cardViewDeviceDetails = findViewById(R.id.cv_device_status);
        cardViewSessionDetails = findViewById(R.id.cv_tagging_session);
        pbDeviceScan = findViewById(R.id.pb_scan_mode);

        startTrainingButton.setOnClickListener(view -> openTrainingView(Constants.TRAINING_REGULAR));
        gatherDataButton.setOnClickListener(view -> openTrainingView(Constants.TRAINING_DATA_GATHERING));
        openWebViewButton.setOnClickListener(view -> openWebViewStats());


        cardViewSessionDetails.setVisibility(View.GONE);
        cardViewDeviceDetails.setOnClickListener(view -> initiateBlScanning());
        deviceMacUi.setVisibility(View.GONE);
        deviceName.setVisibility(View.GONE);
        pbDeviceScan.setVisibility(View.GONE);
        deviceStatus.setText("Tap to scan for device");
    }

    private void openTrainingView(String serviceUrl) {
        String deviceMacAddress = "";
        if (deviceMacAddress.isEmpty()) {
            Toast.makeText(this, "Provide device mac address", Toast.LENGTH_LONG).show();
            return;
        }

        storeDeviceMacAddressToSharedPrefs(deviceMacAddress);

        Intent trainingActivity = new Intent(this, TrainingDataActivity.class);
        trainingActivity.putExtra(Constants.DEVICE_MAC_ADDRESS_SHARED_PREFS_KEY, deviceMacAddress);
        trainingActivity.putExtra(Constants.TRAINING_TYPE, serviceUrl);
        startActivity(trainingActivity);
    }

    private void storeDeviceMacAddressToSharedPrefs(String deviceMacAddress) {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(Constants.DEVICE_MAC_ADDRESS_SHARED_PREFS_KEY, deviceMacAddress);
        editor.apply();
    }

    private void openWebViewStats() {
        Intent statsIntent = new Intent(this, WebViewStats.class);
        startActivity(statsIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.data_collection_activity_menu, menu);
        MenuItem debugSwitcher = menu.findItem(R.id.debug_switch);
        debugSwitcher.setChecked(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.switch_account_data_col_act) {
            switchAccount();
            return true;
        } else if (item.getItemId() == R.id.debug_switch) {
            isDebugActive = !isDebugActive;
            item.setChecked(isDebugActive);
            if (isDebugActive) {
                gatherDataButton.setVisibility(View.VISIBLE);
                Toast.makeText(this, "Debug mode: enabled", Toast.LENGTH_LONG).show();
            } else {
                gatherDataButton.setVisibility(View.GONE);
                Toast.makeText(this, "Debug mode: disabled", Toast.LENGTH_LONG).show();
            }
        }
        Log.i("debug", "Debug");
        return super.onOptionsItemSelected(item);
    }

    private void switchAccount() {
        Intent switchAccountIntent = new Intent(this, InitialActivity.class);
        switchAccountIntent.putExtra(Constants.SHOULD_WIPE_ACCESS_TOKEN_KEY, true);
        switchAccountIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(switchAccountIntent);
        finish();
    }

    private void initiateBlScanning() {
        Log.i("MenuActivity", "initiateBlScanning()");
        deviceStatus.setText("Scanning for devices");
        pbDeviceScan.setVisibility(View.VISIBLE);
        int hasLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);

        if (hasLocationPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }

        if (!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 2);
        }

        blHandler = new BluetoothDeviceScanner(this, (deviceMac) -> {
           this.deviceMac = deviceMac;
           showDeviceStats("MetaWear", deviceMac);
        });
    }

    private void showDeviceStats(String name, String mac) {
        this.deviceStatus.setText("Wearable available");
        this.deviceName.setText(name);
        this.deviceName.setVisibility(View.VISIBLE);
        this.deviceMacUi.setText(mac);
        this.deviceName.setVisibility(View.VISIBLE);
    }
}
