package com.texas.easilendar.ui.profiles;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.texas.easilendar.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChangePrivacyActivity extends AppCompatActivity {
    @BindView(R.id.changePrivacyToolbar) Toolbar changePrivacyToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_privacy);
        ButterKnife.bind(this);

        // setup toolbar
        setSupportActionBar(changePrivacyToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }
}
