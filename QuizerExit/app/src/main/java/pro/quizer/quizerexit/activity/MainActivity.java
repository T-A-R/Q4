package pro.quizer.quizerexit.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import pro.quizer.quizerexit.R;
import pro.quizer.quizerexit.model.config.ConfigModel;
import pro.quizer.quizerexit.model.config.ProjectInfoModel;
import pro.quizer.quizerexit.model.database.UserModel;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
    }

    private void initViews() {
        final UserModel userModel = getUserByUserId(getCurrentUserId());
        final ConfigModel config = userModel.getConfig();
        final ProjectInfoModel projectInfo = config.getProjectInfo();

        final TextView configAgreement = findViewById(R.id.config_agreement);
        final TextView configName = findViewById(R.id.config_name);

        // TODO: 01.12.2018 remove debug
        final TextView currentUser = findViewById(R.id.current_user);
        currentUser.setText("login: " + userModel.login + " \n" +
                "password: " + userModel.password + "\n" +
                "config_id: " + userModel.config_id + "\n" +
                "user_id: " + userModel.user_id + "\n" +
                "user_project_id: " + userModel.user_project_id + "\n" +
                "role_id: " + userModel.role_id);

        configName.setText(projectInfo.getName());
        configAgreement.setText(projectInfo.getAgreement());

        final Button startBtn = findViewById(R.id.start);

        startBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View pView) {
                startQuestionActivity();
            }
        });
    }
}