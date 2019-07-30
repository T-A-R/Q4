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
import pro.quizer.quizerexit.database.model.AppLogsR;
import pro.quizer.quizerexit.model.request.ActivationRequestModel;
import pro.quizer.quizerexit.model.response.ActivationResponseModel;
import pro.quizer.quizerexit.utils.DateUtils;
import pro.quizer.quizerexit.utils.DeviceUtils;
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
        });
    }

    private void sendKeyWithRetrofit(String key) {
        ActivationRequestModel activationRequestModel = new ActivationRequestModel(key);
        Gson gson = new Gson();
        String json = gson.toJson(activationRequestModel);

        AppLogsR appLogsR = new AppLogsR();
        appLogsR.setLogin(null);
        appLogsR.setDevice(DeviceUtils.getDeviceInfo());
        appLogsR.setDevice(DeviceUtils.getAppVersion());
        appLogsR.setAndroid(null);
        appLogsR.setDate(String.valueOf(DateUtils.getCurrentTimeMillis()));
        appLogsR.setType(Constants.LogType.SERVER);
        appLogsR.setObject(Constants.LogObject.KEY);
        appLogsR.setAction("Отправка ключа");
        appLogsR.setResult(Constants.LogResult.ERROR);
        appLogsR.setDescription("3.01");

        getDao().insertAppLogsR(appLogsR);

        QuizerAPI.sendKey(Constants.Default.ACTIVATION_URL, json, this);
    }

    @Override
    public void onSendKey(ResponseBody responseBody) {
        hideProgressBar();

        AppLogsR appLogsR = new AppLogsR();
        appLogsR.setLogin(null);
        appLogsR.setDevice(DeviceUtils.getDeviceInfo());
        appLogsR.setDevice(DeviceUtils.getAppVersion());
        appLogsR.setAndroid(null);
        appLogsR.setDate(String.valueOf(DateUtils.getCurrentTimeMillis()));
        appLogsR.setType(Constants.LogType.SERVER);
        appLogsR.setObject(Constants.LogObject.KEY);
        appLogsR.setAction("Отправка ключа");

        if (responseBody == null) {
            showToast(getString(R.string.NOTIFICATION_SERVER_CONNECTION_ERROR) + " Ошибка: 5.01");

            appLogsR.setResult(Constants.LogResult.ERROR);
            appLogsR.setDescription("3.01");

            getDao().insertAppLogsR(appLogsR);
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
                showToast(activationModel.getError());
            }
        } else {
            showToast(getString(R.string.NOTIFICATION_SERVER_ERROR) + " Ошибка: 5.05");
        }
    }
}