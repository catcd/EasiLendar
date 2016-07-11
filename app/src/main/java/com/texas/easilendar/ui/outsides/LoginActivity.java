package com.texas.easilendar.ui.outsides;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
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

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.texas.easilendar.R;
import com.texas.easilendar.ui.calendars.WeekCalendarActivity;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.texas.easilendar.constant.LoginConstant.LOGIN_EXTRA_PREVIOUS_EMAIL;
import static com.texas.easilendar.constant.LoginConstant.LOGIN_USER_TYPE_ANONYMOUS;
import static com.texas.easilendar.constant.LoginConstant.LOGIN_USER_TYPE_FACEBOOK;
import static com.texas.easilendar.constant.LoginConstant.LOGIN_USER_TYPE_GOOGLE;
import static com.texas.easilendar.constant.LoginConstant.LOGIN_USER_TYPE_PASSWORD;
import static com.texas.easilendar.constant.SharedPreferencesConstant.PREFS_LOGIN_USER;
import static com.texas.easilendar.constant.SharedPreferencesConstant.PREFS_LOGIN_USER_EMAIL;
import static com.texas.easilendar.constant.SharedPreferencesConstant.PREFS_LOGIN_USER_FULL_NAME;
import static com.texas.easilendar.constant.SharedPreferencesConstant.PREFS_LOGIN_USER_ID;
import static com.texas.easilendar.constant.SharedPreferencesConstant.PREFS_LOGIN_USER_TYPE;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    @BindView(R.id.loginAppName) TextView loginAppName;
    @BindView(R.id.loginEmail) EditText loginEmail;
    @BindView(R.id.loginPassword) EditText loginPassword;
    @BindView(R.id.loginProgressBar) ProgressBar loginProgressBar;

    private FirebaseAuth mFirebaseAuth;
    private GoogleApiClient mGoogleApiClient;
    private LoginManager mFacebookLoginManager;
    private CallbackManager mCallbackManager;
    private SharedPreferences sharedPreferencesLoginUser;

    private boolean onLoading = false;

    private static final int RC_GOOGLE_SIGN_IN = 9001;
    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        setAppNameFont();

        configGooglePlayService();
        configFacebookLogin();
        configFirebase();

        // get shared preference editor
        sharedPreferencesLoginUser = getSharedPreferences(PREFS_LOGIN_USER, MODE_PRIVATE);

        specifyInputMethodAction();

        // check for previous email
        checkPreviousEmail();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!onLoading) {
            loginProgressBar.setVisibility(View.GONE);
        } else {
            loginProgressBar.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onStop() {
        super.onStop();
        //Facebook
        if (mFacebookLoginManager != null) {
            mFacebookLoginManager.logOut();
        }
    }

    private void configFirebase() {
        //Get Firebase mFirebaseAuth instance
        mFirebaseAuth = FirebaseAuth.getInstance();
    }

    private void configFacebookLogin() {
        // Initialize the SDK before executing any other operations,
        FacebookSdk.sdkInitialize(getApplicationContext());
        mCallbackManager = CallbackManager.Factory.create();
        mFacebookLoginManager = LoginManager.getInstance();
        mFacebookLoginManager.registerCallback(mCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        handleFacebookAccessToken(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(LoginActivity.this,
                                getResources().getString(R.string.login_error_facebook_not_available),
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Toast.makeText(LoginActivity.this,
                                getResources().getString(R.string.login_error_facebook_connection),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void configGooglePlayService() {
        // [START config_sign_in]
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // [END config_sign_in]

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
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
        onLoading = true;
        // login with password and email
        // authenticate user
        mFirebaseAuth.signInWithEmailAndPassword(mEmail, mPassword)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        loginProgressBar.setVisibility(View.GONE);
                        onLoading = false;

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the mFirebaseAuth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithEmailAndPassword", task.getException());
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
                            sharedPreferencesLoginUser.edit()
                                    .putString(PREFS_LOGIN_USER_TYPE, LOGIN_USER_TYPE_PASSWORD)
                                    .putString(PREFS_LOGIN_USER_ID, user.getUid())
                                    .putString(PREFS_LOGIN_USER_EMAIL, user.getEmail())
                                    .putString(PREFS_LOGIN_USER_FULL_NAME, user.getDisplayName())
                                    .apply();


                            // TODO login with password complete download all data save to SQLite table

                            Toast.makeText(LoginActivity.this,
                                    getResources().getString(R.string.login_success),
                                    Toast.LENGTH_SHORT).show();

                            startActivity(new Intent(LoginActivity.this, WeekCalendarActivity.class));
                            finish();
                        }
                    }
                });
    }

    @OnClick(R.id.loginAnonymous) void loginAnonymous() {
        loginProgressBar.setVisibility(View.VISIBLE);
        // login with password and email
        // authenticate user
        mFirebaseAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the mFirebaseAuth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this,
                                    getResources().getString(R.string.login_error_unknown),
                                    Toast.LENGTH_LONG).show();
                        } else {
                            // set ID, mail, full name
                            FirebaseUser user = task.getResult().getUser();
                            sharedPreferencesLoginUser.edit()
                                    .putString(PREFS_LOGIN_USER_TYPE, LOGIN_USER_TYPE_ANONYMOUS)
                                    .putString(PREFS_LOGIN_USER_ID, user.getUid())
                                    .putString(PREFS_LOGIN_USER_EMAIL,
                                            getResources().getString(R.string.profile_drawer_email_anonymous)
                                    )
                                    .putString(PREFS_LOGIN_USER_FULL_NAME, "Anonymous")
                                    .apply();

                            Toast.makeText(LoginActivity.this,
                                    getResources().getString(R.string.login_success),
                                    Toast.LENGTH_SHORT).show();

                            startActivity(new Intent(LoginActivity.this, WeekCalendarActivity.class));
                            finish();
                        }
                    }
                });
    }

    @OnClick(R.id.loginFacebook) void loginFacebook() {
        mFacebookLoginManager.logInWithReadPermissions(LoginActivity.this, Arrays.asList("email", "public_profile"));
    }

    private void handleFacebookAccessToken(AccessToken token) {
        loginProgressBar.setVisibility(View.VISIBLE);
        onLoading = true;
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        loginProgressBar.setVisibility(View.GONE);
                        onLoading = false;
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            // there was an error
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                Toast.makeText(LoginActivity.this,
                                        getResources().getString(R.string.login_error_user_collision) + " " +
                                        getResources().getString(R.string.login_error_user_collision_facebook),
                                        Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(LoginActivity.this,
                                        getResources().getString(R.string.login_error_authentication),
                                        Toast.LENGTH_LONG).show();
                            }
                        } else {
                            // set ID, mail, full name
                            FirebaseUser user = task.getResult().getUser();
                            sharedPreferencesLoginUser.edit()
                                    .putString(PREFS_LOGIN_USER_TYPE, LOGIN_USER_TYPE_FACEBOOK)
                                    .putString(PREFS_LOGIN_USER_ID, user.getUid())
                                    .putString(PREFS_LOGIN_USER_EMAIL, user.getEmail())
                                    .putString(PREFS_LOGIN_USER_FULL_NAME, user.getDisplayName())
                                    .apply();

                            // TODO login facebook complete download all data save to SQLite table

                            Toast.makeText(LoginActivity.this,
                                    getResources().getString(R.string.login_success),
                                    Toast.LENGTH_SHORT).show();

                            startActivity(new Intent(LoginActivity.this, WeekCalendarActivity.class));
                            finish();
                        }
                    }
                });
    }

    @OnClick(R.id.loginGoogle) void loginGoogle() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Auth.GoogleSignInApi.signOut(mGoogleApiClient);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_GOOGLE_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                Toast.makeText(this,
                        getResources().getString(R.string.login_error_play_service_not_available),
                        Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == FacebookSdk.getCallbackRequestCodeOffset()) {
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        loginProgressBar.setVisibility(View.GONE);
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this,
                getResources().getString(R.string.login_error_play_service_connection),
                Toast.LENGTH_SHORT).show();
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        loginProgressBar.setVisibility(View.VISIBLE);
        onLoading = true;
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        loginProgressBar.setVisibility(View.GONE);
                        onLoading = false;
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            // there was an error
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                Toast.makeText(LoginActivity.this,
                                        getResources().getString(R.string.login_error_user_collision) + " " +
                                                getResources().getString(R.string.login_error_user_collision_facebook),
                                        Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(LoginActivity.this,
                                        getResources().getString(R.string.login_error_authentication),
                                        Toast.LENGTH_LONG).show();
                            }
                        } else {
                            // set ID, mail, full name
                            FirebaseUser user = task.getResult().getUser();
                            sharedPreferencesLoginUser.edit()
                                    .putString(PREFS_LOGIN_USER_TYPE, LOGIN_USER_TYPE_GOOGLE)
                                    .putString(PREFS_LOGIN_USER_ID, user.getUid())
                                    .putString(PREFS_LOGIN_USER_EMAIL, user.getEmail())
                                    .putString(PREFS_LOGIN_USER_FULL_NAME, user.getDisplayName())
                                    .apply();

                            // TODO login google complete download all data save to SQLite table

                            Toast.makeText(LoginActivity.this,
                                    getResources().getString(R.string.login_success),
                                    Toast.LENGTH_SHORT).show();

                            startActivity(new Intent(LoginActivity.this, WeekCalendarActivity.class));
                            finish();
                        }
                    }
                });
    }

    @OnClick(R.id.loginSignUp) void signUp() {
        startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
    }

    @OnClick(R.id.loginForgotPassword) void forgotPassword() {
        startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
    }
}
