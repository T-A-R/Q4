package pro.quizer.quizerexit.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import pro.quizer.quizerexit.R;
import pro.quizer.quizerexit.model.config.ConfigModel;
import pro.quizer.quizerexit.model.config.ProjectInfoModel;
import pro.quizer.quizerexit.model.database.UserModel;
import pro.quizer.quizerexit.utils.StringUtils;

public class ThankYouActivity extends BaseActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thank_you);

        initViews();
    }

    private void initViews() {
        TextView mThankYouTextView = findViewById(R.id.thank_you_text_view);
        ImageView mThankYouImageView = findViewById(R.id.thank_you_image_view);
        Button mButton = findViewById(R.id.end_btn);

        final UserModel userModel = getCurrentUser();
        final ConfigModel configModel = userModel.getConfig();
        final ProjectInfoModel projectInfoModel = configModel.getProjectInfo();

        final String thankYouText = projectInfoModel.getThankYouText();
        final String thankYouImageUrl = projectInfoModel.getThankYouPicture();

        if (StringUtils.isNotEmpty(thankYouText)) {
            mThankYouTextView.setVisibility(View.VISIBLE);
            mThankYouTextView.setText(thankYouText);
        }

        if (StringUtils.isNotEmpty(thankYouImageUrl)) {
            mThankYouImageView.setVisibility(View.VISIBLE);
            Picasso.with(this)
                    .load(thankYouImageUrl)
                    .into(mThankYouImageView);

        }

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startMainActivity();
            }
        });
    }
}