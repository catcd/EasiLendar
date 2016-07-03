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

public class ForgotPasswordActivity extends AppCompatActivity {
    @BindView(R.id.forgotPasswordAppName) TextView forgotPasswordAppName;
    @BindView(R.id.forgotPasswordEmail) EditText forgotPasswordEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        ButterKnife.bind(this);

        setAppNameFont();
    }

    @OnClick(R.id.forgotPasswordSubmit) void resetPassword() {
        String mEmail = forgotPasswordEmail.getText().toString().trim();

        // TODO reset password
        Toast.makeText(ForgotPasswordActivity.this, "Reset password of email " + mEmail, Toast.LENGTH_LONG).show();
    }

    @OnClick(R.id.forgotPasswordLogin) void login() {
        finish();
    }

    private void setAppNameFont() {
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/rotulona-hand.ffp.otf");
        forgotPasswordAppName.setTypeface(font);
    }
}
