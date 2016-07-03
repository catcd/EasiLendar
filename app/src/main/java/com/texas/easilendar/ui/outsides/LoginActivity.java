package com.texas.easilendar.ui.outsides;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.texas.easilendar.R;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        TextView loginAppName = (TextView) findViewById(R.id.loginAppName);
        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/rotulona-hand.ffp.otf");
        loginAppName.setTypeface(face);
    }
}
