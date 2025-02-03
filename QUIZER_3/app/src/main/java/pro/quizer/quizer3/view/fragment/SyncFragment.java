package pro.quizer.quizer3.view.fragment;

import androidx.appcompat.app.AlertDialog;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.util.List;

import okhttp3.ResponseBody;
import pro.quizer.quizer3.API.QuizerAPI;
import pro.quizer.quizer3.API.models.request.RegistrationRequestModel;
import pro.quizer.quizer3.Constants;
import pro.quizer.quizer3.MainActivity;
import pro.quizer.quizer3.R;
import pro.quizer.quizer3.database.models.PhotoAnswersR;
import pro.quizer.quizer3.database.models.RegistrationR;
import pro.quizer.quizer3.database.models.UserModelR;
import pro.quizer.quizer3.executable.ICallback;
import pro.quizer.quizer3.executable.SendQuestionnairesByUserModelExecutable;
import pro.quizer.quizer3.executable.SyncInfoExecutable;
import pro.quizer.quizer3.executable.files.AudiosSendingByUserModelExecutable;
import pro.quizer.quizer3.executable.files.PhotosAnswersSendingExecutable;
import pro.quizer.quizer3.executable.files.PhotosSendingByUserModelExecutable;
import pro.quizer.quizer3.model.view.SyncViewModel;
import pro.quizer.quizer3.utils.Internet;
import pro.quizer.quizer3.utils.SmsUtils;
import pro.quizer.quizer3.utils.UiUtils;
import pro.quizer.quizer3.view.Anim;
import pro.quizer.quizer3.view.Toolbar;

public class SyncFragment extends ScreenFragment implements View.OnClickListener, QuizerAPI.SendRegCallback, ICallback {

    private Button mSendDataButton;
    private Button mSendAudioButton;
    private Button mSendPhotoButton;
    private Button mSendPhotoAnswersButton;
    private Button mSyncSms;
    private Button mDelete;
    private Button mSendReg;
    private TextView mProjectStatusView;
    private TextView mUnfinishedView;
    private TextView mQUnsendedView;
    private TextView mAUnsendedView;
    private TextView mPUnsendedView;
    private TextView mPUnsendedPAView;

    private UserModelR mUserModel;

    private String mQUnsendedViewString;
    private String mAUnsendedViewString;
    private String mPUnsendedViewString;
    private String mPUnsendedPAViewString;

    public SyncFragment() {
        super(R.layout.fragment_sync);
    }

    @Override
    protected void onReady() {

        initViews();
        MainFragment.enableSideMenu(true, getMainActivity().isExit());
        initStrings();
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
        mSendPhotoAnswersButton = findViewById(R.id.send_photo_answers);
        mSyncSms = findViewById(R.id.sync_sms);
        mUnfinishedView = findViewById(R.id.have_unfinished);
        mQUnsendedView = findViewById(R.id.unsended_q);
        mAUnsendedView = findViewById(R.id.unsended_audio);
        mPUnsendedView = findViewById(R.id.unsended_photo);
        mPUnsendedPAView = findViewById(R.id.unsended_photo_answers);
        mDelete = findViewById(R.id.btn_delete);
        mSendReg = findViewById(R.id.send_reg);

        mSyncSms.setOnClickListener(this);
        mDelete.setOnClickListener(this);
        mSendReg.setOnClickListener(this);

        checkReg();

        cont.startAnimation(Anim.getAppear(getContext()));
        mDelete.startAnimation(Anim.getAppearSlide(getContext(), 500));
        mSendDataButton.startAnimation(Anim.getAppearSlide(getContext(), 500));
        mSendAudioButton.startAnimation(Anim.getAppearSlide(getContext(), 500));
        mSendPhotoButton.startAnimation(Anim.getAppearSlide(getContext(), 500));
        mSendPhotoAnswersButton.startAnimation(Anim.getAppearSlide(getContext(), 500));
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
        } else if (view == mSendReg) {
            sendReg(getDao().getRegistrationR(getCurrentUserId()));
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
                        getObjectBoxDao().clearPrevElementsR();
                        getObjectBoxDao().clearElementPassedR();
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
        mPUnsendedPAViewString = getString(R.string.view_unsent_count_photo_answers);
    }

