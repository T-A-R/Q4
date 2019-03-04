package pro.quizer.quizerexit.executable;

import android.content.Context;
import android.support.annotation.NonNull;

import com.activeandroid.query.Update;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.ResponseBody;
import pro.quizer.quizerexit.Constants;
import pro.quizer.quizerexit.DoRequest;
import pro.quizer.quizerexit.R;
import pro.quizer.quizerexit.model.QuestionnaireStatus;
import pro.quizer.quizerexit.model.config.ConfigModel;
import pro.quizer.quizerexit.model.database.QuestionnaireDatabaseModel;
import pro.quizer.quizerexit.model.database.UserModel;
import pro.quizer.quizerexit.model.request.QuestionnaireListRequestModel;
import pro.quizer.quizerexit.model.response.DeletingListResponseModel;
import pro.quizer.quizerexit.utils.SPUtils;

public class SendQuestionnairesByUserModelExecutable extends BaseExecutable {

    private final String mServerUrl;
    private final Context mContext;
    private final UserModel mUserModel;

    public SendQuestionnairesByUserModelExecutable(final Context pContext, final UserModel pUserModel, final ICallback pCallback) {
        super(pCallback);

        final ConfigModel configModel = pUserModel.getConfig();

        mContext = pContext;
        mServerUrl = configModel.getServerUrl();
        mUserModel = pUserModel;
    }

    @Override
    public void execute() {
        onStarting();

        final QuestionnaireListRequestModel requestModel = new QuestionnaireListRequestModelExecutable(mUserModel).execute();

        if (requestModel == null) {
            onSuccess();

            return;
        }

        final Dictionary<String, String> mDictionaryForRequest = new Hashtable();
        mDictionaryForRequest.put(Constants.ServerFields.JSON_DATA, new Gson().toJson(requestModel));

        final Call.Factory client = new OkHttpClient();
        client.newCall(new DoRequest().post(mDictionaryForRequest, mServerUrl))
                .enqueue(new Callback() {

                    @Override
                    public void onFailure(@NonNull final Call call, @NonNull final IOException e) {
                        onError(e);
                    }

                    @Override
                    public void onResponse(@NonNull final Call call, @NonNull final Response response) throws IOException {
                        final ResponseBody responseBody = response.body();

                        if (responseBody == null) {
                            onError(new Exception(mContext.getString(R.string.incorrect_server_response)));

                            return;
                        }

                        final String responseJson = responseBody.string();
                        DeletingListResponseModel deletingListResponseModel = null;

                        try {
                            deletingListResponseModel = new GsonBuilder().create().fromJson(responseJson, DeletingListResponseModel.class);
                        } catch (Exception pE) {
                            // empty
                        }

                        if (deletingListResponseModel != null) {
                            SPUtils.saveSendTimeDifference(mContext, deletingListResponseModel.getServerTime());

                            if (deletingListResponseModel.getResult() != 0) {
                                final List<String> tokensToRemove = deletingListResponseModel.getAccepted();

                                if (tokensToRemove == null || tokensToRemove.isEmpty()) {
                                    onError(new Exception(mContext.getString(R.string.empty_list_of_accepted_questionnairies)));
                                } else {
                                    SPUtils.addSendedQInSession(mContext, tokensToRemove.size());

                                    for (final String token : tokensToRemove) {
                                        new Update(QuestionnaireDatabaseModel.class)
                                                .set(QuestionnaireDatabaseModel.STATUS + " = ?", QuestionnaireStatus.SENT)
                                                .where(QuestionnaireDatabaseModel.TOKEN + " = ?", token)
                                                .execute();
                                    }

                                    onSuccess();
                                }
                            } else {
                                onError(new Exception(deletingListResponseModel.getError()));
                            }
                        } else {
                            onError(new Exception(mContext.getString(R.string.server_error)));
                        }
                    }
                });

    }
}
