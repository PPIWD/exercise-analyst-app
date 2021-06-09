package pl.ppiwd.exerciseanalyst.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import androidx.core.content.ContextCompat;
import pl.ppiwd.exerciseanalyst.BuildConfig;
import pl.ppiwd.exerciseanalyst.R;
import pl.ppiwd.exerciseanalyst.activities.utils.ServerConnection;
import pl.ppiwd.exerciseanalyst.common.Constants;
import pl.ppiwd.exerciseanalyst.common.session.DeviceConnectionServiceChecker;
import pl.ppiwd.exerciseanalyst.persistence.MeasurementsDatabase;
import pl.ppiwd.exerciseanalyst.persistence.dao.MeasurementsDao;
import pl.ppiwd.exerciseanalyst.services.Timer;
import pl.ppiwd.exerciseanalyst.services.metamotion.MetaMotionService;

public class TrainingDataActivity extends AppCompatActivity {
    private Spinner activitySpinner;
    private NumberPicker repetitionsPicker;
    private Button startStopButton;
    private Timer timer;
    private Handler timerHandler;
    private TextView timerTextView;

    private DeviceConnectionServiceChecker deviceConnectionServiceChecker;
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
        this.deviceConnectionServiceChecker = new DeviceConnectionServiceChecker(this);
        configureButton(deviceConnectionServiceChecker.isServiceRunning());
    }

    private ServerConnection getServerConnection() {
        MeasurementsDao measurements = MeasurementsDatabase.getInstance(getApplicationContext()).measurementsDao();
        return new ServerConnection(measurements, this);
    }

    private Spinner getSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(TrainingDataActivity.this, android.R.layout.simple_spinner_item, activities);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner spinner = findViewById(R.id.activitySpinner);
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
    protected void onDestroy() {
        super.onDestroy();
        deviceConnectionServiceChecker.unregister();
        serverConnection.cancelRequest();

        //isFinishing() is true, when activity view is really being closed and not when e.g. screen is being rotated
        if (isFinishing()) {
            Toast.makeText(this, "Unfinished trainings dropped", Toast.LENGTH_SHORT).show();
            stopService(deviceService);
        }
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
        NumberPicker picker = findViewById(R.id.repetitionsPicker);
        picker.setMinValue(1);
        picker.setMaxValue(25);
        picker.setValue(5);
        return picker;
    }

    private void handleSession() {
        timer.reset();
        timerHandler.post(timer);
        startMetaMotionService();
        configureButton(true);
    }

    private void configureButton(boolean isTrainingActive) {
        if (isTrainingActive) {
            startStopButton.setOnClickListener(v -> {
                timerHandler.removeCallbacks(timer);
                stopService(deviceService);
                sendDataToServer();
                configureButton(false);
            });
            startStopButton.setText(R.string.finish_training);
            activitySpinner.setEnabled(false);
            repetitionsPicker.setEnabled(false);
        } else {
            startStopButton.setOnClickListener(view -> handleSession());
            startStopButton.setText(R.string.start_training);
            activitySpinner.setEnabled(true);
            repetitionsPicker.setEnabled(true);
        }
    }

    public void startMetaMotionService() {
        if (deviceConnectionServiceChecker.isServiceRunning()) {
            Toast.makeText(this, "Service is already running", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "Wait for connection with device...", Toast.LENGTH_LONG).show();

        deviceService.putExtra(Constants.DEVICE_MAC_ADDRESS_KEY, getIntent().getStringExtra(Constants.DEVICE_MAC_ADDRESS_SHARED_PREFS_KEY));
        deviceService.putExtra(Constants.ACTIVITY_NAME_KEY, activitySpinner.getSelectedItem().toString());
        deviceService.putExtra(Constants.REPETITIONS_COUNT_KEY, repetitionsPicker.getValue());

        ContextCompat.startForegroundService(this,deviceService);
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
        builder.setMessage("Training finished. Do you want to save training data?")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener)
                .show();
    }
}
