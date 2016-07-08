package com.texas.easilendar.ui.outsides;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.texas.easilendar.R;
import com.texas.easilendar.ui.calendars.MonthCalendarActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {
    @BindView(R.id.loginAppName) TextView loginAppName;
    @BindView(R.id.loginEmail) EditText loginEmail;
    @BindView(R.id.loginPassword) EditText loginPassword;
    @BindView(R.id.loginProgressBar) ProgressBar loginProgressBar;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        setAppNameFont();

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        specifyInputMethodAction();
    }

    private void setAppNameFont() {
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/rotulona-hand.ffp.otf");
        loginAppName.setTypeface(font);
    }

    private void specifyInputMethodAction() {
        loginPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    login();
                    handled = true;
                }
                return handled;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loginProgressBar.setVisibility(View.GONE);
    }

    @OnClick(R.id.loginSubmit) void login() {
        // Hide the keyboard
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        try {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            Log.d("LoginActivity", "login: login without any focus");
        }

        // get EditText
        String mEmail = loginEmail.getText().toString().trim();
        String mPassword = loginPassword.getText().toString().trim();

        // Check valid
        if (TextUtils.isEmpty(mEmail)) {
            loginEmail.requestFocus();
            loginEmail.setError(getResources().getString(R.string.login_error_field_required));
            return;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(mEmail).matches()) {
            loginEmail.requestFocus();
            loginEmail.setError(getResources().getString(R.string.login_error_invalid_email));
            return;
        } else if (TextUtils.isEmpty(mPassword)) {
            loginPassword.requestFocus();
            loginPassword.setError(getResources().getString(R.string.login_error_field_required));
            return;
        } else if (mPassword.length() < 6) {
            loginPassword.requestFocus();
            loginPassword.setError(getResources().getString(R.string.login_error_too_short_password));
            return;
        }

        loginProgressBar.setVisibility(View.VISIBLE);
        // login with password and email
        // authenticate user
        auth.signInWithEmailAndPassword(mEmail, mPassword)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        loginProgressBar.setVisibility(View.GONE);

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            // there was an error
                            if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                                loginEmail.requestFocus();
                                loginEmail.setError(getResources().getString(R.string.login_error_not_exist_email));
                            } else if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                loginPassword.requestFocus();
                                loginPassword.setError(getResources().getString(R.string.login_error_incorrect_password));
                            } else {
                                Toast.makeText(LoginActivity.this,
                                        getResources().getString(R.string.login_error_unknown),
                                        Toast.LENGTH_LONG).show();
                            }
                        } else {
                            // TODO login complete download all data save to SQLite table
                            // Set local status to logged in
                            Toast.makeText(LoginActivity.this,
                                    getResources().getString(R.string.login_success),
                                    Toast.LENGTH_SHORT).show();

                            startActivity(new Intent(LoginActivity.this, MonthCalendarActivity.class));
                            finish();
                        }
                    }
                });
    }

    @OnClick(R.id.loginAnonymous) void loginAnonymous() {
        // TODO login Anonymous
        Toast.makeText(LoginActivity.this, "Login Anonymous, coming soon!", Toast.LENGTH_LONG).show();
    }

    @OnClick(R.id.loginFacebook) void loginFacebook() {
        // TODO login with Facebook
        Toast.makeText(LoginActivity.this, "Login with Facebook, coming soon!", Toast.LENGTH_LONG).show();
    }

    @OnClick(R.id.loginGoogle) void loginGoogle() {
        // TODO login with Google
        Toast.makeText(LoginActivity.this, "Login with Google+, coming soon!", Toast.LENGTH_LONG).show();
    }

    @OnClick(R.id.loginSignUp) void signUp() {
        startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
    }

    @OnClick(R.id.loginForgotPassword) void forgotPassword() {
        startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
    }
}
