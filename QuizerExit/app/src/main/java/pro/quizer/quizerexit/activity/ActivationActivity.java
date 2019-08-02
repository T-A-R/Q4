package pro.quizer.quizerexit.activity;

import android.os.Bundle;
import android.util.Log;
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
    private boolean isKeyBtnPressed = false;

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
            if (!isKeyBtnPressed) {
                isKeyBtnPressed = true;
                sendKeyWithRetrofit(key);
            }
        });
    }

    private void sendKeyWithRetrofit(String key) {
        if (isKeyBtnPressed) {
            Log.d(TAG, "isKeyBtnPressed 1: " + isKeyBtnPressed);
            ActivationRequestModel activationRequestModel = new ActivationRequestModel(key);
            Gson gson = new Gson();
            String json = gson.toJson(activationRequestModel);

            addLog(Constants.LogUser.ANDROID, Constants.LogType.SERVER, Constants.LogObject.KEY, "Отправка ключа", Constants.LogResult.SENT, "Попытка отправки ключа");

            QuizerAPI.sendKey(Constants.Default.ACTIVATION_URL, json, this);
        }
    }

    @Override
    public void onSendKey(ResponseBody responseBody) {
        hideProgressBar();
        isKeyBtnPressed = false;

        if (responseBody == null) {
            showToast(getString(R.string.NOTIFICATION_SERVER_CONNECTION_ERROR) + " Ошибка: 5.01");
            addLog(Constants.LogUser.ANDROID, Constants.LogType.SERVER, Constants.LogObject.KEY, "Получение ответа от сервера на отправку ключа", Constants.LogResult.ERROR, "Ошибка 5.01 (Нет ответа от сервера. Возможны проблемы с подключением к сети интеренет)");
            return;
        }

        String responseJson = null;
        try {
            responseJson = responseBody.string();
        } catch (IOException e) {
            e.printStackTrace();
            showToast(getString(R.string.NOTIFICATION_SERVER_RESPONSE_ERROR) + " Ошибка: 5.02");
            addLog(Constants.LogUser.ANDROID, Constants.LogType.SERVER, Constants.LogObject.KEY, "Получение ответа от сервера на отправку ключа", Constants.LogResult.ERROR, "Ошибка 5.02 (Ошибка получения JSON из ответа сервера)");
        }
        ActivationResponseModel activationModel = null;

        if (responseJson != null)
            try {
                activationModel = new GsonBuilder().create().fromJson(responseJson, ActivationResponseModel.class);
            } catch (Exception pE) {
                showToast(getString(R.string.NOTIFICATION_SERVER_RESPONSE_ERROR) + " Ошибка: 5.03");
                addLog(Constants.LogUser.ANDROID, Constants.LogType.SERVER, Constants.LogObject.KEY, "Получение ответа от сервера на отправку ключа", Constants.LogResult.ERROR, "Ошибка 5.03 (Ошибка парсинга JSON. " + pE.getMessage() + ")");
            }

        if (activationModel != null) {
            if (activationModel.getResult() != 0) {
                saveActivationBundle(activationModel);
                addLog(Constants.LogUser.ANDROID, Constants.LogType.SERVER, Constants.LogObject.KEY, "Получение ответа от сервера на отправку ключа", Constants.LogResult.SUCCESS, "Успешная отправка ключа");
                startAuthActivity();
            } else {
                showToast(activationModel.getError());
                addLog(Constants.LogUser.ANDROID, Constants.LogType.SERVER, Constants.LogObject.KEY, "Получение ответа от сервера на отправку ключа", Constants.LogResult.ERROR, activationModel.getError());
            }
        } else {
            showToast(getString(R.string.NOTIFICATION_SERVER_ERROR) + " Ошибка: 5.05");
        }
    }
}