package pro.quizer.quizer3.view.fragment;

import android.os.Build;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.reginald.editspinner.EditSpinner;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import pro.quizer.quizer3.API.QuizerAPI;
import pro.quizer.quizer3.API.models.request.AuthRequestModel;
import pro.quizer.quizer3.BuildConfig;
import pro.quizer.quizer3.Constants;
import pro.quizer.quizer3.R;
import pro.quizer.quizer3.database.models.UserModelR;
import pro.quizer.quizer3.utils.Fonts;
import pro.quizer.quizer3.utils.MD5Utils;
import pro.quizer.quizer3.utils.StringUtils;
import pro.quizer.quizer3.utils.UiUtils;
import pro.quizer.quizer3.view.Anim;
import pro.quizer.quizer3.view.screens.PageFragment;

public class AuthFragment extends ScreenFragment implements View.OnClickListener, QuizerAPI.AuthUserCallback {

    private static int MAX_USERS = 5;
    private static int MAX_VERSION_TAP_COUNT = 5;

    private TextView tvVersionWarning;
    private TextView tvUsers;
    private TextView tvVersionView;
    private EditSpinner esLogin;
    private EditText etPass;
    private Button btnSend;

    private boolean isExit;
    private int mVersionTapCount = 0;

    String login;
    String password;
    String passwordMD5;

    private List<String> mSavedUsers;
    private List<UserModelR> mSavedUserModels;

    public AuthFragment() {
        super(R.layout.fragment_auth);
    }

    @Override
    protected void onReady() {
        FrameLayout cont = (FrameLayout) findViewById(R.id.cont_auth_fragment);
        LinearLayout image = (LinearLayout) findViewById(R.id.cont_image);
        btnSend = (Button) findViewById(R.id.btn_send_auth);
        esLogin = (EditSpinner) findViewById(R.id.login_spinner);
        etPass = (EditText) findViewById(R.id.auth_password_edit_text);
        tvVersionWarning = (TextView) findViewById(R.id.version_warning);
        tvUsers = (TextView) findViewById(R.id.users_count);
        tvVersionView = (TextView) findViewById(R.id.version_view);

        MainFragment.disableSideMenu();

        esLogin.setTypeface(Fonts.getFuturaPtMedium());
        etPass.setTypeface(Fonts.getFuturaPtMedium());
        btnSend.setTypeface(Fonts.getFuturaPtBook());
        btnSend.setTransformationMethod(null);

        btnSend.setOnClickListener(this);
        tvVersionView.setOnClickListener(this);

        cont.startAnimation(Anim.getAppear(getContext()));
//        image.startAnimation(Anim.getSlideUpDown(getContext()));
        btnSend.startAnimation(Anim.getAppearSlide(getContext(), 500));

        checkVersion();

        final int usersCountValue = getDao().getAllUsers().size();
        tvUsers.setText(String.format(getString(R.string.auth_users_on_device), (usersCountValue + "/" + MAX_USERS)));
        UiUtils.setTextOrHide(tvVersionView, String.format(getString(R.string.auth_version_button), BuildConfig.VERSION_NAME));

        mSavedUserModels = getSavedUserModels();
        mSavedUsers = getSavedUserLogins();

        final ListAdapter adapter = new ArrayAdapter<>(getContext(), R.layout.adapter_spinner, mSavedUsers);
        esLogin.setAdapter(adapter);

        if (mSavedUserModels != null && !mSavedUserModels.isEmpty()) {
            final int lastUserId = getCurrentUserId();

            if (lastUserId != -1) {
                for (final UserModelR userModel : mSavedUserModels) {
                    if (userModel.getUser_id() == lastUserId) {
                        UiUtils.setTextOrHide(esLogin, userModel.getLogin());
                    }
                }
            }
        }


    }

    @Override
    public void onClick(View view) {
        if (view == btnSend) {
//            showScreensaver(true);
//            replaceFragment(new PageFragment());
            onLoginClick();
        } else if (view == tvVersionView) {
            onVersionClick();
        }
    }

    @Override
    public boolean onBackPressed() {
        if (isExit) {
            getActivity().finish();
        } else {
            Toast.makeText(getContext(), getString(R.string.exit_message), Toast.LENGTH_SHORT).show();
            isExit = true;
        }
        return true;
    }

    private void checkVersion() {
        final int sdk = android.os.Build.VERSION.SDK_INT;

        if (sdk < Build.VERSION_CODES.LOLLIPOP && !Build.VERSION.RELEASE.equals("4.4.4")) {
            tvVersionWarning.setVisibility(View.VISIBLE);
            tvVersionWarning.setText(String.format(getString(R.string.auth_version_warning), Build.VERSION.RELEASE));
        } else {
            tvVersionWarning.setVisibility(View.GONE);
        }
    }

    private void onVersionClick() {
        mVersionTapCount++;

        if (mVersionTapCount == MAX_VERSION_TAP_COUNT) {
            addLog("android", Constants.LogType.BUTTON, null, getString(R.string.button_press), Constants.LogResult.PRESSED, getString(R.string.button_version), "");
            mVersionTapCount = 0;
            replaceFragment(new KeyFragment());
        }
    }

    private List<UserModelR> getSavedUserModels() {
        final List<UserModelR> userModels = getDao().getAllUsers();
        return (userModels == null) ? new ArrayList<UserModelR>() : userModels;
    }

    private List<String> getSavedUserLogins() {
        final List<String> users = new ArrayList<>();

        for (final UserModelR model : mSavedUserModels) {
            users.add(model.getLogin());
        }

        return users;
    }

    private void onLoginClick() {
        showScreensaver(false);

        login = esLogin.getText().toString();
        password = etPass.getText().toString();

        if (StringUtils.isEmpty(login) || StringUtils.isEmpty(password)) {
            showToast(getString(R.string.notification_empty_login_or_pass));
            hideScreensaver();
            return;
        }

        if (login.length() < 3) {
            showToast(getString(R.string.notification_short_login));
            hideScreensaver();
            return;
        }

        if (mSavedUsers != null && mSavedUsers.size() >= MAX_USERS && !mSavedUsers.contains(login)) {
            showToast(String.format(getString(R.string.notification_max_users), String.valueOf(MAX_USERS)));
            addLog(null, Constants.LogType.SERVER, Constants.LogObject.AUTH, getString(R.string.user_auth), Constants.LogResult.ERROR, getString(R.string.notification_max_users), "");
            hideScreensaver();
            return;
        }

        passwordMD5 = MD5Utils.formatPassword(login, password);

        AuthRequestModel post = new AuthRequestModel(getLoginAdmin(), passwordMD5, login);
        Gson gson = new Gson();
        String json = gson.toJson(post);

        addLog(login, Constants.LogType.SERVER, Constants.LogObject.AUTH, getString(R.string.user_auth), Constants.LogResult.SENT, getString(R.string.send_auth_request), json);

        QuizerAPI.authUser(getServer(), json, this);
    }

    @Override
    public void onAuthUser(ResponseBody data) {
        hideScreensaver();
        replaceFragment(new PageFragment());
    }
}

