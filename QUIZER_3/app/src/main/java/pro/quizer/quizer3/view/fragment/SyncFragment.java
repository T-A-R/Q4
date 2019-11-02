package pro.quizer.quizer3.view.fragment;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import pro.quizer.quizer3.MainActivity;
import pro.quizer.quizer3.R;
import pro.quizer.quizer3.database.models.UserModelR;
import pro.quizer.quizer3.executable.SyncInfoExecutable;
import pro.quizer.quizer3.executable.files.CleanUpFilesExecutable;
import pro.quizer.quizer3.model.view.SyncViewModel;
import pro.quizer.quizer3.utils.Fonts;
import pro.quizer.quizer3.utils.StringUtils;
import pro.quizer.quizer3.utils.UiUtils;
import pro.quizer.quizer3.view.Anim;
import pro.quizer.quizer3.view.Toolbar;

public class SyncFragment extends ScreenFragment implements View.OnClickListener {

    private Toolbar mToolbar;
    private TextView mUserNameTitle;
    private Button mSendDataButton;
    private Button mSendAudioButton;
    private Button mSendPhotoButton;
    private Button mSyncSms;
    private TextView mQSendedFromThisDeviceView;
    private TextView mQSendedInSessionView;
    private TextView mQUnsendedView;
    private TextView mAUnsendedView;
    private TextView mPUnsendedView;

    private UserModelR mUserModel;

    private String mQSendedFromThisDeviceViewString;
    private String mQSendedInSessionViewString;
    private String mQUnsendedViewString;
    private String mAUnsendedViewString;
    private String mPUnsendedViewString;

    public SyncFragment() {
        super(R.layout.fragment_sync);
    }

    @Override
    protected void onReady() {

        initViews();
        MainFragment.enableSideMenu(true);
        initStrings();
        new CleanUpFilesExecutable(getContext(), null).execute();
        updateData(new SyncInfoExecutable(getContext()).execute());
    }

    public void initViews() {
        RelativeLayout cont = findViewById(R.id.sync_cont);
        mUserModel = getCurrentUser();
        mToolbar = findViewById(R.id.toolbar);
        mUserNameTitle = findViewById(R.id.user_name_title);
        UiUtils.setTextOrHide(mUserNameTitle, mUserModel.getLogin());
        mSendDataButton = findViewById(R.id.send_q);
        mSendAudioButton = findViewById(R.id.send_audio);
        mSendPhotoButton = findViewById(R.id.send_photo);
        mSyncSms = findViewById(R.id.sync_sms);
        mQSendedFromThisDeviceView = findViewById(R.id.sended_q_from_this_device);
        mQSendedInSessionView = findViewById(R.id.sended_q_in_session);
        mQUnsendedView = findViewById(R.id.unsended_q);
        mAUnsendedView = findViewById(R.id.unsended_audio);
        mPUnsendedView = findViewById(R.id.unsended_photo);

//        etKey.setTypeface(Fonts.getFuturaPtMedium());
//        mSendDataButton.setTypeface(Fonts.getFuturaPtBook());
//        mSendAudioButton.setTypeface(Fonts.getFuturaPtBook());
//        mSendPhotoButton.setTypeface(Fonts.getFuturaPtBook());
//        mSyncSms.setTypeface(Fonts.getFuturaPtBook());
//
//        mSendDataButton.setTransformationMethod(null);
//        mSendAudioButton.setTransformationMethod(null);
//        mSendPhotoButton.setTransformationMethod(null);
//        mSyncSms.setTransformationMethod(null);

        mSyncSms.setOnClickListener(this);

        cont.startAnimation(Anim.getAppear(getContext()));
        mSendDataButton.startAnimation(Anim.getAppearSlide(getContext(), 500));
        mSendAudioButton.startAnimation(Anim.getAppearSlide(getContext(), 500));
        mSendPhotoButton.startAnimation(Anim.getAppearSlide(getContext(), 500));
        mSyncSms.startAnimation(Anim.getAppearSlide(getContext(), 500));

        mToolbar.setTitle(getString(R.string.sync_screen));
        mToolbar.showCloseView(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                replaceFragment(new HomeFragment());
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view == mSyncSms) {
//                replaceFragment(new SmsFragment());
        }
    }

