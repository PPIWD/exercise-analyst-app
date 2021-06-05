package pl.ppiwd.exerciseanalyst.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import pl.ppiwd.exerciseanalyst.R;
import pl.ppiwd.exerciseanalyst.common.auth.EmptyFrontendCallback;
import pl.ppiwd.exerciseanalyst.common.auth.FrontendCallback;
import pl.ppiwd.exerciseanalyst.common.auth.SignInCredentials;
import pl.ppiwd.exerciseanalyst.common.auth.UserAccountManager;
import pl.ppiwd.exerciseanalyst.utils.DataValidator;

public class SignInActivity extends AppCompatActivity {

    private EditText etEmail;
    private EditText etPassword;
    private Button btnSignIn;
    private UserAccountManager userAccountManager;
    private final FrontendCallback frontendCallback = new EmptyFrontendCallback() {
        @Override
        public void onSignInSuccess() {
            Toast.makeText(SignInActivity.this, "Sign In successful", Toast.LENGTH_SHORT).show();
            switchToDataCollectionActivity();
        }

        @Override
        public void onSignInFailed() {
            Toast.makeText(SignInActivity.this, "Sign In failed", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        initViews();
        userAccountManager = new UserAccountManager(this, frontendCallback);
    }

    private void initViews() {
        etEmail = findViewById(R.id.et_email_sign_in);
        etPassword = findViewById(R.id.et_password_sign_in);
        btnSignIn = findViewById(R.id.btn_sign_in);

        btnSignIn.setOnClickListener(v -> this.signIn());
    }

    private void signIn() {
        String email = etEmail.getText().toString();
        if(!DataValidator.isEmailValid(email)) {
            Toast.makeText(this, "Email address invalid", Toast.LENGTH_SHORT).show();
            return;
        }

        String password = etPassword.getText().toString();
        if(password.isEmpty()) {
            Toast.makeText(this, "Password can't be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        SignInCredentials credentials = new SignInCredentials(email, password);
        userAccountManager.signIn(credentials);
    }

    private void switchToDataCollectionActivity() {
        Intent menuActivity = new Intent(this, MenuActivity.class);
        menuActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(menuActivity);
        finish();
    }
}