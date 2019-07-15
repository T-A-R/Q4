package pro.quizer.quizerexit.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import okhttp3.ResponseBody;
import pro.quizer.quizerexit.API.QuizerAPI;
import pro.quizer.quizerexit.Constants;
import pro.quizer.quizerexit.R;
import pro.quizer.quizerexit.model.request.ActivationRequestModel;
import pro.quizer.quizerexit.model.response.ActivationResponseModel;
import pro.quizer.quizerexit.utils.StringUtils;

public class ActivationActivity extends BaseActivity implements QuizerAPI.SendKeyCallback {

    private EditText mActivationEditText;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activation);

        mActivationEditText = findViewById(R.id.activation_edit_text);
        final Button sendButton = findViewById(R.id.send_activation_button);

        sendButton.setOnClickListener(v -> {
            showProgressBar();

            final String key = mActivationEditText.getText().toString();

            if (StringUtils.isEmpty(key)) {
                showToast(getString(R.string.NOTIFICATION_EMPTY_ACTIVATION_KEY));
                hideProgressBar();
                return;
            }

            sendKeyWithRetrofit(key);


//                final Dictionary<String, String> mDictionaryForRequest = new Hashtable();
//                mDictionaryForRequest.put(Constants.ServerFields.JSON_DATA, new Gson().toJson(new ActivationRequestModel(key)));
//
//                final Call.Factory client = new OkHttpClient();
//                client.newCall(new DoRequest().post(mDictionaryForRequest, Constants.Default.ACTIVATION_URL))
//                        .enqueue(new Callback() {
//
//                            @Override
//                            public void onFailure(@NonNull final Call call, @NonNull final IOException e) {
//                                hideProgressBar();
//                                showToast(getString(R.string.NOTIFICATION_INTERNET_CONNECTION_ERROR));
//                            }
//
//                            @Override
//                            public void onResponse(@NonNull final Call call, @NonNull final Response response) throws IOException {
//                                hideProgressBar();
//
//                                final ResponseBody responseBody = response.body();
//
//                                if (responseBody == null) {
//                                    showToast(getString(R.string.NOTIFICATION_SERVER_RESPONSE_ERROR));
//
//                                    return;
//                                }
//
//                                final String responseJson = responseBody.string();
//                                ActivationResponseModel activationModel = null;
//
//                                try {
//                                    activationModel = new GsonBuilder().create().fromJson(responseJson, ActivationResponseModel.class);
//                                } catch (Exception pE) {
//                                    // empty
//                                }
//
//                                if (activationModel != null) {
//                                    if (activationModel.getResult() != 0) {
//                                        saveActivationBundle(activationModel);
//                                        startAuthActivity();
//                                    } else {
//                                        showToast(activationModel.getError());
//                                    }
//                                } else {
//                                    showToast(getString(R.string.NOTIFICATION_SERVER_ERROR));
//                                }
//                            }
//                        });

        });
    }

    private void sendKeyWithRetrofit(String key) {
        ActivationRequestModel activationRequestModel = new ActivationRequestModel(key);
        Gson gson = new Gson();
        String json = gson.toJson(activationRequestModel);

        QuizerAPI.sendKey(Constants.Default.ACTIVATION_URL, json, this);
    }

    @Override
    public void onSendKey(ResponseBody responseBody) {
        hideProgressBar();

        if (responseBody == null) {
            showToast(getString(R.string.NOTIFICATION_SERVER_CONNECTION_ERROR) + " Ошибка: 5.01");
            return;
        }

        String responseJson = null;
        try {
            responseJson = responseBody.string();
        } catch (IOException e) {
            e.printStackTrace();
            showToast(getString(R.string.NOTIFICATION_SERVER_RESPONSE_ERROR) + " Ошибка: 5.02");
        }
        ActivationResponseModel activationModel = null;

        if (responseJson != null)
            try {
                activationModel = new GsonBuilder().create().fromJson(responseJson, ActivationResponseModel.class);
            } catch (Exception pE) {
                showToast(getString(R.string.NOTIFICATION_SERVER_RESPONSE_ERROR) + " Ошибка: 5.03");
            }

        if (activationModel != null) {
            if (activationModel.getResult() != 0) {
                saveActivationBundle(activationModel);
                startAuthActivity();
            } else {
                showToast(getString(R.string.NOTIFICATION_SERVER_RESPONSE_ERROR) + " Ошибка: 5.04");
            }
        } else {
            showToast(getString(R.string.NOTIFICATION_SERVER_ERROR) + " Ошибка: 5.05");
        }
    }
}