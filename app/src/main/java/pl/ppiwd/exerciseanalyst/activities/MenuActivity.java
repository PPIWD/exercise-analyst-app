package pl.ppiwd.exerciseanalyst.activities;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import pl.ppiwd.exerciseanalyst.R;
import pl.ppiwd.exerciseanalyst.common.BroadcastMsgs;
import pl.ppiwd.exerciseanalyst.common.Constants;
import pl.ppiwd.exerciseanalyst.utils.WipeDatabase;

public class MenuActivity extends AppCompatActivity {

    private EditText deviceMacAddressTextView;
    private Button gatherDataButton;
    private Button wipeDbButton;
    private Button openWebViewButton;
    private boolean isDebugActive;
    private Button startTrainingButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.isDebugActive = false;
        super.onCreate(savedInstanceState);
        Log.i("DataCollectionActivity()", "onCreate()");
        setContentView(R.layout.activity_main);
        initViews();
        init();
    }

    private void init() {
        Log.i("DataCollectionActivity", "init()");
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BroadcastMsgs.METAMOTION_SERVICE_ALIVE_RESP);
        intentFilter.addAction(BroadcastMsgs.ACTIVITY_BROADCAST_INTENT_ACTION);

    }

    private void initViews() {
        deviceMacAddressTextView = findViewById(R.id.et_device_mac_address);
        gatherDataButton = findViewById(R.id.btn_start_meta_motion);
        wipeDbButton = findViewById(R.id.btn_wipe_db);
        openWebViewButton = findViewById(R.id.btn_open_webview);
        startTrainingButton = findViewById(R.id.btn_start_training);

        fillDeviceMacAddressEditTextFromSharedPrefs();

        startTrainingButton.setOnClickListener(view -> openTrainingView(Constants.TRAINING_REGULAR));
        gatherDataButton.setOnClickListener(view -> openTrainingView(Constants.TRAINING_DATA_GATHERING));
        wipeDbButton.setOnClickListener(view -> WipeDatabase.wipe(this, Constants.ROOM_DB_NAME));
        openWebViewButton.setOnClickListener(view -> openWebViewStats());
    }

    private void openTrainingView(String serviceUrl) {
        String deviceMacAddress = deviceMacAddressTextView.getText().toString().toUpperCase();
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

    private void fillDeviceMacAddressEditTextFromSharedPrefs() {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        String macAddress = sharedPref.getString(Constants.DEVICE_MAC_ADDRESS_SHARED_PREFS_KEY, "");
        deviceMacAddressTextView.setText(macAddress);
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
                wipeDbButton.setVisibility(View.VISIBLE);
                gatherDataButton.setVisibility(View.VISIBLE);
                Toast.makeText(this, "Debug mode: enabled", Toast.LENGTH_LONG).show();
            } else {
                wipeDbButton.setVisibility(View.GONE);
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

}