    private void updateData(final SyncViewModel pSyncViewModel) {
        final int mQUnsendedCount = pSyncViewModel.getmNotSentQuestionnaireModels().size();
        final int mAUnsendedCount = pSyncViewModel.getmNotSendedAudio().size();
        final int mPUnsendedCount = pSyncViewModel.getmNotSendedPhoto().size();
        final Integer mPUnsendedPAnswersCount = pSyncViewModel.getmNotSendedPhotoAnswers();
        final boolean hasReserveChannel = pSyncViewModel.hasReserveChannel();
        final boolean hasUnfinishedQuiz = pSyncViewModel.hasUnfinishedQuiz();

        final MainActivity activity = (MainActivity) getActivity();

        if (activity == null) {
            return;
        }

        activity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                RegistrationR reg = getDao().getRegistrationR(getCurrentUserId());
                if (hasReserveChannel && activity.isExit() && ((reg != null && !reg.smsClosed()) || activity.getConfig().getUserSettings().getActive_registration_data() != null)) {
                    showSmsButton();
                } else {
                    hideSmsButton();
                }

                if (activity.getSettings().isProject_is_active()) {
                    mProjectStatusView.setVisibility(View.GONE);
                    UiUtils.setButtonEnabled(mSendDataButton, mQUnsendedCount > 0);
                    UiUtils.setButtonEnabled(mSendPhotoButton, mPUnsendedCount > 0);
                    UiUtils.setButtonEnabled(mSendAudioButton, mAUnsendedCount > 0);
                    if (mPUnsendedPAnswersCount > 0) {
                        mSendPhotoAnswersButton.setVisibility(View.VISIBLE);
                        mPUnsendedPAView.setVisibility(View.VISIBLE);
                        UiUtils.setButtonEnabled(mSendPhotoAnswersButton, true);
                        UiUtils.setTextOrHide(mPUnsendedPAView, (String.format(mPUnsendedPAViewString, mPUnsendedPAnswersCount)));
                    } else {
                        mSendPhotoAnswersButton.setVisibility(View.GONE);
                        mPUnsendedPAView.setVisibility(View.GONE);
                    }
                } else {
                    mProjectStatusView.setVisibility(View.VISIBLE);
                    UiUtils.setButtonEnabledLightGreen(mSendDataButton, mQUnsendedCount > 0);
                    UiUtils.setButtonEnabledLightGreen(mSendPhotoButton, mPUnsendedCount > 0);
                    UiUtils.setButtonEnabledLightGreen(mSendAudioButton, mAUnsendedCount > 0);
                    if (mPUnsendedPAnswersCount > 0) {
                        mSendPhotoAnswersButton.setVisibility(View.VISIBLE);
                        mPUnsendedPAView.setVisibility(View.VISIBLE);
                        UiUtils.setButtonEnabledLightGreen(mSendPhotoAnswersButton, true);
                        UiUtils.setTextOrHide(mPUnsendedPAView, (String.format(mPUnsendedPAViewString, mPUnsendedPAnswersCount)));
                    } else {
                        mSendPhotoAnswersButton.setVisibility(View.GONE);
                        mPUnsendedPAView.setVisibility(View.GONE);
                    }
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
                    activity.addLog(Constants.LogObject.AUDIO, "key", Constants.LogResult.ATTEMPT, "Press send audio", null);
                    if (hasUnfinishedOrSend(mQUnsendedCount, hasUnfinishedQuiz)) return;

                    new AudiosSendingByUserModelExecutable((MainActivity) getActivity(), mUserModel, SyncFragment.this).execute();
                });

                mSendPhotoAnswersButton.setOnClickListener(v -> sendPhotoAnswers());
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

    private void checkReg() {
        new Thread(() -> {
            if (getMainActivity().isExit() && getMainActivity().getConfig().has_registration()) {
                RegistrationR reg = getDao().getRegistrationR(getCurrentUserId());
                if (reg != null && reg.notSent()) {
                    mSendReg.setVisibility(View.VISIBLE);
                }
            }
        }).start();
    }

