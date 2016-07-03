package com.texas.easilendar.ui.outsides;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.texas.easilendar.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignUpActivity extends AppCompatActivity {
    @BindView(R.id.signUpAppName) TextView signUpAppName;
    @BindView(R.id.signUpEmail) EditText signUpEmail;
    @BindView(R.id.signUpPassword) EditText signUpPassword;
    @BindView(R.id.signUpRetypePassword) EditText signUpRetypePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);

        setAppNameFont();
    }

    @OnClick(R.id.signUpSubmit) void register() {
        String mEmail = signUpEmail.getText().toString().trim();
        String mPassword = signUpPassword.getText().toString().trim();
        String mRetypePassword = signUpRetypePassword.getText().toString().trim();

        // TODO register
        Toast.makeText(SignUpActivity.this, "Register new account with " + mEmail + ", password " + mPassword + " and retype password " + mRetypePassword, Toast.LENGTH_LONG).show();
    }

    @OnClick(R.id.signUpLogin) void login() {
        finish();
    }

    private void setAppNameFont() {
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/rotulona-hand.ffp.otf");
        signUpAppName.setTypeface(font);
    }
}
