package pl.ppiwd.exerciseanalyst.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import pl.ppiwd.exerciseanalyst.R;
import pl.ppiwd.exerciseanalyst.common.auth.EmptyFrontendCallback;
import pl.ppiwd.exerciseanalyst.common.auth.FrontendCallback;
import pl.ppiwd.exerciseanalyst.common.auth.SignUpData;
import pl.ppiwd.exerciseanalyst.common.auth.UserAccountManager;
import pl.ppiwd.exerciseanalyst.utils.DataValidator;

public class SignUpActivity extends AppCompatActivity {

    private TextView tvHeight;
    private EditText etHeight;
    private TextView tvWeight;
    private EditText etWeight;
    private TextView tvAge;
    private EditText etAge;
    private TextView tvGender;
    private Spinner spinnerGender;
    private Button btnNext;
    private TextView tvEmail;
    private EditText etEmail;
    private TextView tvPassword;
    private EditText etPassword;
    private TextView tvPasswordRepeat;
    private EditText etPasswordRepeat;
    private Button btnSignUp;
    private Button btnPrevious;

    private View[] firstGroup;
    private View[] secondGroup;

    private UserAccountManager userAccountManager;
    private final FrontendCallback frontendCallback = new EmptyFrontendCallback() {
        @Override
        public void onSignUpSuccess() {
            Toast.makeText(SignUpActivity.this, "Sign Up successful", Toast.LENGTH_SHORT).show();
            switchToDataCollectionActivity();
        }

        @Override
        public void onSignUpFailed() {
            Toast.makeText(SignUpActivity.this, "Sign Up failed", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        initViews();
        userAccountManager = new UserAccountManager(this, frontendCallback);
    }

    private void initViews() {
        tvHeight = findViewById(R.id.tv_height_sign_up);
        etHeight = findViewById(R.id.et_height_sign_up);
        tvWeight = findViewById(R.id.tv_weight_sign_up);
        etWeight = findViewById(R.id.et_weight_sign_up);
        tvAge = findViewById(R.id.tv_age_sign_up);
        etAge = findViewById(R.id.et_age_sign_up);
        tvGender = findViewById(R.id.tv_gender_sign_up);
        spinnerGender = findViewById(R.id.spinner_gender_sign_up);
        btnNext = findViewById(R.id.btn_next_sign_up);
        btnNext.setOnClickListener(v -> this.onNextClick());

        String[] gender_items = getResources().getStringArray(R.array.gender_items);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                gender_items
        );
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGender.setAdapter(dataAdapter);

        tvEmail = findViewById(R.id.tv_email_sign_up);
        etEmail = findViewById(R.id.et_email_sign_up);
        tvPassword = findViewById(R.id.tv_password_sign_up);
        etPassword = findViewById(R.id.et_password_sign_up);
        tvPasswordRepeat = findViewById(R.id.tv_password_repeat_sign_up);
        etPasswordRepeat = findViewById(R.id.et_password_repeat_sign_up);
        btnSignUp = findViewById(R.id.btn_sign_up);
        btnPrevious = findViewById(R.id.btn_previous_sign_up);

        btnSignUp.setOnClickListener(v -> this.signUp());
        btnPrevious.setOnClickListener(v -> this.onPreviousClick());

        firstGroup = new View[]{tvHeight, etHeight, tvWeight, etWeight, tvAge, etAge,
                                tvGender, spinnerGender, btnNext};

        secondGroup = new View[]{tvEmail, etEmail, tvPassword, etPassword, tvPasswordRepeat,
                                 etPasswordRepeat, btnSignUp, btnPrevious};

        hideGroup(secondGroup);
        showGroup(firstGroup);
    }

    private void showGroup(View[] group) {
        for(View view : group)
            view.setVisibility(View.VISIBLE);
    }

    private void hideGroup(View[] group) {
        for(View view : group)
            view.setVisibility(View.GONE);
    }

    private void onNextClick() {
        hideGroup(firstGroup);
        showGroup(secondGroup);
    }

    private void onPreviousClick() {
        hideGroup(secondGroup);
        showGroup(firstGroup);
    }

    private void signUp() {
        int height;
        try{
            height = Integer.parseInt(etHeight.getText().toString());
        }catch(NumberFormatException e) {
            Toast.makeText(this, "Invalid height format", Toast.LENGTH_SHORT).show();
            return;
        }

        double weight;
        try{
            weight = Double.parseDouble(etWeight.getText().toString());
        }catch(NumberFormatException e) {
            Toast.makeText(this, "Invalid weight format", Toast.LENGTH_SHORT).show();
            return;
        }

        int age;
        try{
            age = Integer.parseInt(etAge.getText().toString());
        }catch(NumberFormatException e) {
            Toast.makeText(this, "Invalid age format", Toast.LENGTH_SHORT).show();
            return;
        }

        String gender = spinnerGender.getSelectedItem().toString();
        String email = etEmail.getText().toString();
        if(!DataValidator.isEmailValid(email)) {
            Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show();
            return;
        }

        String password = etPassword.getText().toString();
        String repeatedPassword = etPasswordRepeat.getText().toString();
        if(password.isEmpty() || repeatedPassword.isEmpty() || !password.equals(repeatedPassword)) {
            Toast.makeText(this, "Password empty or passwords don't match", Toast.LENGTH_SHORT).show();
            return;
        }

        SignUpData data = new SignUpData(email, password, height, weight, age, gender);
        userAccountManager.signUp(data);
    }

    private void switchToDataCollectionActivity() {
        Intent dataCollectionActivityIntent = new Intent(this, MenuActivity.class);
        dataCollectionActivityIntent.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(dataCollectionActivityIntent);
        finish();
    }
}