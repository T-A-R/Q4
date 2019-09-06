package pro.quizer.quizerexit.executable;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.activeandroid.query.Update;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import pro.quizer.quizerexit.API.QuizerAPI;
import pro.quizer.quizerexit.Constants;
import pro.quizer.quizerexit.R;
import pro.quizer.quizerexit.activity.BaseActivity;
import pro.quizer.quizerexit.database.model.UserModelR;
import pro.quizer.quizerexit.model.QuestionnaireStatus;
import pro.quizer.quizerexit.model.config.ConfigModel;
import pro.quizer.quizerexit.model.config.ElementModel;
import pro.quizer.quizerexit.model.request.QuestionnaireListRequestModel;
import pro.quizer.quizerexit.model.response.DeletingListResponseModel;
import pro.quizer.quizerexit.model.sms.SmsStage;
import pro.quizer.quizerexit.model.view.SmsViewModel;
import pro.quizer.quizerexit.utils.NetworkUtils;
import pro.quizer.quizerexit.utils.SPUtils;
import pro.quizer.quizerexit.utils.SmsUtils;

import static pro.quizer.quizerexit.activity.BaseActivity.TAG;

public class SendQuestionnairesByUserModelExecutable extends BaseExecutable implements QuizerAPI.SendQuestionnairesCallback {

    private final String mServerUrl;
    private final BaseActivity mBaseActivity;
    private final UserModelR mUserModel;
    private final boolean mIsShowAlertDialog;

    public SendQuestionnairesByUserModelExecutable(final BaseActivity pBaseActivity, final UserModelR pUserModel, final ICallback pCallback, final boolean pIsShowAlertDialog) {
        super(pCallback);

        final ConfigModel configModel = pUserModel.getConfigR();

        mBaseActivity = pBaseActivity;
        mServerUrl = configModel.getServerUrl();
        mUserModel = pUserModel;
        mIsShowAlertDialog = pIsShowAlertDialog;
    }

    @Override
    public void execute() {
        onStarting();

        if (NetworkUtils.hasConnection()) {
            sendViaInternetWithRetrofit();
        } else if (mUserModel.getConfigR().hasReserveChannels()) {
            sendViaSms(mBaseActivity.createNewMap(mUserModel.getConfigR().getProjectInfo().getElements()), mBaseActivity);
        } else {
            onError(new Exception(mBaseActivity.getString(R.string.NOTIFICATION_SENDING_ERROR_NO_CONNECTION)));
        }
    }

    private void sendViaSms(final Map<Integer, ElementModel> mMap, final BaseActivity pBaseActivity) {
        final SmsViewModel smsViewModel = new SmsViewModelExecutable(mMap, pBaseActivity).execute();
        final List<SmsStage> readyToSendStages = smsViewModel.getReadyToSendStages();

        if (readyToSendStages != null && !readyToSendStages.isEmpty()) {
            if (mIsShowAlertDialog) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(mBaseActivity, R.style.AlertDialogTheme);
                alertDialog.setCancelable(false);
                alertDialog.setTitle(R.string.DIALOG_SENDING_WAVES_VIA_SMS);
                alertDialog.setMessage(pBaseActivity.getString(R.string.DIALOG_SENDING_WAVES_VIA_SMS_CONFIRMATION) + mUserModel.getLogin() + " " + pBaseActivity.getString(R.string.WITH_SMS));
                alertDialog.setPositiveButton(R.string.VIEW_BUTTON_SEND, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        SmsUtils.sendSms(mBaseActivity, getCallback(), readyToSendStages, null);
                    }
                });
                alertDialog.setNegativeButton(R.string.VIEW_CANCEL, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        onError(new Exception(pBaseActivity.getString(R.string.NOTIFICATION_CANCEL_SMS_SENDING)));
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

        QuestionnaireListRequestModel requestModel = new QuestionnaireListRequestModelExecutable(mUserModel).execute();
        if (requestModel == null) {
            onSuccess();
            return;
        }
        Gson gson = new Gson();
        String json = gson.toJson(requestModel);

        BaseActivity.addLogWithData(mUserModel.getLogin(), Constants.LogType.SERVER, Constants.LogObject.QUESTIONNAIRE, mBaseActivity.getString(R.string.SEND_QUESTION), Constants.LogResult.SENT, "Отправка на сервер", json);

        QuizerAPI.sendQuestionnaires(mServerUrl, json, this);
    }

