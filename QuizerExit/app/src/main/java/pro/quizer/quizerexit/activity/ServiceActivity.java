package pro.quizer.quizerexit.activity;

import android.os.Bundle;
import android.widget.TextView;

import pro.quizer.quizerexit.R;

public class ServiceActivity extends BaseActivity {

    private TextView mUsersCount;
    private String mUsersCountString;
    private TextView mUnsendedQuestionaires;
    private String mUnsendedQuestionairesString;
    private TextView mUnsendedAudio;
    private String mUnsendedAudioString;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service);

        initViews();
        initStrings();
        updateData();
    }

    private void initViews() {
        mUsersCount = findViewById(R.id.users_count);
        mUnsendedQuestionaires = findViewById(R.id.unsended_questionnaires_count);
        mUnsendedAudio = findViewById(R.id.unsended_audio_files_count);
    }

    private void initStrings() {
        mUsersCountString = getString(R.string.count_users_on_this_device);
        mUnsendedQuestionairesString = getString(R.string.count_unsended_questionnaires);
        mUnsendedAudioString = getString(R.string.count_unsended_audio_files);
    }

    private void updateData() {
        mUsersCount.setText(String.format(mUsersCountString, getUsersCount()));
        mUnsendedQuestionaires.setText(String.format(mUnsendedQuestionairesString, getCountAllUnsendedQuestionaires()));
        mUnsendedAudio.setText(String.format(mUnsendedAudioString, getCountAllUnsendedAudioFiled()));
    }
}