    private void sendReg(RegistrationR registration) {
        String url;
        url = getCurrentUser().getConfigR().getExitHost() != null ? getCurrentUser().getConfigR().getExitHost() + Constants.Default.REG_URL : null;
        Log.d("T-L.Reg3Fragment", "REG EXIT URL: " + url);
        List<File> photos = getMainActivity().getRegPhotosByUserId(registration.getUser_id());
        Log.d("T-L.Reg3Fragment", "====: 1");
        if (photos == null || photos.isEmpty()) {
            Log.d("T-L.Reg3Fragment", "====: 2");
            showToast(getString(R.string.no_reg_photo));
            return;
        }
        Log.d("T-L.Reg3Fragment", "====: 3");

        try {
            if (Internet.hasConnection(getMainActivity()) && url != null) {
                UiUtils.setButtonEnabled(mSendReg, false);
                Log.d("T-L.Reg3Fragment", "Отправка регистрации...");
                getMainActivity().addLog(Constants.LogObject.REGISTRATION, "SEND_REG", Constants.LogResult.ATTEMPT, registration.getUser_id() + "/" + registration.getPhone(), url);
                QuizerAPI.sendReg(url, photos, new RegistrationRequestModel(
                        getDao().getKey(),
                        registration.getUser_id(),
                        registration.getUik_number(),
                        registration.getPhone(),
                        registration.getGps(),
                        registration.getGps_network(),
                        registration.getGps_time(),
                        registration.getGps_time_network(),
                        registration.getReg_time(),
                        false
                ), registration.getId(), "jpeg", this);
            } else {
                UiUtils.setButtonEnabled(mSendReg, true);
                showToast("Нет доступа в интернет");
            }
            Log.d("T-L.Reg3Fragment", "====: 4");
        } catch (Exception e) {
            UiUtils.setButtonEnabled(mSendReg, true);
            e.printStackTrace();
            showToast("Нет доступа в интернет");
        }
    }

    @Override
    public void onSendRegCallback(ResponseBody response, Integer id) {
        if (response == null) {
            showToast("Нет ответа от сервера");
            getMainActivity().addLog(Constants.LogObject.REGISTRATION, "SEND_REG", Constants.LogResult.ERROR, "Нет ответа от сервера", null);
            UiUtils.setButtonEnabled(mSendReg, true);
            return;
        } else {
            try {
                if (id != null) {
                    getMainActivity().addLog(Constants.LogObject.REGISTRATION, "SEND_REG", Constants.LogResult.SUCCESS, "Сервер ответил 202", null);
                    showToast("Регистрация успешна");
                    getDao().setRegStatus(id, Constants.Registration.SENT);
                    UiUtils.setButtonEnabled(mSendReg, false);
                } else {
                    getMainActivity().addLog(Constants.LogObject.REGISTRATION, "SEND_REG", Constants.LogResult.ERROR, "ID = null", null);
                }
            } catch (Exception e) {
                e.printStackTrace();
                showToast("Ошибка регистрации");
                UiUtils.setButtonEnabled(mSendReg, true);
                getMainActivity().addLog(Constants.LogObject.REGISTRATION, "SEND_REG", Constants.LogResult.ERROR, "Регистрация. Ошибка сохранения статуса", e.toString());
            }
        }
    }

    private void sendPhotoAnswers() {
        getMainActivity().addLog(Constants.LogObject.PHOTOS, "key", Constants.LogResult.ATTEMPT, "Press send PHOTOS", null);
        List<PhotoAnswersR> list = getDao().getPhotoAnswersByStatus(Constants.LogStatus.READY_FOR_SEND);
        if (list != null && list.size() > 0) {
            new PhotosAnswersSendingExecutable(getMainActivity(), mUserModel, list, new ICallback() {
                @Override
                public void onStarting() {

                }

                @Override
                public void onSuccess() {
                    updateData(new SyncInfoExecutable(getContext()).execute());
                }

                @Override
                public void onError(Exception pException) {

                }
            }).execute();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        checkConfigTime(false);
    }
}

