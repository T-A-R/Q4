package pro.quizer.quizer3.executable;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.Log;

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
                alertDialog.setPositiveButton(R.string.button_send, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        SmsUtils.sendSms(mBaseActivity, getCallback(), readyToSendStages, null);
                    }
                });
                alertDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        onError(new Exception(pBaseActivity.getString(R.string.notification_cancel_sending_sms)));
                    }
                });

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

        MainActivity.addLog(mUserModel.getLogin(), Constants.LogType.SERVER, Constants.LogObject.QUESTIONNAIRE, mBaseActivity.getString(R.string.send_quiz), Constants.LogResult.SENT, mBaseActivity.getString(R.string.send_quiz_attempt), json);
        Log.d(TAG, "================== sendViaInternetWithRetrofit: =====================");
        QuizerAPI.sendQuestionnaires(mServerUrl, json, this);
    }

    @Override
    public void onSendQuestionnaires(ResponseBody responseBody) {
        if (responseBody == null) {
            MainActivity.addLog(mUserModel.getLogin(), Constants.LogType.SERVER, Constants.LogObject.QUESTIONNAIRE, mBaseActivity.getString(R.string.send_quiz), Constants.LogResult.ERROR, mBaseActivity.getString(R.string.log_error_201_desc), null);
            onError(new Exception(mBaseActivity.getString(R.string.server_response_error) + " " + mBaseActivity.getString(R.string.error_201)));
            Log.d(TAG, "onSendQuestionnaires: responseBody = null!");
            return;
        }

        String responseJson = null;
        try {
            responseJson = responseBody.string();
        } catch (IOException e) {
            MainActivity.addLog(mUserModel.getLogin(), Constants.LogType.SERVER, Constants.LogObject.QUESTIONNAIRE, mBaseActivity.getString(R.string.send_quiz), Constants.LogResult.ERROR, mBaseActivity.getString(R.string.log_error_202_desc), null);
            onError(new Exception(mBaseActivity.getString(R.string.server_response_error) + " " + mBaseActivity.getString(R.string.error_202)));
            return;
        }
        DeletingListResponseModel deletingListResponseModel = null;

        try {
            deletingListResponseModel = new GsonBuilder().create().fromJson(responseJson, DeletingListResponseModel.class);
        } catch (Exception pE) {
            MainActivity.addLog(mUserModel.getLogin(), Constants.LogType.SERVER, Constants.LogObject.QUESTIONNAIRE, mBaseActivity.getString(R.string.send_quiz), Constants.LogResult.ERROR, mBaseActivity.getString(R.string.log_error_203_desc), responseJson);
            onError(new Exception(mBaseActivity.getString(R.string.server_response_error) + " " + mBaseActivity.getString(R.string.error_203)));
            return;
        }

        if (deletingListResponseModel != null) {
            SPUtils.saveSendTimeDifference(mBaseActivity, deletingListResponseModel.getServerTime());

            if (deletingListResponseModel.getQuotas() != null && deletingListResponseModel.getQuotas().size() > 0) {
                Log.d(TAG, "????????? onSendQuestionnaires: UP " + mUserModel.getUser_project_id());
                List<QuotaR> quotaRList = new ArrayList<>();
                for(QuotaModel model : deletingListResponseModel.getQuotas()) {
                    quotaRList.add(new QuotaR(model.getSequence(), model.getLimit(), model.getSent()));
                }
                mBaseActivity.getMainDao().clearQuotaR();
                mBaseActivity.getMainDao().insertQuotaR(quotaRList);
//                quotaUpdate();
            }

            if (deletingListResponseModel.getResult() != 0) {
                final List<String> tokensToRemove = deletingListResponseModel.getAccepted();

                if (tokensToRemove == null || tokensToRemove.isEmpty()) {
                    MainActivity.addLog(mUserModel.getLogin(), Constants.LogType.SERVER, Constants.LogObject.QUESTIONNAIRE, mBaseActivity.getString(R.string.send_quiz), Constants.LogResult.ERROR, mBaseActivity.getString(R.string.error_204) + mBaseActivity.getString(R.string.empty_tokens_list_error), responseJson);
                    onError(new Exception(mBaseActivity.getString(R.string.empty_tokens_list_error) + " " + mBaseActivity.getString(R.string.error_204)));
                } else {
                    MainActivity.addLog(mUserModel.getLogin(), Constants.LogType.SERVER, Constants.LogObject.QUESTIONNAIRE, mBaseActivity.getString(R.string.send_quiz), Constants.LogResult.SUCCESS, mBaseActivity.getString(R.string.quiz_sent), null);

                    for (final String token : tokensToRemove) {
                        try {
                            MainActivity.addLog(mUserModel.getLogin(), Constants.LogType.DATABASE, Constants.LogObject.QUESTIONNAIRE, mBaseActivity.getString(R.string.set_quiz_status), Constants.LogResult.SENT, mBaseActivity.getString(R.string.set_sent_quiz_status), null);
                            mBaseActivity.getMainDao().insertToken(new TokensCounterR(token, mUserModel.getUser_id()));
                            mBaseActivity.getMainDao().deleteQuestionnaireByToken(token);

                        } catch (Exception e) {
                            MainActivity.addLog(mUserModel.getLogin(), Constants.LogType.DATABASE, Constants.LogObject.QUESTIONNAIRE, mBaseActivity.getString(R.string.set_quiz_status), Constants.LogResult.ERROR, mBaseActivity.getString(R.string.db_save_error), e.getMessage());
                        }
                    }

                    try {
                        mBaseActivity.getMainDao().clearWarningsR();
                    } catch (Exception e) {
                        MainActivity.addLog(mUserModel.getLogin(), Constants.LogType.DATABASE, Constants.LogObject.WARNINGS, mBaseActivity.getString(R.string.clear_warnings_db), Constants.LogResult.ERROR, mBaseActivity.getString(R.string.db_clear_error), e.getMessage());
                    }
                    onSuccess();
                }
            } else {
                onError(new Exception(deletingListResponseModel.getError() + " " + mBaseActivity.getString(R.string.error_205)));
                MainActivity.addLog(mUserModel.getLogin(), Constants.LogType.SERVER, Constants.LogObject.QUOTA, mBaseActivity.getString(R.string.send_quiz), Constants.LogResult.ERROR, " " + mBaseActivity.getString(R.string.error_205) + deletingListResponseModel.getError(), null);

            }
        } else {
            onError(new Exception(mBaseActivity.getString(R.string.server_response_error) + " " + mBaseActivity.getString(R.string.error_206)));
        }
    }
}
