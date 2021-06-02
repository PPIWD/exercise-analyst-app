package pl.ppiwd.exerciseanalyst.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Optional;

import pl.ppiwd.exerciseanalyst.R;
import pl.ppiwd.exerciseanalyst.common.Constants;

public class SessionTaggingActivity extends AppCompatActivity {
    private Spinner activitySpinner;
    private NumberPicker repetitionsPicker;

    private static final String[] activities = new String[]{
            "Walking",
            "Frontal elevation of arms",
            "Jump front & back",
            "Squats",
            "Push ups"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_init);
        initialize();
    }

    @Override
    protected void onStart() {
        super.onStart();
        setResult(RESULT_CANCELED, null);
    }

    private void initialize() {
        activitySpinner = findViewById(R.id.activitySpinner);
        ArrayAdapter<String> activitiesArrayAdapter = new ArrayAdapter<>(SessionTaggingActivity.this, android.R.layout.simple_spinner_item, activities);
        activitiesArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        activitySpinner.setAdapter(activitiesArrayAdapter);
        createRepetitionsPicker();

        findViewById(R.id.startSessionButton).setOnClickListener(view -> startSession());
    }

    private void createRepetitionsPicker() {
        this.repetitionsPicker = (NumberPicker) findViewById(R.id.repetitionsPicker);
        this.repetitionsPicker.setMinValue(1);
        this.repetitionsPicker.setMaxValue(25);
        this.repetitionsPicker.setValue(5);
    }

    private void startSession() {
        final int repetitions = repetitionsPicker.getValue();
        final String activity = activitySpinner.getSelectedItem().toString();

        Intent data = new Intent();
        data.putExtra(Constants.ACTIVITY_NAME_KEY, activity);
        data.putExtra(Constants.REPETITIONS_COUNT_KEY, repetitions);

        setResult(RESULT_OK, data);
        Log.i("SessionStarting", "Starting session for: " + activity + ", repetitions: " + repetitions);
        finish();
    }
}