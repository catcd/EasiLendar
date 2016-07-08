package com.texas.easilendar.ui.outsides;

import android.content.Context;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.texas.easilendar.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ForgotPasswordActivity extends AppCompatActivity {
    @BindView(R.id.forgotPasswordAppName) TextView forgotPasswordAppName;
    @BindView(R.id.forgotPasswordEmail) EditText forgotPasswordEmail;
    @BindView(R.id.forgotPasswordProgressBar) ProgressBar forgotPasswordProgressBar;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        ButterKnife.bind(this);
        setAppNameFont();

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        specifyInputMethodAction();
    }

    private void setAppNameFont() {
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/rotulona-hand.ffp.otf");
        forgotPasswordAppName.setTypeface(font);
    }

    private void specifyInputMethodAction() {
        forgotPasswordEmail.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    resetPassword();
                    handled = true;
                }
                return handled;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        forgotPasswordProgressBar.setVisibility(View.GONE);
    }

    @OnClick(R.id.forgotPasswordSubmit) void resetPassword() {
        // Hide the keyboard
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        try {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            Log.d("ForgotPasswordActivity", "resetPassword: resetPassword without any focus");
        }

        // get EditText
        String mEmail = forgotPasswordEmail.getText().toString().trim();

        // Check valid
        if (TextUtils.isEmpty(mEmail)) {
            forgotPasswordEmail.requestFocus();
            forgotPasswordEmail.setError(getResources().getString(R.string.forgot_password_error_field_required));
            return;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(mEmail).matches()) {
            forgotPasswordEmail.requestFocus();
            forgotPasswordEmail.setError(getResources().getString(R.string.forgot_password_error_invalid_email));
            return;
        }

        forgotPasswordProgressBar.setVisibility(View.VISIBLE);
        // send password reset instruction email
        auth.sendPasswordResetEmail(mEmail)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        forgotPasswordProgressBar.setVisibility(View.GONE);

                        if (task.isSuccessful()) {
                            Toast.makeText(ForgotPasswordActivity.this,
                                    getResources().getString(R.string.forgot_password_success),
                                    Toast.LENGTH_LONG).show();

                            finish();
                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                                forgotPasswordEmail.requestFocus();
                                forgotPasswordEmail.setError(getResources().getString(R.string.forgot_password_error_not_existed_email));
                            }  else {
                                Toast.makeText(ForgotPasswordActivity.this,
                                        getResources().getString(R.string.forgot_password_error_unknown),
                                        Toast.LENGTH_LONG).show();
                            }
                        }

                    }
                });
    }

    @OnClick(R.id.forgotPasswordLogin) void login() {
        finish();
    }
}
