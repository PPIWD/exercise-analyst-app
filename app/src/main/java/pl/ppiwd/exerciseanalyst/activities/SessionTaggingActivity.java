package pl.ppiwd.exerciseanalyst.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Optional;

import pl.ppiwd.exerciseanalyst.R;
import pl.ppiwd.exerciseanalyst.common.Constants;

public class SessionTaggingActivity extends AppCompatActivity {
    private Spinner activitySpinner;
    private EditText numberOfRepetitionsInput;
    private Button startSessionButton;

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
        setContentView(R.layout.activity_session_tagging);

        initialize();
    }

    @Override
    protected void onStart() {
        super.onStart();
        setResult(RESULT_CANCELED, null);
    }

    private void initialize() {
        activitySpinner = findViewById(R.id.activitySpinner);
        ArrayAdapter<String> activitiesArrayAdapter = new ArrayAdapter<String>(SessionTaggingActivity.this, android.R.layout.simple_spinner_item, activities);
        activitiesArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        activitySpinner.setAdapter(activitiesArrayAdapter);

        numberOfRepetitionsInput = findViewById(R.id.numberOfRepetitionsInput);

        startSessionButton = findViewById(R.id.startSessionButton);
        startSessionButton.setOnClickListener(view -> startSession());
    }

    private void startSession() {
        final Optional<Integer> maybeNumberOfRepetitions = convertNumberOfRepetitionsFromInput();
        if (!maybeNumberOfRepetitions.isPresent()) {
            alertAboutBadNumberOfRepetitions();
            return;
        }

        final int numberOfRepetitions = maybeNumberOfRepetitions.get();
        final String currentActivity = activitySpinner.getSelectedItem().toString();

        Intent data = new Intent();
        data.putExtra(Constants.ACTIVITY_NAME_KEY, currentActivity);
        data.putExtra(Constants.REPETITIONS_COUNT_KEY, numberOfRepetitions);

        setResult(RESULT_OK, data);
        finish();
    }

    private Optional<Integer> convertNumberOfRepetitionsFromInput() {
        final Editable input = numberOfRepetitionsInput.getText();
        if (input == null) {
            return Optional.empty();
        }

        Integer numberOfRepetitions;
        try {
            numberOfRepetitions = Integer.valueOf(input.toString());
        } catch (NumberFormatException e) {
            return Optional.empty();
        }

        if (numberOfRepetitions == 0) {
            return Optional.empty();
        }

        return Optional.of(numberOfRepetitions);
    }

    private void alertAboutBadNumberOfRepetitions() {
        Toast.makeText(this, "A number of repetitions must be a positive integer number", Toast.LENGTH_LONG).show();
    }
}