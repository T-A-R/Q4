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
import pro.quizer.quizerexit.executable.ICallback;
import pro.quizer.quizerexit.executable.SendQuestionnairesByUserModelExecutable;
import pro.quizer.quizerexit.executable.SyncInfoExecutable;
import pro.quizer.quizerexit.model.database.UserModel;
import pro.quizer.quizerexit.model.view.SyncViewModel;

public class SyncFragment extends BaseFragment implements ICallback {

    private TextView mUserNameTitle;

    private Button mSendDataButton;
    private Button mSendAudioButton;
    private Button mSendPhotoButton;
    private Button mSyncSms;

    private TextView mQSendedFromThisDeviceView;
    private TextView mQSendedInSessionView;
    private TextView mQUnsendedView;

    private TextView mASendedFromThisDeviceView;
    private TextView mASendedInSessionView;
    private TextView mAUnsendedView;

    private TextView mPSendedFromThisDeviceView;
    private TextView mPSendedInSessionView;
    private TextView mPUnsendedView;

    private UserModel mUserModel;

    private String mQSendedFromThisDeviceViewString;
    private String mQSendedInSessionViewString;
    private String mQUnsendedViewString;
    private String mASendedFromThisDeviceViewString;
    private String mASendedInSessionViewString;
    private String mAUnsendedViewString;
    private String mPSendedFromThisDeviceViewString;
    private String mPSendedInSessionViewString;
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

        updateData(new SyncInfoExecutable().execute());
    }


    private void initViews(final View pView) {
        mUserModel = getBaseActivity().getCurrentUser();

        mUserNameTitle = pView.findViewById(R.id.user_name_title);
        mUserNameTitle.setText(mUserModel.login);

        mSendDataButton = pView.findViewById(R.id.send_q);
        mSendAudioButton = pView.findViewById(R.id.send_audio);
        mSendPhotoButton = pView.findViewById(R.id.send_photo);
        mSyncSms = pView.findViewById(R.id.sync_sms);

        mQSendedFromThisDeviceView = pView.findViewById(R.id.sended_q_from_this_device);
        mQSendedInSessionView = pView.findViewById(R.id.sended_q_in_session);
        mQUnsendedView = pView.findViewById(R.id.unsended_q);

        mASendedFromThisDeviceView = pView.findViewById(R.id.sended_audio_from_this_device);
        mASendedInSessionView = pView.findViewById(R.id.sended_audio_in_session);
        mAUnsendedView = pView.findViewById(R.id.unsended_audio);

        mPSendedFromThisDeviceView = pView.findViewById(R.id.sended_photo_from_this_device);
        mPSendedInSessionView = pView.findViewById(R.id.sended_photo_in_session);
        mPUnsendedView = pView.findViewById(R.id.unsended_photo);
    }

    private void initStrings() {
        mQSendedFromThisDeviceViewString = getString(R.string.sended_q_from_this_device_string);
        mQSendedInSessionViewString = getString(R.string.sended_q_in_session_string);
        mQUnsendedViewString = getString(R.string.count_unsended_questionnaires);
        mASendedFromThisDeviceViewString = getString(R.string.sended_a_from_this_device_string);
        mASendedInSessionViewString = getString(R.string.sended_a_in_session_string);
        mAUnsendedViewString = getString(R.string.count_unsended_audio_files);
        mPSendedFromThisDeviceViewString = getString(R.string.sended_p_from_this_device_string);
        mPSendedInSessionViewString = getString(R.string.sended_p_in_session_string);
        mPUnsendedViewString = getString(R.string.count_unsended_photo_files);
    }

    private void updateData(final SyncViewModel pSyncViewModel) {
        final int mQSendedFromThisDeviceCount = pSyncViewModel.getQSendedFromThisDeviceCount();
        final int mQSendedInSessionCount = pSyncViewModel.getQSendedInSessionCount();
        final int mQUnsendedCount = pSyncViewModel.getQUnsendedCount();
        final int mASendedFromThisDeviceCount = pSyncViewModel.getASendedFromThisDeviceCount();
        final int mASendedInSessionCount = pSyncViewModel.getASendedInSessionCount();
        final int mAUnsendedCount = pSyncViewModel.getAUnsendedCount();
        final int mPSendedFromThisDeviceCount = pSyncViewModel.getPSendedFromThisDeviceCount();
        final int mPSendedInSessionCount = pSyncViewModel.getPSendedInSessionCount();
        final int mPUnsendedCount = pSyncViewModel.getPUnsendedCount();

        getBaseActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                mQSendedFromThisDeviceView.setText(String.format(mQSendedFromThisDeviceViewString, mQSendedFromThisDeviceCount));
                mQSendedInSessionView.setText(String.format(mQSendedInSessionViewString, mQSendedInSessionCount));
                mQUnsendedView.setText(String.format(mQUnsendedViewString, mQUnsendedCount));

                mASendedFromThisDeviceView.setText(String.format(mASendedFromThisDeviceViewString, mASendedFromThisDeviceCount));
                mASendedInSessionView.setText(String.format(mASendedInSessionViewString, mASendedInSessionCount));
                mAUnsendedView.setText(String.format(mAUnsendedViewString, mAUnsendedCount));

                mPSendedFromThisDeviceView.setText(String.format(mPSendedFromThisDeviceViewString, mPSendedFromThisDeviceCount));
                mPSendedInSessionView.setText(String.format(mPSendedInSessionViewString, mPSendedInSessionCount));
                mPUnsendedView.setText(String.format(mPUnsendedViewString, mPUnsendedCount));
            }
        });

        mSendDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SendQuestionnairesByUserModelExecutable(getBaseActivity(), mUserModel, SyncFragment.this).execute();
            }
        });
    }

    @Override
    public void onStarting() {
        showProgressBar();
    }

    @Override
    public void onSuccess() {
        hideProgressBar();

        updateData(new SyncInfoExecutable().execute());
    }

    @Override
    public void onError(Exception pException) {
        hideProgressBar();

        updateData(new SyncInfoExecutable().execute());
    }
}