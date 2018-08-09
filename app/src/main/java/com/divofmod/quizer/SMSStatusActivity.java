package com.divofmod.quizer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.divofmod.quizer.Utils.Utils;

public class SMSStatusActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    public void onClick(final View pView) {
        switch (pView.getId()) {
            case R.id.btn_1:
                final TextView tv = (TextView) findViewById(R.id.tv_1);
                final TextView status1 = (TextView) findViewById(R.id.status_1);
                status1.setText("Отправлено");
                pView.setVisibility(View.INVISIBLE);
                Utils.sendSMS(this, tv.getText().toString());
                break;
            case R.id.btn_2:
                final TextView tv2 = (TextView) findViewById(R.id.tv_2);
                final TextView status2 = (TextView) findViewById(R.id.status_2);
                status2.setText("Отправлено");
                pView.setVisibility(View.INVISIBLE);
                Utils.sendSMS(this, tv2.getText().toString());

                break;
            default:
        }
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sms_status);

        findViewById(R.id.btn_1).setOnClickListener(this);
        findViewById(R.id.btn_2).setOnClickListener(this);
    }
}
