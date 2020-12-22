package pro.quizer.quizer3.view.fragment;

import androidx.appcompat.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import pro.quizer.quizer3.MainActivity;
import pro.quizer.quizer3.R;
import pro.quizer.quizer3.database.models.UserModelR;
import pro.quizer.quizer3.executable.ICallback;
import pro.quizer.quizer3.executable.SendQuestionnairesByUserModelExecutable;
import pro.quizer.quizer3.executable.SyncInfoExecutable;
import pro.quizer.quizer3.executable.files.AudiosSendingByUserModelExecutable;
import pro.quizer.quizer3.executable.files.PhotosSendingByUserModelExecutable;
import pro.quizer.quizer3.model.view.SyncViewModel;
import pro.quizer.quizer3.utils.UiUtils;
import pro.quizer.quizer3.view.Anim;
import pro.quizer.quizer3.view.Toolbar;

import static pro.quizer.quizer3.MainActivity.EXIT;

public class SyncFragment extends ScreenFragment implements View.OnClickListener, ICallback {

    private Button mSendDataButton;
    private Button mSendAudioButton;
    private Button mSendPhotoButton;
    private Button mSyncSms;
    private Button mDelete;
    private TextView mProjectStatusView;
    private TextView mUnfinishedView;
    private TextView mQUnsendedView;
    private TextView mAUnsendedView;
    private TextView mPUnsendedView;

    private UserModelR mUserModel;

    private String mQUnsendedViewString;
    private String mAUnsendedViewString;
    private String mPUnsendedViewString;

    public SyncFragment() {
        super(R.layout.fragment_sync);
    }

    @Override
    protected void onReady() {

        initViews();
        MainFragment.enableSideMenu(true, getMainActivity().isExit());
        initStrings();
//        new CleanUpFilesExecutable(getContext(), null).execute();
        updateData(new SyncInfoExecutable(getContext()).execute());
    }

    public void initViews() {
        RelativeLayout cont = findViewById(R.id.sync_cont);
        mUserModel = getCurrentUser();
        Toolbar mToolbar = findViewById(R.id.toolbar);
        TextView mUserNameTitle = findViewById(R.id.user_name_title);
        mProjectStatusView = findViewById(R.id.project_status);
        UiUtils.setTextOrHide(mUserNameTitle, mUserModel.getLogin());
        mSendDataButton = findViewById(R.id.send_q);
        mSendAudioButton = findViewById(R.id.send_audio);
        mSendPhotoButton = findViewById(R.id.send_photo);
        mSyncSms = findViewById(R.id.sync_sms);
        mUnfinishedView = findViewById(R.id.have_unfinished);
        mQUnsendedView = findViewById(R.id.unsended_q);
        mAUnsendedView = findViewById(R.id.unsended_audio);
        mPUnsendedView = findViewById(R.id.unsended_photo);
        mDelete = findViewById(R.id.btn_delete);

        mSyncSms.setOnClickListener(this);
        mDelete.setOnClickListener(this);

        cont.startAnimation(Anim.getAppear(getContext()));
        mDelete.startAnimation(Anim.getAppearSlide(getContext(), 500));
        mSendDataButton.startAnimation(Anim.getAppearSlide(getContext(), 500));
        mSendAudioButton.startAnimation(Anim.getAppearSlide(getContext(), 500));
        mSendPhotoButton.startAnimation(Anim.getAppearSlide(getContext(), 500));
        mSyncSms.startAnimation(Anim.getAppearSlide(getContext(), 500));

        mToolbar.setTitle(getString(R.string.sync_screen));
        if (getMainActivity().getCurrentQuestionnaire() != null) {
            mUnfinishedView.setText(getResources().getString(R.string.sync_have_unfinished_yes));
        } else {
            mUnfinishedView.setText(getResources().getString(R.string.sync_have_unfinished_no));
        }

        mToolbar.showCloseView(v -> replaceFragment(new HomeFragment()));
    }

    @Override
    public void onClick(View view) {
        if (view == mSyncSms) {
            replaceFragment(new SmsFragment());
        } else if (view == mDelete) {
            showDeleteDialog();
        }
    }

