package pro.quizer.quizer3.view.fragment;

import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import pro.quizer.quizer3.MainActivity;
import pro.quizer.quizer3.R;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import okhttp3.ResponseBody;
import pro.quizer.quizer3.API.QuizerAPI;
import pro.quizer.quizer3.API.models.request.ActivationRequestModel;
import pro.quizer.quizer3.API.models.response.ActivationResponseModel;
import pro.quizer.quizer3.Constants;
import pro.quizer.quizer3.database.models.ActivationModelR;
import pro.quizer.quizer3.utils.Fonts;
import pro.quizer.quizer3.utils.StringUtils;
import pro.quizer.quizer3.view.Anim;

import static pro.quizer.quizer3.MainActivity.TAG;

public class KeyFragment extends ScreenFragment implements View.OnClickListener, QuizerAPI.SendKeyCallback {
    private Button btnSend;
    private EditText etKey;
    private String finalKey;

    private boolean isKeyBtnPressed = false;
    private boolean isExit = false;

    public KeyFragment() {
        super(R.layout.fragment_key);
    }

    @Override
    protected void onReady() {
        FrameLayout cont = (FrameLayout) findViewById(R.id.cont_key_fragment);
        LinearLayout image = (LinearLayout) findViewById(R.id.cont_image);
        btnSend = (Button) findViewById(R.id.btn_send_activation);
        etKey = (EditText) findViewById(R.id.et_activation);

        MainFragment.disableSideMenu();

        if (isAvia()) {
            etKey.setTypeface(Fonts.getAviaText());
            btnSend.setTypeface(Fonts.getAviaButton());
//            btnSend.setText("ОТПРАВИТЬ");
//            btnSend.setTransformationMethod(null);
        } else {
            getMainActivity().makeEncryptionTable();
        }

        getMainActivity().getSettings();

        btnSend.setOnClickListener(this);

        cont.startAnimation(Anim.getAppear(getContext()));
        btnSend.startAnimation(Anim.getAppearSlide(getContext(), 500));

        etKey.setOnFocusChangeListener((v, hasFocus) -> {
            String liganame = etKey.getText().toString();
            if (liganame.length() != 0) {
                getMainActivity().hideKeyboardFrom(etKey);
            }
        });

        etKey.requestFocus();
        getMainActivity().showKeyboard();
    }

    @Override
    public void onClick(View view) {
        if (view == btnSend) {
            showScreensaver(false);
            final String key = etKey.getText().toString();

            if (StringUtils.isEmpty(key)) {
                showToast(getString(R.string.empty_key));
                hideScreensaver();
                return;
            }
            if (!isKeyBtnPressed) {
                isKeyBtnPressed = true;
                sendKeyWithRetrofit(key);
            }
        }
    }

    private void sendKeyWithRetrofit(String key) {
        if (isKeyBtnPressed) {
            finalKey = key;
            ActivationRequestModel activationRequestModel = new ActivationRequestModel(key);
            Gson gson = new Gson();
            String json = gson.toJson(activationRequestModel);
            QuizerAPI.sendKey(Constants.Default.ACTIVATION_URL, key, this);
        }
    }

    public void saveActivationBundle(final ActivationResponseModel pActivationModel) {

        final ActivationModelR activationModelR = new ActivationModelR(pActivationModel.getServer(), pActivationModel.getLoginAdmin(), finalKey);
        try {
            getDao().clearActivationModelR();
        } catch (Exception e) {
            showToast(getString(R.string.db_clear_error));
        }

        try {
//            addLog(Constants.LogUser.ANDROID, Constants.LogType.DATABASE, Constants.LogObject.CONFIG, getString(R.string.log_save_server_info), Constants.LogResult.SENT, getString(R.string.log_save_server_to_db), "");
            getDao().insertActivationModelR(activationModelR);
        } catch (Exception e) {
            showToast(getString(R.string.db_save_error));
//            addLog(Constants.LogUser.ANDROID, Constants.LogType.DATABASE, Constants.LogObject.CONFIG, getString(R.string.log_save_server_info), Constants.LogResult.ERROR, getString(R.string.db_save_error), e.getMessage());

        }
    }

    @Override
    public void onSendKey(ResponseBody responseBody) {
//        hideProgressBar();
        hideScreensaver();
        isKeyBtnPressed = false;

        if (responseBody == null) {
            showToast(getString(R.string.server_not_response) + " " + getString(R.string.error_501));
//            addLog(Constants.LogUser.ANDROID, Constants.LogType.SERVER, Constants.LogObject.KEY, getString(R.string.log_send_key), Constants.LogResult.ERROR, getString(R.string.log_error_501_desc), "");
            return;
        }

        String responseJson = null;
        try {
            responseJson = responseBody.string();
        } catch (IOException e) {
            e.printStackTrace();
            showToast(getString(R.string.server_response_error) + " " + getString(R.string.error_502));
//            addLog(Constants.LogUser.ANDROID, Constants.LogType.SERVER, Constants.LogObject.KEY, getString(R.string.log_send_key), Constants.LogResult.ERROR, getString(R.string.log_error_502_desc), responseJson);
        }
        ActivationResponseModel activationModel = null;

        if (responseJson != null)
            Log.d(TAG, "onSendKey: " + responseJson);
        try {
            activationModel = new GsonBuilder().create().fromJson(responseJson, ActivationResponseModel.class);
        } catch (Exception pE) {
            showToast(getString(R.string.server_response_error) + " " + getString(R.string.error_503));
//            addLog(Constants.LogUser.ANDROID, Constants.LogType.SERVER, Constants.LogObject.KEY, getString(R.string.log_send_key), Constants.LogResult.ERROR, getString(R.string.log_error_503_desc), responseJson);
        }

        if (activationModel != null) {
            if (activationModel.getError() == null) {
                saveActivationBundle(activationModel);
//                addLog(Constants.LogUser.ANDROID, Constants.LogType.SERVER, Constants.LogObject.KEY, getString(R.string.log_send_key), Constants.LogResult.SUCCESS, getString(R.string.log_send_key_success), responseJson);
                replaceFragment(new AuthFragment());
            } else {
                Log.d("T-A-R", "onSendKey: " + activationModel.getError());
                showToast(activationModel.getError());
//                addLog(Constants.LogUser.ANDROID, Constants.LogType.SERVER, Constants.LogObject.KEY, getString(R.string.log_send_key), Constants.LogResult.ERROR, activationModel.getError(), responseJson);
            }
        } else {
            showToast(getString(R.string.server_response_error) + " " + getString(R.string.error_504));
        }
    }

    @Override
    public boolean onBackPressed() {
        if (isExit) {
            getMainActivity().closeApp();
        } else {
            showToast("Для выхода нажмите \"Назад\" еще раз");
            isExit = true;
        }
        return true;
    }
}

