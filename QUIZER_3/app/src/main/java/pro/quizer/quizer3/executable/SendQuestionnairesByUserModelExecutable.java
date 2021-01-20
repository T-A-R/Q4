package pro.quizer.quizer3.executable;

import androidx.appcompat.app.AlertDialog;

import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import pro.quizer.quizer3.API.QuizerAPI;
import pro.quizer.quizer3.Constants;
import pro.quizer.quizer3.MainActivity;
import pro.quizer.quizer3.R;
import pro.quizer.quizer3.database.models.QuotaR;
import pro.quizer.quizer3.database.models.TokensCounterR;
import pro.quizer.quizer3.database.models.UserModelR;
import pro.quizer.quizer3.model.config.ConfigModel;
import pro.quizer.quizer3.API.models.request.QuestionnaireListRequestModel;
import pro.quizer.quizer3.API.models.response.DeletingListResponseModel;
import pro.quizer.quizer3.model.config.ElementModelNew;
import pro.quizer.quizer3.model.quota.QuotaModel;
import pro.quizer.quizer3.model.sms.SmsStage;
import pro.quizer.quizer3.model.view.SmsViewModel;
import pro.quizer.quizer3.utils.DateUtils;
import pro.quizer.quizer3.utils.NetworkUtils;
import pro.quizer.quizer3.utils.SPUtils;
import pro.quizer.quizer3.utils.SmsUtils;

import static pro.quizer.quizer3.MainActivity.TAG;

public class SendQuestionnairesByUserModelExecutable extends BaseExecutable implements QuizerAPI.SendQuestionnairesCallback {

    private final String mServerUrl;
    private final MainActivity mBaseActivity;
    private final UserModelR mUserModel;
    private final boolean mIsShowAlertDialog;
    private final boolean isFromService;

    public SendQuestionnairesByUserModelExecutable(final MainActivity pBaseActivity, final UserModelR pUserModel, final ICallback pCallback, final boolean pIsShowAlertDialog) {
        super(pCallback);
        final ConfigModel configModel = pBaseActivity.getConfig();
        mBaseActivity = pBaseActivity;
        mServerUrl = configModel.getServerUrl();
        mUserModel = pUserModel;
        mIsShowAlertDialog = pIsShowAlertDialog;
        this.isFromService = false;
    }

    public SendQuestionnairesByUserModelExecutable(final MainActivity pBaseActivity, final UserModelR pUserModel, final ICallback pCallback, final boolean pIsShowAlertDialog, final boolean isFromService) {
        super(pCallback);
        final ConfigModel configModel = pBaseActivity.getConfig();
        mBaseActivity = pBaseActivity;
        mServerUrl = configModel.getServerUrl();
        mUserModel = pUserModel;
        mIsShowAlertDialog = pIsShowAlertDialog;
        this.isFromService = isFromService;
    }

    @Override
    public void execute() {
        onStarting();

        if (NetworkUtils.hasConnection()) {
            sendViaInternetWithRetrofit();
        } else if (mBaseActivity.getConfig().hasReserveChannels()) {
            sendViaSms(mBaseActivity.createNewMap(mBaseActivity.getConfig().getProjectInfo().getElements()), mBaseActivity);
        } else {
            onError(new Exception(mBaseActivity.getString(R.string.notification_no_connection)));
        }
    }

    private void sendViaSms(final Map<Integer, ElementModelNew> mMap, final MainActivity pBaseActivity) {
        final SmsViewModel smsViewModel = new SmsViewModelExecutable(mMap, pBaseActivity).execute();
        final List<SmsStage> readyToSendStages = smsViewModel.getReadyToSendStages();

        if (readyToSendStages != null && !readyToSendStages.isEmpty()) {
            if (mIsShowAlertDialog) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(mBaseActivity, R.style.AlertDialogTheme);
                alertDialog.setCancelable(false);
                alertDialog.setTitle(R.string.dialog_sending_waves_via_sms);
                alertDialog.setMessage(pBaseActivity.getString(R.string.dialog_sending_waves_via_sms_confirmation) + mUserModel.getLogin() + " " + pBaseActivity.getString(R.string.with_sms));
                alertDialog.setPositiveButton(R.string.button_send, (dialog, which) -> SmsUtils.sendSms(mBaseActivity, getCallback(), readyToSendStages, null));
                alertDialog.setNegativeButton(R.string.cancel, (dialog, which) -> onError(new Exception(pBaseActivity.getString(R.string.notification_cancel_sending_sms))));

                if (!mBaseActivity.isFinishing()) {
                    alertDialog.show();
                }
            } else {
                SmsUtils.sendSms(mBaseActivity, getCallback(), readyToSendStages, null);
            }
        }

