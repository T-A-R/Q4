package pro.quizer.quizerexit.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import pro.quizer.quizerexit.R;
import pro.quizer.quizerexit.model.config.ProjectInfoField;

public class MainActivity extends BaseActivity {

//    public void onSingleSelectionClick(final View view) {
//        final Intent intent = new Intent(this, RecyclerViewActivity.class);
//        intent.putExtra(RecyclerViewActivity.BUNDLE_MAX_ANSWERS, 1);
//        intent.putExtra(RecyclerViewActivity.BUNDLE_MIN_ANSWERS, 1);
//        startActivity(intent);
//    }
//
//    public void onMaxSelectionClick(final View view) {
//        final Intent intent = new Intent(this, RecyclerViewActivity.class);
//        intent.putExtra(RecyclerViewActivity.BUNDLE_MAX_ANSWERS, 3);
//        startActivity(intent);
//    }
//
//    public void onMinSelectionClick(final View view) {
//        final Intent intent = new Intent(this, RecyclerViewActivity.class);
//        intent.putExtra(RecyclerViewActivity.BUNDLE_MIN_ANSWERS, 3);
//        startActivity(intent);
//    }
//
//    public void onMinAndMaxSelectionClick(final View view) {
//        final Intent intent = new Intent(this, RecyclerViewActivity.class);
//        intent.putExtra(RecyclerViewActivity.BUNDLE_MAX_ANSWERS, 4);
//        intent.putExtra(RecyclerViewActivity.BUNDLE_MIN_ANSWERS, 2);
//        startActivity(intent);
//    }
//
//    public void onMultiSelectionClick(final View view) {
//        final Intent intent = new Intent(this, RecyclerViewActivity.class);
//        startActivity(intent);
//    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
    }

    private void initViews() {
//        final ProjectInfoField projectInfo = getSPConfigModel().getConfig().getProjectInfo();

        final TextView configAgreement = findViewById(R.id.config_agreement);
        final TextView configName = findViewById(R.id.config_name);
//
//        configName.setText(projectInfo.getName());
//        configAgreement.setText(projectInfo.getAgreement());

        final Button settingsBtn = findViewById(R.id.settings);
        final Button syncBtn = findViewById(R.id.sync);
        final Button startBtn = findViewById(R.id.start);

        startBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View pView) {
                startQuestionActivity();
            }
        });
    }
}