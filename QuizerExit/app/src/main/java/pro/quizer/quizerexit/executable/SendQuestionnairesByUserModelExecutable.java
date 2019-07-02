package pro.quizer.quizerexit.executable;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;

import com.activeandroid.query.Update;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.ResponseBody;
import pro.quizer.quizerexit.Constants;
import pro.quizer.quizerexit.DoRequest;
import pro.quizer.quizerexit.R;
import pro.quizer.quizerexit.activity.BaseActivity;
import pro.quizer.quizerexit.model.QuestionnaireStatus;
import pro.quizer.quizerexit.model.config.ConfigModel;
import pro.quizer.quizerexit.model.config.ElementModel;
import pro.quizer.quizerexit.model.database.QuestionnaireDatabaseModel;
import pro.quizer.quizerexit.model.database.UserModel;
import pro.quizer.quizerexit.model.request.QuestionnaireListRequestModel;
import pro.quizer.quizerexit.model.response.DeletingListResponseModel;
import pro.quizer.quizerexit.model.sms.SmsStage;
import pro.quizer.quizerexit.model.view.SmsViewModel;
import pro.quizer.quizerexit.utils.NetworkUtils;
import pro.quizer.quizerexit.utils.SPUtils;
import pro.quizer.quizerexit.utils.SmsUtils;

public class SendQuestionnairesByUserModelExecutable extends BaseExecutable {

    private final String mServerUrl;
    private final BaseActivity mBaseActivity;
    private final UserModel mUserModel;
    private final boolean mIsShowAlertDialog;

    public SendQuestionnairesByUserModelExecutable(final BaseActivity pBaseActivity, final UserModel pUserModel, final ICallback pCallback, final boolean pIsShowAlertDialog) {
        super(pCallback);

        final ConfigModel configModel = pUserModel.getConfig();

        mBaseActivity = pBaseActivity;
        mServerUrl = configModel.getServerUrl();
        mUserModel = pUserModel;
        mIsShowAlertDialog = pIsShowAlertDialog;
    }

    @Override
    public void execute() {
        onStarting();

        if (NetworkUtils.hasConnection()) {
            sendViaInternet();
        } else if (mUserModel.getConfig().hasReserveChannels()) {
            sendViaSms(mBaseActivity.createNewMap(mUserModel.getConfig().getProjectInfo().getElements()), mBaseActivity);
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
                alertDialog.setMessage(pBaseActivity.getString(R.string.DIALOG_SENDING_WAVES_VIA_SMS_CONFIRMATION) + mUserModel.login + " через СМС?");
                alertDialog.setPositiveButton(R.string.VIEW_BUTTON_SEND, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        SmsUtils.sendSms(mBaseActivity, getCallback(), readyToSendStages);
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
                SmsUtils.sendSms(mBaseActivity, getCallback(), readyToSendStages);
            }
        }

        onSuccess();
    }

    private void sendViaInternet() {
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
                            onError(new Exception(mBaseActivity.getString(R.string.NOTIFICATION_SERVER_RESPONSE_ERROR)));

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
                            SPUtils.saveSendTimeDifference(mBaseActivity, deletingListResponseModel.getServerTime());

                            if (deletingListResponseModel.getResult() != 0) {
                                final List<String> tokensToRemove = deletingListResponseModel.getAccepted();

                                if (tokensToRemove == null || tokensToRemove.isEmpty()) {
                                    onError(new Exception(mBaseActivity.getString(R.string.NOTIFICATION_SENDING_ERROR_EMPTY_TOKENS_LIST)));
                                } else {
                                    SPUtils.addSendedQInSession(mBaseActivity, tokensToRemove.size());

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
                            onError(new Exception(mBaseActivity.getString(R.string.NOTIFICATION_SERVER_ERROR)));
                        }
                    }
                });
    }
}
