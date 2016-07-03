package com.texas.easilendar.ui.outsides;

import android.content.Intent;
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

public class LoginActivity extends AppCompatActivity {
    @BindView(R.id.loginAppName) TextView loginAppName;
    @BindView(R.id.loginEmail) EditText loginEmail;
    @BindView(R.id.loginPassword) EditText loginPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        setAppNameFont();
    }

    @OnClick(R.id.loginSubmit) void login() {
        String mEmail = loginEmail.getText().toString().trim();
        String mPassword = loginPassword.getText().toString().trim();

        // TODO login
        Toast.makeText(LoginActivity.this, "Login to " + mEmail + " with password " + mPassword, Toast.LENGTH_LONG).show();
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

    private void setAppNameFont() {
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/rotulona-hand.ffp.otf");
        loginAppName.setTypeface(font);
    }
}