    @Override
    public boolean onBackPressed() {
        replaceFragment(new HomeFragment());
        return true;
    }

    private void hideSmsButton() {
        mSyncSms.setVisibility(View.GONE);
    }

    private void showSmsButton() {
        mSyncSms.setVisibility(View.VISIBLE);
    }

    private void initStrings() {
        mQSendedFromThisDeviceViewString = getString(R.string.view_sent_count_quiz_from_this_device);
        mQSendedInSessionViewString = getString(R.string.view_sent_count_quiz_from_this_session);
        mQUnsendedViewString = getString(R.string.view_unsent_count_quiz);
        mAUnsendedViewString = getString(R.string.view_unsent_count_audio);
        mPUnsendedViewString = getString(R.string.view_unsent_count_photo);
    }

    private void updateData(final SyncViewModel pSyncViewModel) {
        final int mQSendedFromThisDeviceCount = pSyncViewModel.getmSentQuestionnaireModelsFromThisDevice().size();
        final int mQSendedInSessionCount = pSyncViewModel.getSentQuestionnaireModelsInSession(getContext());
        final int mQUnsendedCount = pSyncViewModel.getmNotSentQuestionnaireModels().size();
        final int mAUnsendedCount = pSyncViewModel.getmNotSendedAudio().size();
        final int mPUnsendedCount = pSyncViewModel.getmNotSendedPhoto().size();
        final boolean hasReserveChannel = pSyncViewModel.hasReserveChannel();

        final MainActivity activity = (MainActivity) getActivity();

        if (activity == null) {
            return;
        }

        activity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if (hasReserveChannel) {
                    showSmsButton();
                } else {
                    hideSmsButton();
                }
                UiUtils.setTextOrHide(mQSendedFromThisDeviceView, (String.format(mQSendedFromThisDeviceViewString, mQSendedFromThisDeviceCount)));
                UiUtils.setTextOrHide(mQSendedInSessionView, (String.format(mQSendedInSessionViewString, mQSendedInSessionCount)));
                UiUtils.setTextOrHide(mQUnsendedView, (String.format(mQUnsendedViewString, mQUnsendedCount)));
                UiUtils.setTextOrHide(mAUnsendedView, (String.format(mAUnsendedViewString, mAUnsendedCount)));
                UiUtils.setTextOrHide(mPUnsendedView, (String.format(mPUnsendedViewString, mPUnsendedCount)));

                UiUtils.setButtonEnabled(mSendDataButton, mQUnsendedCount > 0);
                mSendDataButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View view) {
                        //TODO SEND QUIZ
//                        new SendQuestionnairesByUserModelExecutable(getBaseActivity(), mUserModel, SyncFragment.this, false).execute();
                    }
                });

                UiUtils.setButtonEnabled(mSendPhotoButton, mPUnsendedCount > 0);
                mSendPhotoButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View view) {
                        if (mQUnsendedCount > 0) {
                            showToast(getString(R.string.notification_please_send_quiz));
                            return;
                        }

                        //TODO SEND PHOTO
//                        new PhotosSendingByUserModelExecutable(getBaseActivity(), mUserModel, SyncFragment.this).execute();
                    }
                });

                UiUtils.setButtonEnabled(mSendAudioButton, mAUnsendedCount > 0);
                mSendAudioButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(final View view) {
                        if (mQUnsendedCount > 0) {
                            showToast(getString(R.string.notification_please_send_quiz));
                            return;
                        }

                        //TODO SEND AUIDIO
//                        new AudiosSendingByUserModelExecutable(getBaseActivity(), mUserModel, SyncFragment.this).execute();
                    }
                });
            }
        });
    }

//    @Override
//    public void onStarting() {
//        if (isAdded()) {
//            showToast(getString(R.string.NOTIFICATION_SENDING));
//        }
//    }
//
//    @Override
//    public void onSuccess() {
//        if (isAdded()) {
//            updateData(new SyncInfoExecutable(getContext()).execute());
//        }
//    }
//
//    @Override
//    public void onError(final Exception pException) {
//        if (isAdded()) {
//            showToast(pException.toString());
//            updateData(new SyncInfoExecutable(getContext()).execute());
//        }
//    }
}

