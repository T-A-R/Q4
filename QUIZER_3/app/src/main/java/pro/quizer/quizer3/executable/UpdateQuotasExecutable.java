package pro.quizer.quizer3.executable;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import pro.quizer.quizer3.API.QuizerAPI;
import pro.quizer.quizer3.Constants;
import pro.quizer.quizer3.MainActivity;
import pro.quizer.quizer3.R;
import pro.quizer.quizer3.database.models.QuotaR;
import pro.quizer.quizer3.database.models.UserModelR;
import pro.quizer.quizer3.model.config.ConfigModel;
import pro.quizer.quizer3.API.models.request.QuotaRequestModel;
import pro.quizer.quizer3.API.models.response.QuotaResponseModel;
import pro.quizer.quizer3.model.quota.QuotaModel;
import pro.quizer.quizer3.utils.DateUtils;
import pro.quizer.quizer3.utils.SPUtils;

import static pro.quizer.quizer3.MainActivity.TAG;

public class UpdateQuotasExecutable extends BaseExecutable implements QuizerAPI.GetQuotasCallback {

    private final Context mContext;
    private MainActivity mainActivity;
    private UserModelR userModel;
    private ConfigModel configModel;
    private Integer userProjectId;

    public UpdateQuotasExecutable(final Context pContext, final ICallback pCallback) {
        super(pCallback);

        mContext = pContext;
    }

    @Override
    public void execute() {
        onStarting();

        mainActivity = (MainActivity) mContext;
        userModel = mainActivity.getCurrentUser();
        configModel = mainActivity.getConfig();
        userProjectId = configModel.getUserProjectId();
        if (userProjectId == null)
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
            onError(new Exception(mContext.getString(R.string.load_quotas_error) + " " + mContext.getString(R.string.error_101)));
            return;
        }
        String responseJson;
        try {
            responseJson = responseBody.string();
        } catch (IOException e) {
            onError(new Exception(mContext.getString(R.string.load_quotas_error) + " " + mContext.getString(R.string.error_102)));
            return;
        }
//        mainActivity.copyToClipboard(responseJson);
        QuotaResponseModel quotaResponseModel;

        try {
            quotaResponseModel = new GsonBuilder().create().fromJson(responseJson, QuotaResponseModel.class);
        } catch (final Exception pE) {
            onError(new Exception(mContext.getString(R.string.load_quotas_error) + " " + mContext.getString(R.string.error_103)));
            return;
        }

        if (quotaResponseModel != null) {
            if(quotaResponseModel.isProjectActive() != null) {
                try {
                    mainActivity.getMainDao().setProjectActive(quotaResponseModel.isProjectActive());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            SPUtils.saveQuotaTimeDifference(mContext, quotaResponseModel.getServerTime());

            if (quotaResponseModel.getResult() != 0) {
                try {
                    Log.d(TAG, "onGetQuotasCallback: " + responseJson);
                    if(quotaResponseModel.getQuotas() != null && quotaResponseModel.getQuotas().size() > 0){
                        List<QuotaR> quotaRList = new ArrayList<>();
                        for(QuotaModel model : quotaResponseModel.getQuotas()) {
                            quotaRList.add(new QuotaR(model.getSequence(), model.getLimit(), model.getSent(), userProjectId, model.getQuota_id()));
                        }
                        mainActivity.getMainDao().clearQuotaR(userProjectId);
                        mainActivity.getMainDao().insertQuotaR(quotaRList);
                        mainActivity.setSettings(Constants.Settings.QUOTA_TIME, String.valueOf(DateUtils.getCurrentTimeMillis()));

                    }
                } catch (Exception e) {
                    Log.d(TAG, mContext.getString(R.string.db_save_error));
                }

                onSuccess();
            } else {
                onError(new Exception(mContext.getString(R.string.load_quotas_error) + " " + mContext.getString(R.string.error_104)));
                return;
            }
        } else {
            onError(new Exception(mContext.getString(R.string.load_quotas_error) + " " + mContext.getString(R.string.error_105)));
            return;
        }
    }
}