    @Override
    public void onSendQuestionnaires(ResponseBody responseBody) {
        if (responseBody == null) {
            BaseActivity.addLog(mUserModel.getLogin(), Constants.LogType.SERVER, Constants.LogObject.QUESTIONNAIRE, mBaseActivity.getString(R.string.SEND_QUESTION), Constants.LogResult.ERROR, "Ошибка 2.01 (Не получен или отрицательный ответ от сервера)");
            onError(new Exception(mBaseActivity.getString(R.string.NOTIFICATION_SERVER_RESPONSE_ERROR) + " Ошибка: 2.01"));
            Log.d(TAG, "onSendQuestionnaires: responseBody = null!");
            return;
        }

        String responseJson = null;
        try {
            responseJson = responseBody.string();
        } catch (IOException e) {
            BaseActivity.addLog(mUserModel.getLogin(), Constants.LogType.SERVER, Constants.LogObject.QUESTIONNAIRE, mBaseActivity.getString(R.string.SEND_QUESTION), Constants.LogResult.ERROR, "Ошибка 2.02 (Ошибка получения JSON из ответа сервера)");
            onError(new Exception(mBaseActivity.getString(R.string.NOTIFICATION_SERVER_RESPONSE_ERROR) + " Ошибка: 2.02"));
            return;
        }
        DeletingListResponseModel deletingListResponseModel = null;

        try {
            deletingListResponseModel = new GsonBuilder().create().fromJson(responseJson, DeletingListResponseModel.class);
        } catch (Exception pE) {
            BaseActivity.addLogWithData(mUserModel.getLogin(), Constants.LogType.SERVER, Constants.LogObject.QUESTIONNAIRE, mBaseActivity.getString(R.string.SEND_QUESTION), Constants.LogResult.ERROR, "Ошибка 2.03 (Ошибка парсинга JSON)", responseJson);
            onError(new Exception(mBaseActivity.getString(R.string.NOTIFICATION_SERVER_RESPONSE_ERROR) + " Ошибка: 2.03"));
            return;
        }

        if (deletingListResponseModel != null) {
            SPUtils.saveSendTimeDifference(mBaseActivity, deletingListResponseModel.getServerTime());

            if (deletingListResponseModel.getResult() != 0) {
                final List<String> tokensToRemove = deletingListResponseModel.getAccepted();

                if (tokensToRemove == null || tokensToRemove.isEmpty()) {
                    BaseActivity.addLogWithData(mUserModel.getLogin(), Constants.LogType.SERVER, Constants.LogObject.QUESTIONNAIRE, mBaseActivity.getString(R.string.SEND_QUESTION), Constants.LogResult.ERROR, "Ошибка 2.04" + mBaseActivity.getString(R.string.NOTIFICATION_SENDING_ERROR_EMPTY_TOKENS_LIST), responseJson);
                    onError(new Exception(mBaseActivity.getString(R.string.NOTIFICATION_SENDING_ERROR_EMPTY_TOKENS_LIST) + " Ошибка: 2.04"));
                } else {
                    SPUtils.addSendedQInSession(mBaseActivity, tokensToRemove.size());

                    BaseActivity.addLog(mUserModel.getLogin(), Constants.LogType.SERVER, Constants.LogObject.QUESTIONNAIRE, mBaseActivity.getString(R.string.SEND_QUESTION), Constants.LogResult.SUCCESS, "Анкеты отправлены");

                    for (final String token : tokensToRemove) {
                        try {
                            BaseActivity.addLog(mUserModel.getLogin(), Constants.LogType.DATABASE, Constants.LogObject.QUESTIONNAIRE, "Установка статуса SENT у анкеты", Constants.LogResult.SENT, "Статус отправки анкеты в базе данных установлен как SENT");
                            BaseActivity.getDao().setQuestionnaireStatus(QuestionnaireStatus.SENT, token);
                        } catch (Exception e) {
                            BaseActivity.addLogWithData(mUserModel.getLogin(), Constants.LogType.DATABASE, Constants.LogObject.QUESTIONNAIRE, "Установка статуса SENT у анкеты", Constants.LogResult.ERROR, "Ошибка записи а базу данных", e.getMessage());
                            Log.d(TAG, "Ошибка записи в базу данных.");
                        }
                    }

                    new UpdateQuotasExecutable(mBaseActivity, new ICallback() {

                        @Override
                        public void onStarting() {

                        }

                        @Override
                        public void onSuccess() {
                            Toast.makeText(mBaseActivity, R.string.NOTIFICATION_UPDATE_QUOTAS, Toast.LENGTH_SHORT).show();
                            BaseActivity.addLog(mUserModel.getLogin(), Constants.LogType.SERVER, Constants.LogObject.QUOTA, "Получение квот", Constants.LogResult.SUCCESS, "Список квот обновлен");
                        }

                        @Override
                        public void onError(Exception pException) {
                            Toast.makeText(mBaseActivity, R.string.NOTIFICATION_ERROR_CANNOT_UPDATE_QUOTAS + " Ошибка: 2.07", Toast.LENGTH_SHORT).show();
                            BaseActivity.addLogWithData(mUserModel.getLogin(), Constants.LogType.SERVER, Constants.LogObject.QUOTA, "Получение квот", Constants.LogResult.ERROR, " Ошибка: 1.07" + R.string.NOTIFICATION_ERROR_CANNOT_UPDATE_QUOTAS, pException.toString());
                        }
                    }).execute();

                    onSuccess();
                }
            } else {
                onError(new Exception(deletingListResponseModel.getError() + " Ошибка: 2.05"));
                BaseActivity.addLog(mUserModel.getLogin(), Constants.LogType.SERVER, Constants.LogObject.QUOTA, mBaseActivity.getString(R.string.SEND_QUESTION), Constants.LogResult.ERROR, " Ошибка: 2.05" + deletingListResponseModel.getError());

            }
        } else {
            onError(new Exception(mBaseActivity.getString(R.string.NOTIFICATION_SERVER_ERROR) + " Ошибка: 2.06"));
        }
    }
}
