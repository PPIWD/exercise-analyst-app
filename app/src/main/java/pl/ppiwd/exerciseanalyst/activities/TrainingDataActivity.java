package pl.ppiwd.exerciseanalyst.activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;
import pl.ppiwd.exerciseanalyst.BuildConfig;
import pl.ppiwd.exerciseanalyst.R;
import pl.ppiwd.exerciseanalyst.activities.utils.MetaMotionServiceConnection;
import pl.ppiwd.exerciseanalyst.activities.utils.ServerConnection;
import pl.ppiwd.exerciseanalyst.common.Constants;
import pl.ppiwd.exerciseanalyst.common.session.DeviceConnectionChecker;
import pl.ppiwd.exerciseanalyst.persistence.MeasurementsDatabase;
import pl.ppiwd.exerciseanalyst.persistence.dao.MeasurementsDao;
import pl.ppiwd.exerciseanalyst.services.Timer;
import pl.ppiwd.exerciseanalyst.services.metamotion.MetaMotionService;

@RuntimePermissions
public class TrainingDataActivity extends AppCompatActivity {
    private Spinner activitySpinner;
    private NumberPicker repetitionsPicker;
    private Button startStopButton;
    private Timer timer;
    private Handler timerHandler;
    private TextView timerTextView;

    private DeviceConnectionChecker deviceConnectionChecker;
    private MetaMotionServiceConnection metaMotionServiceConnection;
    private ServerConnection serverConnection;
    private static final String[] activities = new String[]{
            "Walking",
            "Frontal elevation of arms",
            "Jump front & back",
            "Squats",
            "Push ups"
    };
    private Intent deviceService;

    private void initialize() {
        this.timerHandler = new Handler();
        this.timerTextView = findViewById(R.id.timerTextView);
        this.timer = new Timer(timerTextView, timerHandler);
        this.startStopButton = findViewById(R.id.startSessionButton);
        this.activitySpinner = getSpinner();
        this.repetitionsPicker = getPicker();
        this.startStopButton.setOnClickListener(view -> handleSession());

        if (getIntent().getStringExtra(Constants.TRAINING_TYPE).equals(Constants.TRAINING_REGULAR)) {
            findViewById(R.id.activityTextView).setVisibility(View.INVISIBLE);
            findViewById(R.id.repetitionsTextView).setVisibility(View.INVISIBLE);
            activitySpinner.setVisibility(View.INVISIBLE);
            repetitionsPicker.setVisibility(View.INVISIBLE);
        } else {
            findViewById(R.id.backgroundImage).setVisibility(View.INVISIBLE);
        }

        this.deviceService = new Intent(this, MetaMotionService.class);
        this.serverConnection = getServerConnection();
        this.deviceConnectionChecker = new DeviceConnectionChecker(this, metaMotionServiceConnection);
    }

    private ServerConnection getServerConnection() {
        MeasurementsDao measurements = MeasurementsDatabase.getInstance(getApplicationContext()).measurementsDao();
        return new ServerConnection(measurements, this);
    }

    private Spinner getSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(TrainingDataActivity.this, android.R.layout.simple_spinner_item, activities);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner spinner = (Spinner) findViewById(R.id.activitySpinner);
        spinner.setAdapter(adapter);
        return spinner;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);
        initialize();
    }

    @Override
    protected void onStart() {
        super.onStart();
        setResult(RESULT_CANCELED, null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (deviceConnectionChecker.isConnected()) {
            bindToMetaMotionService();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        finishSession();
        deviceConnectionChecker.unregister();
        serverConnection.cancelRequest();
        Toast.makeText(this, "Unfinished trainings dropped", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        if (startStopButton.getText().toString().equals(getString(R.string.finish_training))) {
            Toast.makeText(getApplicationContext(), "Finish training before going back!", Toast.LENGTH_LONG).show();
        } else {
            finish();
        }
    }

    private NumberPicker getPicker() {
        NumberPicker picker = (NumberPicker) findViewById(R.id.repetitionsPicker);
        picker.setMinValue(1);
        picker.setMaxValue(25);
        picker.setValue(5);
        return picker;
    }

    private void handleSession() {
        timer.reset();
        timerHandler.post(timer);
        startMetaMotionService();
        startStopButton.setText(R.string.finish_training);
        activitySpinner.setEnabled(false);
        repetitionsPicker.setEnabled(false);
        startStopButton.setOnClickListener(v -> {
            timerHandler.removeCallbacks(timer);

            finishSession();
            sendDataToServer();
        });
    }

    @NeedsPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    public void startMetaMotionService() {
        if (deviceConnectionChecker.isConnected()) {
            Toast.makeText(this, "Service is already running", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "Wait for connection with device...", Toast.LENGTH_LONG).show();

        deviceService.putExtra(Constants.DEVICE_MAC_ADDRESS_KEY, getIntent().getStringExtra(Constants.DEVICE_MAC_ADDRESS_SHARED_PREFS_KEY));
        deviceService.putExtra(Constants.ACTIVITY_NAME_KEY, activitySpinner.getSelectedItem().toString());
        deviceService.putExtra(Constants.REPETITIONS_COUNT_KEY, repetitionsPicker.getValue());

        startService(deviceService);
        bindToMetaMotionService();
    }

    private void bindToMetaMotionService() {
        Intent serviceIntent = new Intent(this, MetaMotionService.class);
        metaMotionServiceConnection = new MetaMotionServiceConnection();
        bindService(serviceIntent, metaMotionServiceConnection, Context.BIND_AUTO_CREATE);
    }

    private void finishSession() {
        unbindMetaMotionService();
        stopService(deviceService);

        startStopButton.setOnClickListener(view -> handleSession());
        startStopButton.setText(R.string.start_training);
        activitySpinner.setEnabled(true);
        repetitionsPicker.setEnabled(true);
    }

    private void unbindMetaMotionService() {
        if (metaMotionServiceConnection != null) {
            Log.i("DataCollectionActivity()", "unbindMetaMotionService() unbinding");
            unbindService(metaMotionServiceConnection);
            metaMotionServiceConnection = null;
        }
    }

    private void sendDataToServer() {
        DialogInterface.OnClickListener dialogClickListener = (dialog, clickedButton) -> {
            String trainingType = getIntent().getStringExtra(Constants.TRAINING_TYPE);
            if (clickedButton == DialogInterface.BUTTON_POSITIVE) {
                if (trainingType.equals(Constants.TRAINING_DATA_GATHERING)) {
                    serverConnection.sendModelTrainingData(BuildConfig.GATHER_DATA_URL);
                } else if (trainingType.equals(Constants.TRAINING_REGULAR)) {
                    //todo
                    // This is the method which sends data to url which should recognize activity.
                    // Sent data shouldn't contain info about repetitions and activity.
                    // Currently it does and it should be changed.
                    // (Possibly server will simply not read additional fields and it might work fine)
                    serverConnection.sendModelTrainingData(BuildConfig.RECOGNIZE_DATA_URL);
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Training finished. Do you want to save gathered data?")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener)
                .show();
    }

    //Permissions handling below...
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // NOTE: delegate the permission handling to generated method
        TrainingDataActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
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

    @OnShowRationale(Manifest.permission.ACCESS_FINE_LOCATION)
    public void showRationaleForBackgroundLocation(PermissionRequest request) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder
                .setPositiveButton(R.string.permissions_dialog_allow, (dialog, which) -> request.proceed())
                .setNegativeButton(R.string.permissions_dialog_deny, (dialog, which) -> request.cancel())
                .setCancelable(false)
                .setMessage(R.string.permissions_dialog_rationale)
                .show();
    }
}
