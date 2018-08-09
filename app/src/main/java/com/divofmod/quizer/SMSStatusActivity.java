package com.divofmod.quizer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class SMSStatusActivity extends AppCompatActivity {

    public void onBackClick(final View view) {
        onBackPressed();
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sms_status);
    }
}
