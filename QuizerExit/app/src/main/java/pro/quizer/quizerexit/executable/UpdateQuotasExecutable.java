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
import pro.quizer.quizerexit.Constants;
import pro.quizer.quizerexit.DoRequest;
import pro.quizer.quizerexit.R;
import pro.quizer.quizerexit.activity.BaseActivity;
import pro.quizer.quizerexit.model.config.ConfigModel;
import pro.quizer.quizerexit.model.database.UserModel;
import pro.quizer.quizerexit.model.request.QuotaRequestModel;
import pro.quizer.quizerexit.model.response.QuotaResponseModel;
import pro.quizer.quizerexit.utils.SPUtils;

public class UpdateQuotasExecutable extends BaseExecutable {

    private final Context mContext;

    public UpdateQuotasExecutable(final Context pContext, final ICallback pCallback) {
        super(pCallback);

        mContext = pContext;
    }

    @Override
    public void execute() {
        onStarting();

        final BaseActivity baseActivity = (BaseActivity) mContext;
        final UserModel userModel = baseActivity.getCurrentUser();
        final ConfigModel configModel = userModel.getConfig();
        final int userProjectId = userModel.user_project_id;

        final Dictionary<String, String> mDictionaryForRequest = new Hashtable();
        mDictionaryForRequest.put(Constants.ServerFields.JSON_DATA, new Gson().toJson(new QuotaRequestModel(configModel.getLoginAdmin(), userModel.password, userModel.login)));

        final Call.Factory client = new OkHttpClient();
        client.newCall(new DoRequest().post(mDictionaryForRequest, configModel.getServerUrl()))
                .enqueue(new Callback() {

                    @Override
                    public void onFailure(@NonNull final Call call, @NonNull final IOException e) {
                        onError(e);
                    }

                    @Override
                    public void onResponse(@NonNull final Call call, @NonNull final Response response) throws IOException {
                        final ResponseBody responseBody = response.body();

                        if (responseBody == null) {
                            onFailure(call, new IOException(mContext.getString(R.string.NOTIFICATION_SERVER_RESPONSE_ERROR)));

                            return;
                        }

                        final String responseJson = responseBody.string();
                        QuotaResponseModel quotaResponseModel = null;

                        try {
                            quotaResponseModel = new GsonBuilder().create().fromJson(responseJson, QuotaResponseModel.class);
                        } catch (final Exception pE) {
                            // empty
                        }

                        if (quotaResponseModel != null) {
                            SPUtils.saveQuotaTimeDifference(mContext, quotaResponseModel.getServerTime());

                            if (quotaResponseModel.getResult() != 0) {
                                new Update(UserModel.class)
                                        .set(UserModel.QUOTAS + " = ?", responseJson)
                                        .where(UserModel.USER_PROJECT_ID + " = ?", userProjectId).execute();


                                onSuccess();
                            } else {
                                onFailure(call, new IOException(quotaResponseModel.getError()));
                            }
                        } else {
                            onFailure(call, new IOException(mContext.getString(R.string.NOTIFICATION_SERVER_ERROR)));
                        }
                    }
                });
    }
}
