package pro.quizer.quizerexit.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import pro.quizer.quizerexit.R;
import pro.quizer.quizerexit.model.config.ConfigField;
import pro.quizer.quizerexit.model.config.ProjectInfoField;
import pro.quizer.quizerexit.model.database.UserModel;

public class ServiceActivity extends BaseActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service);

        initViews();
    }

    private void initViews() {

    }
}