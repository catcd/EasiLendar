package com.texas.easilendar.ui.outsides;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
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
import com.google.firebase.auth.FirebaseUser;
import com.texas.easilendar.R;
import com.texas.easilendar.ui.calendars.WeekCalendarActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.texas.easilendar.constant.LoginConstant.LOGIN_EXTRA_PREVIOUS_EMAIL;
import static com.texas.easilendar.constant.LoginConstant.LOGIN_LOGIN_ANONYMOUS_UID;
import static com.texas.easilendar.constant.WeekCalendarConstant.WCAL_TYPE_WEEK_VIEW;
import static com.texas.easilendar.constant.SharedPreferencesConstant.*;

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

        // check for previous email
        checkPreviousEmail();
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

    private void checkPreviousEmail() {
        Intent i = getIntent();
        String prevEmail = i.getStringExtra(LOGIN_EXTRA_PREVIOUS_EMAIL);
        if (prevEmail != null && android.util.Patterns.EMAIL_ADDRESS.matcher(prevEmail).matches()) {
            loginEmail.setText(prevEmail);
            loginPassword.requestFocus();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loginProgressBar.setVisibility(View.GONE);
    }

    @OnClick(R.id.loginSubmit) void login() {
        // Hide the keyboard
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        View currentFocus = getCurrentFocus();
        if (currentFocus != null) {
            imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
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
                            // get ID, mail, full name
                            FirebaseUser user = task.getResult().getUser();
                            SharedPreferences loginUser = getSharedPreferences(PREFS_LOGIN_USER, 0);
                            SharedPreferences.Editor editor = loginUser.edit();
                            editor.putString(PREFS_LOGIN_USER_ID, user.getUid());
                            editor.putString(PREFS_LOGIN_USER_EMAIL, user.getEmail());
                            editor.putString(PREFS_LOGIN_USER_FULL_NAME, user.getDisplayName());
                            editor.apply();

                            // TODO get user avatar from firebase storage save to file

                            // TODO login complete download all data save to SQLite table

                            Toast.makeText(LoginActivity.this,
                                    getResources().getString(R.string.login_success),
                                    Toast.LENGTH_SHORT).show();

                            Intent i = new Intent(LoginActivity.this, WeekCalendarActivity.class);
                            i.putExtra("weekViewType", WCAL_TYPE_WEEK_VIEW);
                            startActivity(i);
                            finish();
                        }
                    }
                });
    }

    @OnClick(R.id.loginAnonymous) void loginAnonymous() {
        loginProgressBar.setVisibility(View.VISIBLE);
        // login with password and email
        // authenticate user
        auth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this,
                                    getResources().getString(R.string.login_error_unknown),
                                    Toast.LENGTH_LONG).show();
                        } else {
                            // set ID, mail, full name
                            SharedPreferences loginUser = getSharedPreferences(PREFS_LOGIN_USER, 0);
                            SharedPreferences.Editor editor = loginUser.edit();
                            editor.putString(PREFS_LOGIN_USER_ID, LOGIN_LOGIN_ANONYMOUS_UID);
                            editor.putString(PREFS_LOGIN_USER_EMAIL,
                                    getResources().getString(R.string.profile_drawer_email_anonymous)
                            );
                            editor.putString(PREFS_LOGIN_USER_FULL_NAME, "Anonymous");
                            editor.apply();

                            // TODO login anonymous complete download all anonymous data save to SQLite table

                            Toast.makeText(LoginActivity.this,
                                    getResources().getString(R.string.login_success),
                                    Toast.LENGTH_SHORT).show();

                            Intent i = new Intent(LoginActivity.this, WeekCalendarActivity.class);
                            i.putExtra("weekViewType", WCAL_TYPE_WEEK_VIEW);
                            startActivity(i);
                            finish();
                        }
                    }
                });
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
