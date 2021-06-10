package pl.ppiwd.exerciseanalyst.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import pl.ppiwd.exerciseanalyst.R;
import pl.ppiwd.exerciseanalyst.common.Constants;
import pl.ppiwd.exerciseanalyst.common.auth.UserAccountManager;

public class InitialActivity extends AppCompatActivity {

    private Button btnSignIn;
    private Button btnSignUp;
    private boolean shouldWipeToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial);

        initViews();
        shouldWipeToken = false;
        Intent intent = getIntent();
        if(intent != null) {
            shouldWipeToken = intent.getBooleanExtra(Constants.SHOULD_WIPE_ACCESS_TOKEN_KEY, false);
        }
    }

    private void initViews() {
        btnSignIn = findViewById(R.id.btn_sign_in_initial);
        btnSignUp = findViewById(R.id.btn_sign_up_initial);

        btnSignIn.setOnClickListener(v -> this.signIn());
        btnSignUp.setOnClickListener(v -> this.signUp());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(shouldWipeToken)
            UserAccountManager.wipeToken(this);
        else if(UserAccountManager.isSignedIn(this))
            switchToDataCollectionActivity();
    }

    private void switchToDataCollectionActivity() {
        Intent dataCollectionActivityIntent = new Intent(this, MenuActivity.class);
        startActivity(dataCollectionActivityIntent);
        finish();
    }

    private void signIn() {
        Intent signInActivity = new Intent(this, SignInActivity.class);
        startActivity(signInActivity);
    }

    private void signUp() {
        Intent signUpActivity = new Intent(this, SignUpActivity.class);
        startActivity(signUpActivity);
    }
}