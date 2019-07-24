package pro.quizer.quizerexit.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import pro.quizer.quizerexit.R;
import pro.quizer.quizerexit.activity.BaseActivity;
import pro.quizer.quizerexit.database.model.UserModelR;
import pro.quizer.quizerexit.executable.ICallback;
import pro.quizer.quizerexit.executable.SendQuestionnairesByUserModelExecutable;
import pro.quizer.quizerexit.executable.SyncInfoExecutable;
import pro.quizer.quizerexit.executable.files.AudiosSendingByUserModelExecutable;
import pro.quizer.quizerexit.executable.files.CleanUpFilesExecutable;
import pro.quizer.quizerexit.executable.files.PhotosSendingByUserModelExecutable;
import pro.quizer.quizerexit.model.database.UserModel;
import pro.quizer.quizerexit.model.view.SyncViewModel;
import pro.quizer.quizerexit.utils.UiUtils;

public class SyncFragment extends BaseFragment implements ICallback {

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

//    private UserModel mUserModel;
    private UserModelR mUserModel;
    private BaseActivity mBaseActivity;

    private String mQSendedFromThisDeviceViewString;
    private String mQSendedInSessionViewString;
    private String mQUnsendedViewString;
    private String mAUnsendedViewString;
    private String mPUnsendedViewString;

    public static Fragment newInstance() {
        final SyncFragment fragment = new SyncFragment();

        final Bundle bundle = new Bundle();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sync, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        initStrings();

        new CleanUpFilesExecutable(getContext(), null).execute();
        updateData(new SyncInfoExecutable(getContext()).execute());
    }

    private void initViews(final View pView) {
        mBaseActivity = getBaseActivity();

        if (mBaseActivity == null) {
            return;
        }

        mUserModel = mBaseActivity.getCurrentUser();

        mUserNameTitle = pView.findViewById(R.id.user_name_title);
//        UiUtils.setTextOrHide(mUserNameTitle, mUserModel.login);
        UiUtils.setTextOrHide(mUserNameTitle, mUserModel.getLogin());

        mSendDataButton = pView.findViewById(R.id.send_q);
        mSendAudioButton = pView.findViewById(R.id.send_audio);
        mSendPhotoButton = pView.findViewById(R.id.send_photo);
        mSyncSms = pView.findViewById(R.id.sync_sms);

        mQSendedFromThisDeviceView = pView.findViewById(R.id.sended_q_from_this_device);
        mQSendedInSessionView = pView.findViewById(R.id.sended_q_in_session);
        mQUnsendedView = pView.findViewById(R.id.unsended_q);

//        mASendedFromThisDeviceView = pView.findViewById(R.id.sended_audio_from_this_device);
//        mASendedInSessionView = pView.findViewById(R.id.sended_audio_in_session);
        mAUnsendedView = pView.findViewById(R.id.unsended_audio);

//        mPSendedFromThisDeviceView = pView.findViewById(R.id.sended_photo_from_this_device);
//        mPSendedInSessionView = pView.findViewById(R.id.sended_photo_in_session);
        mPUnsendedView = pView.findViewById(R.id.unsended_photo);
    }

    private void hideSmsButton() {
        mSyncSms.setVisibility(View.GONE);
    }

    private void showSmsButton() {
        mSyncSms.setVisibility(View.VISIBLE);
        mSyncSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBaseActivity.showSmsFragment();
            }
        });
    }

    private void initStrings() {
        mQSendedFromThisDeviceViewString = getString(R.string.VIEW_SENT_COUNT_QUIZ_FROM_THIS_DEVICE);
        mQSendedInSessionViewString = getString(R.string.VIEW_SENT_COUNT_QUIZ_THIS_SESSION);
        mQUnsendedViewString = getString(R.string.VIEW_UNSENT_COUNT_QUIZ);
//        mASendedFromThisDeviceViewString = getString(R.string.sended_a_from_this_device_string);
//        mASendedInSessionViewString = getString(R.string.sended_a_in_session_string);
        mAUnsendedViewString = getString(R.string.VIEW_UNSENT_COUNT_AUDIO);
//        mPSendedFromThisDeviceViewString = getString(R.string.sended_p_from_this_device_string);
//        mPSendedInSessionViewString = getString(R.string.sended_p_in_session_string);
        mPUnsendedViewString = getString(R.string.VIEW_UNSENT_COUNT_PHOTO);
    }

    private void updateData(final SyncViewModel pSyncViewModel) {
        final int mQSendedFromThisDeviceCount = pSyncViewModel.getmSentQuestionnaireModelsFromThisDevice().size();
        final int mQSendedInSessionCount = pSyncViewModel.getSentQuestionnaireModelsInSession(getContext());
        final int mQUnsendedCount = pSyncViewModel.getmNotSentQuestionnaireModels().size();
        final int mAUnsendedCount = pSyncViewModel.getmNotSendedAudio().size();
        final int mPUnsendedCount = pSyncViewModel.getmNotSendedPhoto().size();
        final boolean hasReserveChannel = pSyncViewModel.hasReserveChannel();

        final BaseActivity activity = getBaseActivity();

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
                        new SendQuestionnairesByUserModelExecutable(getBaseActivity(), mUserModel, SyncFragment.this, false).execute();
                    }
                });

                UiUtils.setButtonEnabled(mSendPhotoButton, mPUnsendedCount > 0);
                mSendPhotoButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View view) {
                        if (mQUnsendedCount > 0) {
                            showToast(getString(R.string.NOTIFICATION_PLEASE_SEND_QUIZ));

                            return;
                        }

                        new PhotosSendingByUserModelExecutable(getBaseActivity(), mUserModel, SyncFragment.this).execute();
                    }
                });

                UiUtils.setButtonEnabled(mSendAudioButton, mAUnsendedCount > 0);
                mSendAudioButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(final View view) {
                        if (mQUnsendedCount > 0) {
                            showToast(getString(R.string.NOTIFICATION_PLEASE_SEND_QUIZ));

                            return;
                        }
                        new AudiosSendingByUserModelExecutable(getBaseActivity(), mUserModel, SyncFragment.this).execute();
                    }
                });
            }
        });
    }

    @Override
    public void onStarting() {
        if (isAdded()) {
            showToast(getString(R.string.NOTIFICATION_SENDING));
        }
//        showProgressBar();
    }

    @Override
    public void onSuccess() {
//        hideProgressBar();
//
        if (isAdded()) {
            updateData(new SyncInfoExecutable(getContext()).execute());
        }
    }

    @Override
    public void onError(final Exception pException) {
//        hideProgressBar();

        if (isAdded()) {
            showToast(pException.toString());

            updateData(new SyncInfoExecutable(getContext()).execute());
        }
    }
}