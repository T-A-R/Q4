package pro.quizer.quizerexit.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.google.gson.GsonBuilder;

import java.util.List;

import pro.quizer.quizerexit.R;
import pro.quizer.quizerexit.model.database.ActivationModel;
import pro.quizer.quizerexit.model.database.UserModel;
import pro.quizer.quizerexit.model.response.ActivationResponseModel;
import pro.quizer.quizerexit.model.response.AuthResponseModel;
import pro.quizer.quizerexit.model.response.ConfigResponseModel;

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
        return getActivationModel() != null;
    }

    public String getServer() {
        return getActivationModel().server;
    }

    public String getLoginAdmin() {
        return getActivationModel().login_admin;
    }

    public void saveActivationBundle(final ActivationResponseModel pActivationModel) {
        final ActivationModel activationModel = new ActivationModel();
        activationModel.server = pActivationModel.getServer();
        activationModel.login_admin = pActivationModel.getLoginAdmin();

        new Delete().from(ActivationModel.class).execute();

        activationModel.save();
    }

    public ActivationModel getActivationModel() {
        final List<ActivationModel> list = new Select().from(ActivationModel.class).limit(1).execute();

        if (list != null && !list.isEmpty()) {
            return list.get(0);
        } else {
            return null;
        }
    }

    public UserModel getUserByUserId(final int pUserId) {
        final List<UserModel> list = new Select().from(UserModel.class).where(UserModel.USER_ID + " = ?", pUserId).execute();

        if (list == null || list.isEmpty()) {
            return null;
        } else {
            return list.get(0);
        }
    }

    public void saveCurrentUserId(final int pUserId) {
        
    }

    public void saveUser(final String pLogin, final String pPassword, final AuthResponseModel pModel, final ConfigResponseModel pConfigResponseModel) {
        new Delete().from(UserModel.class).where(UserModel.USER_ID + " = ?", pModel.getUserId()).execute();

        final UserModel userModel = new UserModel();
        userModel.login = pLogin;
        userModel.password = pPassword;
        userModel.config_id = pModel.getConfigId();
        userModel.role_id = pModel.getRoleId();
        userModel.user_id = pModel.getUserId();
        userModel.user_project_id = pModel.getUserProjectId();
        userModel.config = new GsonBuilder().create().toJson(pConfigResponseModel);

        userModel.save();
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
