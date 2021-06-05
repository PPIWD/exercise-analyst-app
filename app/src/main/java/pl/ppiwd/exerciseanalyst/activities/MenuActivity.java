package pl.ppiwd.exerciseanalyst.activities;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import pl.ppiwd.exerciseanalyst.R;
import pl.ppiwd.exerciseanalyst.activities.utils.BluetoothDeviceScanner;
import pl.ppiwd.exerciseanalyst.common.BroadcastMsgs;
import pl.ppiwd.exerciseanalyst.common.Constants;

public class MenuActivity extends AppCompatActivity {

    private Button gatherDataButton;
    private Button openWebViewButton;
    private boolean isDebugActive;
    private Button startTrainingButton;
    private CardView cardViewDeviceDetails;
    private TextView deviceNameTextView;
    private TextView deviceMacTextView;
    private TextView deviceStatus;
    private ProgressBar pbDeviceScan;
    private BluetoothDeviceScanner blHandler;
    private String deviceMac = "";
    private ActivityResultLauncher<Intent> btServiceLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Log.i("MenuActivity", "User enabled the bluetooth service");
                    startBtScanner();
                } else {
                    Log.e("MenuActivity", "User didn't enable the bluetooth service");
                }
            });

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
        deviceNameTextView = findViewById(R.id.tv_device_name);
        deviceMacTextView = findViewById(R.id.tv_mac_address);
        deviceStatus = findViewById(R.id.tv_device_status);
        cardViewDeviceDetails = findViewById(R.id.cv_device_status);
        pbDeviceScan = findViewById(R.id.pb_scan_mode);

        startTrainingButton.setOnClickListener(view -> openTrainingView(Constants.TRAINING_REGULAR));
        gatherDataButton.setOnClickListener(view -> openTrainingView(Constants.TRAINING_DATA_GATHERING));
        openWebViewButton.setOnClickListener(view -> openWebViewStats());

        cardViewDeviceDetails.setOnClickListener(view -> handleDeviceCardClick());
        invalidateDeviceStats();
    }

    private void openTrainingView(String serviceUrl) {
        Intent trainingActivity = new Intent(this, TrainingDataActivity.class);
        trainingActivity.putExtra(Constants.DEVICE_MAC_ADDRESS_SHARED_PREFS_KEY, this.deviceMac);
        trainingActivity.putExtra(Constants.TRAINING_TYPE, serviceUrl);
        startActivity(trainingActivity);
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

    private void handleDeviceCardClick() {
        if(blHandler != null) {
            blHandler.close();
            invalidateDeviceStats();
            blHandler = null;
        } else {
            initializeBtService();
        }
    }

    private void startBtScanner() {
        deviceStatus.setText("Scanning, tap to cancel");
        pbDeviceScan.setVisibility(View.VISIBLE);

        blHandler = new BluetoothDeviceScanner(this, (deviceMac) -> {
            if(deviceMac != null) {
                Log.i("BluetoothDeviceScanner", deviceMac);
                this.deviceMac = deviceMac;
                showDeviceStats("MetaWear", deviceMac);
            } else {
                // We timed out while waiting for nearby devices.
                invalidateDeviceStats();
            }
        });
    }

    private void initializeBtService() {
        Log.i("MenuActivity", "initializeBtService");
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        btServiceLauncher.launch(enableBtIntent);
    }

    private void showDeviceStats(String name, String mac) {
        this.deviceStatus.setText("Wearable available");
        pbDeviceScan.setVisibility(View.GONE);
        this.deviceNameTextView.setText(name);
        this.deviceNameTextView.setVisibility(View.VISIBLE);
        this.deviceMacTextView.setText(mac);
        this.deviceMacTextView.setVisibility(View.VISIBLE);
        startTrainingButton.setEnabled(true);
        gatherDataButton.setEnabled(true);
    }

    private void invalidateDeviceStats() {
        Log.i("MenuActivity", "invalidateDeviceStats");
        deviceMacTextView.setVisibility(View.GONE);
        deviceNameTextView.setVisibility(View.GONE);
        pbDeviceScan.setVisibility(View.GONE);
        startTrainingButton.setEnabled(false);
        gatherDataButton.setEnabled(false);
        deviceStatus.setText("Tap to scan for devices");
    }
}