    public void showDeleteDialog() {

        MainActivity activity = getMainActivity();

        if (activity != null && !activity.isFinishing()) {

            new AlertDialog.Builder(activity, R.style.AlertDialogTheme)
                    .setCancelable(false)
                    .setTitle(R.string.dialog_delete_title)
                    .setMessage(R.string.dialog_delete_body)
                    .setPositiveButton(R.string.view_yes, (dialog, which) -> {

                        if (activity.getConfig().isSaveAborted()) {
                            saveQuestionnaireToDatabase(activity.getCurrentQuestionnaireForce(), true);
                        } else {
                            getDao().deleteElementDatabaseModelByToken(activity.getCurrentQuestionnaireForce().getToken());
                        }
                        getDao().clearCurrentQuestionnaireR();
                        getDao().clearPrevElementsR();
                        getDao().clearElementPassedR();
                        activity.setCurrentQuestionnaireNull();
                        updateData(new SyncInfoExecutable(getContext()).execute());
                        mUnfinishedView.setText(getResources().getString(R.string.sync_have_unfinished_no));
                    })
                    .setNegativeButton(R.string.view_no, null).show();
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

        mSyncSms.setOnClickListener(view -> replaceFragment(new SmsFragment()));
    }

    private void initStrings() {
        mQUnsendedViewString = getString(R.string.view_unsent_count_quiz);
        mAUnsendedViewString = getString(R.string.view_unsent_count_audio);
        mPUnsendedViewString = getString(R.string.view_unsent_count_photo);
    }

    private void updateData(final SyncViewModel pSyncViewModel) {
        final int mQUnsendedCount = pSyncViewModel.getmNotSentQuestionnaireModels().size();
        final int mAUnsendedCount = pSyncViewModel.getmNotSendedAudio().size();
        final int mPUnsendedCount = pSyncViewModel.getmNotSendedPhoto().size();
        final boolean hasReserveChannel = pSyncViewModel.hasReserveChannel();
        final boolean hasUnfinishedQuiz = pSyncViewModel.hasUnfinishedQuiz();

        final MainActivity activity = (MainActivity) getActivity();

        if (activity == null) {
            return;
        }

        activity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if (hasReserveChannel && EXIT) {
                    showSmsButton();
                } else {
                    hideSmsButton();
                }

                if(activity.getSettings().isProject_is_active()) {
                   mProjectStatusView.setVisibility(View.GONE);
                    UiUtils.setButtonEnabled(mSendDataButton, mQUnsendedCount > 0);
                    UiUtils.setButtonEnabled(mSendPhotoButton, mPUnsendedCount > 0);
                    UiUtils.setButtonEnabled(mSendAudioButton, mAUnsendedCount > 0);
                } else {
                    mProjectStatusView.setVisibility(View.VISIBLE);
                    UiUtils.setButtonEnabledLightGreen(mSendDataButton, mQUnsendedCount > 0);
                    UiUtils.setButtonEnabledLightGreen(mSendPhotoButton, mPUnsendedCount > 0);
                    UiUtils.setButtonEnabledLightGreen(mSendAudioButton, mAUnsendedCount > 0);
                }

                UiUtils.setTextOrHide(mQUnsendedView, (String.format(mQUnsendedViewString, mQUnsendedCount)));
                UiUtils.setTextOrHide(mAUnsendedView, (String.format(mAUnsendedViewString, mAUnsendedCount)));
                UiUtils.setTextOrHide(mPUnsendedView, (String.format(mPUnsendedViewString, mPUnsendedCount)));

                UiUtils.setButtonEnabledRed(mDelete, hasUnfinishedQuiz);

                mSendDataButton.setOnClickListener(view -> new SendQuestionnairesByUserModelExecutable(getMainActivity(), mUserModel, SyncFragment.this, false).execute());

                mSendPhotoButton.setOnClickListener(view -> {
                    if (hasUnfinishedOrSend(mQUnsendedCount, hasUnfinishedQuiz)) return;

                    new PhotosSendingByUserModelExecutable((MainActivity) getActivity(), mUserModel, SyncFragment.this).execute();
                });

                mSendAudioButton.setOnClickListener(view -> {
                    if (hasUnfinishedOrSend(mQUnsendedCount, hasUnfinishedQuiz)) return;

                    new AudiosSendingByUserModelExecutable((MainActivity) getActivity(), mUserModel, SyncFragment.this).execute();
                });
            }
        });
    }

    @Override
    public void onStarting() {
        if (isAdded()) {
            showToast(getString(R.string.notification_sending));
            showScreensaver(true);
        }
    }

    @Override
    public void onSuccess() {
        if (isAdded()) {
            updateData(new SyncInfoExecutable(getContext()).execute());
            hideScreensaver();
        }
    }

    @Override
    public void onError(final Exception pException) {
        if (isAdded()) {
            showToast(pException.toString());
            updateData(new SyncInfoExecutable(getContext()).execute());
            hideScreensaver();
        }
    }
}

