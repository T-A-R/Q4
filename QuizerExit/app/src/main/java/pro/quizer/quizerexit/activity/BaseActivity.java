package pro.quizer.quizerexit.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import pro.quizer.quizerexit.R;
import pro.quizer.quizerexit.model.response.AuthResponseModel;
import pro.quizer.quizerexit.model.response.ConfigResponseModel;
import pro.quizer.quizerexit.utils.SPUtils;

@SuppressLint("Registered")
public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void showToastMessage(final CharSequence pMessage) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(BaseActivity.this, pMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public boolean isActivated() {
        return SPUtils.isActivated(this);
    }

    public String getSPLogin() {
        return SPUtils.getLogin(this);
    }

    public String getSPPassword() {
        return SPUtils.getPassword(this);
    }

    public String getSPConfigId() {
        return SPUtils.getConfigId(this);
    }

    public ConfigResponseModel getSPConfigModel() {
        return SPUtils.getConfigModel(this);
    }

    public String getSPServer() {
        return SPUtils.getServer(this);
    }

    public String getSPLoginAdmin() {
        return SPUtils.getLoginAdmin(this);
    }

    public void saveActivationBundle(final String pServer, final String pLoginAdmin) {
        SPUtils.saveActivationBundle(this,
                pServer,
                pLoginAdmin);
    }

    public void saveConfigModel(final ConfigResponseModel pConfigResponseModel) {
        SPUtils.saveConfig(this, pConfigResponseModel);
    }

    public void saveAuthBundle(final String pLogin, final String pPassword, final AuthResponseModel pModel) {
        SPUtils.saveAuthBundle(this,
                pLogin,
                pPassword,
                pModel.getConfigId(),
                pModel.getUserId(),
                pModel.getRoleId(),
                pModel.getUserProjectId());
    }

    public void startAuthActivity() {
        startActivity(new Intent(this, AuthActivity.class));
    }

    public void startActivationActivity() {
        startActivity(new Intent(this, ActivationActivity.class));
    }

    public void startQuestionActivity() {
        startActivity(new Intent(this, QuestionActivity.class));
    }

    public void startMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
    }

    public View getProgressBar() {
        return findViewById(R.id.progressBar);
    }

    public void showProgressBar() {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                getProgressBar().setVisibility(View.VISIBLE);
            }
        });
    }

    public void hideProgressBar() {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                getProgressBar().setVisibility(View.GONE);
            }
        });
    }
}
