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
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.texas.easilendar.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.texas.easilendar.constant.SharedPreferencesConstant.PREFS_LOGIN_USER;
import static com.texas.easilendar.constant.SharedPreferencesConstant.PREFS_LOGIN_USER_EMAIL;
import static com.texas.easilendar.constant.SharedPreferencesConstant.PREFS_LOGIN_USER_FULL_NAME;
import static com.texas.easilendar.constant.SharedPreferencesConstant.PREFS_LOGIN_USER_ID;

public class SignUpActivity extends AppCompatActivity {
    @BindView(R.id.signUpAppName) TextView signUpAppName;
    @BindView(R.id.signUpEmail) EditText signUpEmail;
    @BindView(R.id.signUpFullName) EditText signUpFullName;
    @BindView(R.id.signUpPassword) EditText signUpPassword;
    @BindView(R.id.signUpRetypePassword) EditText signUpRetypePassword;
    @BindView(R.id.signUpProgressBar) ProgressBar signUpProgressBar;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);
        setAppNameFont();

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        specifyInputMethodAction();
    }

    private void setAppNameFont() {
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/rotulona-hand.ffp.otf");
        signUpAppName.setTypeface(font);
    }

    private void specifyInputMethodAction() {
        signUpRetypePassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    register();
                    handled = true;
                }
                return handled;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        signUpProgressBar.setVisibility(View.GONE);
    }

    @OnClick(R.id.signUpSubmit) void register() {
        // Hide the keyboard
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        View currentFocus = getCurrentFocus();
        if (currentFocus != null) {
            imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
        }

        // get EditText
        String mEmail = signUpEmail.getText().toString().trim();
        final String mFullName = signUpFullName.getText().toString().trim();
        String mPassword = signUpPassword.getText().toString().trim();
        String mRetypePassword = signUpRetypePassword.getText().toString().trim();

        // Check valid
        if (TextUtils.isEmpty(mEmail)) {
            signUpEmail.requestFocus();
            signUpEmail.setError(getResources().getString(R.string.sign_up_error_field_required));
            return;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(mEmail).matches()) {
            signUpEmail.requestFocus();
            signUpEmail.setError(getResources().getString(R.string.sign_up_error_invalid_email));
            return;
        } else if (TextUtils.isEmpty(mFullName)) {
            signUpFullName.requestFocus();
            signUpFullName.setError(getResources().getString(R.string.sign_up_error_field_required));
            return;
        } else if (TextUtils.isEmpty(mPassword)) {
            signUpPassword.requestFocus();
            signUpPassword.setError(getResources().getString(R.string.sign_up_error_field_required));
            return;
        } else if (mPassword.length() < 6) {
            signUpPassword.requestFocus();
            signUpPassword.setError(getResources().getString(R.string.sign_up_error_too_short_password));
            return;
        } else if (TextUtils.isEmpty(mRetypePassword)) {
            signUpRetypePassword.requestFocus();
            signUpRetypePassword.setError(getResources().getString(R.string.sign_up_error_field_required));
            return;
        } else if (!mPassword.equals(mRetypePassword)) {
            signUpRetypePassword.requestFocus();
            signUpRetypePassword.setError(getResources().getString(R.string.sign_up_error_not_matched_password));
            return;
        }

        signUpProgressBar.setVisibility(View.VISIBLE);
        //create user
        auth.createUserWithEmailAndPassword(mEmail, mPassword)
                .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            signUpProgressBar.setVisibility(View.GONE);

                            if (task.getException() instanceof FirebaseAuthWeakPasswordException) {
                                signUpPassword.requestFocus();
                                signUpPassword.setError(getResources().getString(R.string.sign_up_error_week_password));
                            } else if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                signUpEmail.requestFocus();
                                signUpEmail.setError(getResources().getString(R.string.sign_up_error_invalid_email));
                            } else if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                signUpEmail.requestFocus();
                                signUpEmail.setError(getResources().getString(R.string.sign_up_error_existed_email));
                            } else {
                                Toast.makeText(SignUpActivity.this,
                                        getResources().getString(R.string.sign_up_error_unknown),
                                        Toast.LENGTH_LONG).show();
                            }
                        } else {
                            // get ID, mail, full name
                            FirebaseUser user = task.getResult().getUser();
                            SharedPreferences loginUser = getSharedPreferences(PREFS_LOGIN_USER, MODE_PRIVATE);
                            SharedPreferences.Editor editor = loginUser.edit();
                            editor.putString(PREFS_LOGIN_USER_ID, user.getUid());
                            editor.putString(PREFS_LOGIN_USER_EMAIL, user.getEmail());
                            editor.apply();

                            // Update name
                            task.getResult().getUser().updateProfile(
                                    new UserProfileChangeRequest.Builder()
                                    .setDisplayName(mFullName)
                                    .build()
                            ).addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    signUpProgressBar.setVisibility(View.GONE);

                                    if (!task.isComplete()) {
                                        Toast.makeText(SignUpActivity.this,
                                                getResources().getString(R.string.sign_up_success_without_name),
                                                Toast.LENGTH_LONG).show();
                                    } else {
                                        // set full name
                                        getSharedPreferences(PREFS_LOGIN_USER, MODE_PRIVATE).edit()
                                            .putString(PREFS_LOGIN_USER_FULL_NAME, mFullName).apply();

                                        Toast.makeText(SignUpActivity.this,
                                                getResources().getString(R.string.sign_up_success),
                                                Toast.LENGTH_SHORT).show();
                                    }

                                    Intent i = new Intent(SignUpActivity.this, WelcomeActivity.class);
                                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(i);
                                }
                            });
                        }
                    }
                });
    }

    @OnClick(R.id.signUpLogin) void login() {
        finish();
    }
}