        onSuccess();
    }

    private void sendViaInternetWithRetrofit() {

        QuestionnaireListRequestModel requestModel;
        if (isFromService) {
            requestModel = new QuestionnaireListRequestModelExecutable(mBaseActivity, mUserModel, true).execute();
        } else {
            requestModel = new QuestionnaireListRequestModelExecutable(mBaseActivity, mUserModel, false).execute();
        }
        if (requestModel == null) {
            onSuccess();
            return;
        }

        if (isFromService) {
            requestModel.setFromService();
        }
        Gson gson = new Gson();
        String json = gson.toJson(requestModel);

        Log.d(TAG, "================== sendViaInternetWithRetrofit: =====================");
        QuizerAPI.sendQuestionnaires(mServerUrl, json, this);
    }

    @Override
    public void onSendQuestionnaires(ResponseBody responseBody) {
        setSendStatus();
        if (responseBody == null) {
            onError(new Exception(mBaseActivity.getString(R.string.server_response_error) + " " + mBaseActivity.getString(R.string.error_201)));
            Log.d(TAG, "onSendQuestionnaires: responseBody = null!");
            return;
        }

        String responseJson = null;
        try {
            responseJson = responseBody.string();
        } catch (IOException e) {
            e.printStackTrace();
            onError(new Exception(mBaseActivity.getString(R.string.server_response_error) + " " + mBaseActivity.getString(R.string.error_202)));
            return;
        }
        DeletingListResponseModel deletingListResponseModel = null;

        try {
            deletingListResponseModel = new GsonBuilder().create().fromJson(responseJson, DeletingListResponseModel.class);
        } catch (Exception pE) {
            pE.printStackTrace();
            onError(new Exception(mBaseActivity.getString(R.string.server_response_error) + " " + mBaseActivity.getString(R.string.error_203)));
            return;
        }

        if (deletingListResponseModel != null) {
            if (deletingListResponseModel.isProjectActive() != null) {
                try {
                    mBaseActivity.getMainDao().setProjectActive(deletingListResponseModel.isProjectActive());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            SPUtils.saveSendTimeDifference(mBaseActivity, deletingListResponseModel.getServerTime());

            if (deletingListResponseModel.getQuotas() != null && deletingListResponseModel.getQuotas().size() > 0) {
                List<QuotaR> quotaRList = new ArrayList<>();
                Integer userProjectId = mBaseActivity.getCurrentUser().getConfigR().getUserProjectId();
                if (userProjectId == null)
                    userProjectId = mBaseActivity.getCurrentUser().getUser_project_id();
                for (QuotaModel model : deletingListResponseModel.getQuotas()) {
                    quotaRList.add(new QuotaR(model.getSequence(), model.getLimit(), model.getSent(), userProjectId));
                }
                try {
                    mBaseActivity.getMainDao().clearQuotaR(userProjectId);
                    mBaseActivity.getMainDao().insertQuotaR(quotaRList);
                    mBaseActivity.setSettings(Constants.Settings.QUOTA_TIME, String.valueOf(DateUtils.getCurrentTimeMillis()));
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            if (deletingListResponseModel.getResult() != 0) {
                final List<String> tokensToRemove = deletingListResponseModel.getAccepted();

                if (tokensToRemove == null || tokensToRemove.isEmpty()) {
                    onError(new Exception(deletingListResponseModel.getMessage() + " " + mBaseActivity.getString(R.string.error_204)));
                } else {
                    for (final String token : tokensToRemove) {
                        try {
                            mBaseActivity.addLog(Constants.LogObject.QUESTIONNAIRE, "SENT", Constants.LogResult.SUCCESS, token, null);
                            TokensCounterR tokensCounter = new TokensCounterR();
                            tokensCounter.setToken(token);
                            tokensCounter.setUser_id(mUserModel.getUser_id());
                            tokensCounter.setName(mBaseActivity.getSettings().getUser_name());
                            tokensCounter.setDate(mBaseActivity.getSettings().getUser_date());
                            tokensCounter.setUser_project_id(mUserModel.getUser_project_id());
                            mBaseActivity.getMainDao().insertToken(tokensCounter);
                            mBaseActivity.getMainDao().deleteQuestionnaireByToken(token);
                            mBaseActivity.getMainDao().deleteElementDatabaseModelByToken(token);
                            mBaseActivity.addLog(Constants.LogObject.QUESTIONNAIRE, "DELETE", Constants.LogResult.SUCCESS, token, null);
                            mBaseActivity.setSettings(Constants.Settings.SENT_TIME, String.valueOf(DateUtils.getCurrentTimeMillis()));

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    try {
                        mBaseActivity.getMainDao().clearWarningsR();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    onSuccess();

                    final String message = deletingListResponseModel.getMessage();
                    if (message != null)
                        mBaseActivity.runOnUiThread(() -> Toast.makeText(mBaseActivity, message, Toast.LENGTH_LONG).show());
                    else
                        mBaseActivity.runOnUiThread(() -> Toast.makeText(mBaseActivity, R.string.message_questionnaires_sent, Toast.LENGTH_LONG).show());
                }
            } else {
                onError(new Exception(deletingListResponseModel.getError() + " " + mBaseActivity.getString(R.string.error_205)));
            }
        } else {
            onError(new Exception(mBaseActivity.getString(R.string.server_response_error) + " " + mBaseActivity.getString(R.string.error_206)));
        }
    }

    private void setSendStatus() {
        try {
            mBaseActivity.getMainDao().setQuestionnaireSentOnline(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
