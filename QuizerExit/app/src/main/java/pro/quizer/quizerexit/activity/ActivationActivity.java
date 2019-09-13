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
import pro.quizer.quizerexit.utils.FontUtils;
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

        setChangeFontCallback(new BaseActivity.ChangeFontCallback() {
            @Override
            public void onChangeFont() {
                showToast(getString(R.string.SETTED) + " " + FontUtils.getCurrentFontName(getFontSizePosition()));
                finish();
                overridePendingTransition(0, 0);
                startActivity(getIntent());
                overridePendingTransition(0, 0);

            }
        });
    }

    private void sendKeyWithRetrofit(String key) {
        if (isKeyBtnPressed) {
            ActivationRequestModel activationRequestModel = new ActivationRequestModel(key);
            Gson gson = new Gson();
            String json = gson.toJson(activationRequestModel);

            addLogWithData(Constants.LogUser.ANDROID, Constants.LogType.SERVER, Constants.LogObject.KEY, getString(R.string.SEND_KEY), Constants.LogResult.SENT, getString(R.string.TRY_TO_SEND_KEY) + " : " + key, json);

            QuizerAPI.sendKey(Constants.Default.ACTIVATION_URL, json, this);
        }
    }

    @Override
    public void onSendKey(ResponseBody responseBody) {
        hideProgressBar();
        isKeyBtnPressed = false;

        if (responseBody == null) {
            showToast(getString(R.string.NOTIFICATION_SERVER_CONNECTION_ERROR) + " " + getString(R.string.ERROR_501));
            addLog(Constants.LogUser.ANDROID, Constants.LogType.SERVER, Constants.LogObject.KEY, getString(R.string.SEND_KEY), Constants.LogResult.ERROR, getString(R.string.ERROR_501_DESC));
            return;
        }

        String responseJson = null;
        try {
            responseJson = responseBody.string();
        } catch (IOException e) {
            e.printStackTrace();
            showToast(getString(R.string.NOTIFICATION_SERVER_RESPONSE_ERROR) + " " + getString(R.string.ERROR_502));
            addLog(Constants.LogUser.ANDROID, Constants.LogType.SERVER, Constants.LogObject.KEY, getString(R.string.SEND_KEY), Constants.LogResult.ERROR, getString(R.string.ERROR_502_DESC));
        }
        ActivationResponseModel activationModel = null;

        if (responseJson != null)
            try {
                activationModel = new GsonBuilder().create().fromJson(responseJson, ActivationResponseModel.class);
            } catch (Exception pE) {
                showToast(getString(R.string.NOTIFICATION_SERVER_RESPONSE_ERROR) + " " + getString(R.string.ERROR_503));
                addLogWithData(Constants.LogUser.ANDROID, Constants.LogType.SERVER, Constants.LogObject.KEY, getString(R.string.SEND_KEY), Constants.LogResult.ERROR, getString(R.string.ERROR_503_DESC), responseJson);
            }

        if (activationModel != null) {
            if (activationModel.getResult() != 0) {
                saveActivationBundle(activationModel);
                addLogWithData(Constants.LogUser.ANDROID, Constants.LogType.SERVER, Constants.LogObject.KEY, getString(R.string.SEND_KEY), Constants.LogResult.SUCCESS, getString(R.string.SEND_KEY_SUCCESS), responseJson);
                startAuthActivity();
            } else {
                showToast(activationModel.getError());
                addLogWithData(Constants.LogUser.ANDROID, Constants.LogType.SERVER, Constants.LogObject.KEY, getString(R.string.SEND_KEY), Constants.LogResult.ERROR, activationModel.getError(), responseJson);
            }
        } else {
            showToast(getString(R.string.NOTIFICATION_SERVER_ERROR) + " " + getString(R.string.ERROR_505));
        }
    }
}