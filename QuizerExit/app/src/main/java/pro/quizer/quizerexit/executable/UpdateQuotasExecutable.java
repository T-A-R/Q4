package pro.quizer.quizerexit.executable;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import okhttp3.ResponseBody;
import pro.quizer.quizerexit.API.QuizerAPI;
import pro.quizer.quizerexit.Constants;
import pro.quizer.quizerexit.R;
import pro.quizer.quizerexit.activity.BaseActivity;
import pro.quizer.quizerexit.database.model.UserModelR;
import pro.quizer.quizerexit.model.config.ConfigModel;
import pro.quizer.quizerexit.model.request.QuotaRequestModel;
import pro.quizer.quizerexit.model.response.QuotaResponseModel;
import pro.quizer.quizerexit.utils.SPUtils;

import static pro.quizer.quizerexit.activity.BaseActivity.TAG;

public class UpdateQuotasExecutable extends BaseExecutable implements QuizerAPI.GetQuotasCallback {

    private final Context mContext;
    private BaseActivity baseActivity;
    private UserModelR userModel;
    private ConfigModel configModel;
    private int userProjectId;

    public UpdateQuotasExecutable(final Context pContext, final ICallback pCallback) {
        super(pCallback);

        mContext = pContext;
    }

    @Override
    public void execute() {
        onStarting();

        baseActivity = (BaseActivity) mContext;
        userModel = baseActivity.getCurrentUser();
        configModel = userModel.getConfigR();
        userProjectId = userModel.getUser_project_id();

        QuotaRequestModel requestModel = new QuotaRequestModel(configModel.getLoginAdmin(), userModel.getPassword(), userModel.getLogin());

        Gson gson = new Gson();
        String json = gson.toJson(requestModel);

        String mServerUrl = configModel.getServerUrl();

        BaseActivity.addLog(userModel.getLogin(), Constants.LogType.SERVER, Constants.LogObject.QUOTA, "Получение квот", Constants.LogResult.SENT, "Отправлен запрос на сервер");

        QuizerAPI.getQuotas(mServerUrl, json, this);
    }

    @Override
    public void onGetQuotasCallback(ResponseBody responseBody) {
        if (responseBody == null) {
            onError(new Exception(mContext.getString(R.string.NOTIFICATION_ERROR_CANNOT_UPDATE_QUOTAS) + " Ошибка: 1.01"));
            return;
        }
        String responseJson;
        try {
            responseJson = responseBody.string();
        } catch (IOException e) {
            onError(new Exception(mContext.getString(R.string.NOTIFICATION_ERROR_CANNOT_UPDATE_QUOTAS) + " Ошибка: 1.02"));
            return;
        }
        QuotaResponseModel quotaResponseModel;

        try {
            quotaResponseModel = new GsonBuilder().create().fromJson(responseJson, QuotaResponseModel.class);
        } catch (final Exception pE) {
            onError(new Exception(mContext.getString(R.string.NOTIFICATION_ERROR_CANNOT_UPDATE_QUOTAS) + " Ошибка: 1.03"));
            return;
        }

        if (quotaResponseModel != null) {
            SPUtils.saveQuotaTimeDifference(mContext, quotaResponseModel.getServerTime());

            if (quotaResponseModel.getResult() != 0) {
                try {
                    BaseActivity.addLog(userModel.getLogin(), Constants.LogType.DATABASE, Constants.LogObject.QUOTA, "Сохранение квот", Constants.LogResult.SENT, "Сохранение квот в базе данных");

                    BaseActivity.getDao().updateQuotas(responseJson, userProjectId);
                } catch (Exception e) {
                    Log.d(TAG, mContext.getString(R.string.DB_SAVE_ERROR));
                    BaseActivity.addLog(userModel.getLogin(), Constants.LogType.DATABASE, Constants.LogObject.QUOTA, "Сохранение квот", Constants.LogResult.ERROR, e.getMessage());

                }

                onSuccess();
            } else {
                onError(new Exception(mContext.getString(R.string.NOTIFICATION_ERROR_CANNOT_UPDATE_QUOTAS) + " Ошибка: 1.04"));
                return;
            }
        } else {
            onError(new Exception(mContext.getString(R.string.NOTIFICATION_ERROR_CANNOT_UPDATE_QUOTAS) + " Ошибка: 1.05"));
            return;
        }
    }
}
