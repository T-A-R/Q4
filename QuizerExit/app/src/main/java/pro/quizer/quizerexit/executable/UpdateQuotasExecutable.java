package pro.quizer.quizerexit.executable;

import android.content.Context;
import android.support.annotation.NonNull;

import com.activeandroid.query.Update;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.ResponseBody;
import pro.quizer.quizerexit.API.QuizerAPI;
import pro.quizer.quizerexit.Constants;
import pro.quizer.quizerexit.DoRequest;
import pro.quizer.quizerexit.R;
import pro.quizer.quizerexit.activity.BaseActivity;
import pro.quizer.quizerexit.database.model.UserModelR;
import pro.quizer.quizerexit.model.config.ConfigModel;
import pro.quizer.quizerexit.model.database.UserModel;
import pro.quizer.quizerexit.model.request.QuotaRequestModel;
import pro.quizer.quizerexit.model.response.QuotaResponseModel;
import pro.quizer.quizerexit.utils.SPUtils;

public class UpdateQuotasExecutable extends BaseExecutable implements QuizerAPI.GetQuotasCallback {

    private final Context mContext;
    private BaseActivity baseActivity;
    //    private  UserModel userModel;
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
//        configModel = userModel.getConfig();
//        userProjectId = userModel.user_project_id;
//
//        QuotaRequestModel requestModel = new QuotaRequestModel(configModel.getLoginAdmin(), userModel.password, userModel.login);
        configModel = userModel.getConfigR();
        userProjectId = userModel.getUser_project_id();

        QuotaRequestModel requestModel = new QuotaRequestModel(configModel.getLoginAdmin(), userModel.getPassword(), userModel.getLogin());

        Gson gson = new Gson();
        String json = gson.toJson(requestModel);

        String mServerUrl = configModel.getServerUrl();
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
//                new Update(UserModel.class)
//                        .set(UserModel.QUOTAS + " = ?", responseJson)
//                        .where(UserModel.USER_PROJECT_ID + " = ?", userProjectId).execute();
                BaseActivity.getDao().updateQuotas(responseJson, userProjectId);

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
