package pro.quizer.quizer3.view.fragment;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import okhttp3.ResponseBody;
import pro.quizer.quizer3.API.QuizerAPI;
import pro.quizer.quizer3.API.models.request.ActivationRequestModel;
import pro.quizer.quizer3.API.models.response.ActivationResponseModel;
import pro.quizer.quizer3.Constants;
import pro.quizer.quizer3.R;
import pro.quizer.quizer3.database.models.ActivationModelR;
import pro.quizer.quizer3.database.models.UserModelR;
import pro.quizer.quizer3.executable.SyncInfoExecutable;
import pro.quizer.quizer3.model.config.ConfigModel;
import pro.quizer.quizer3.model.config.ProjectInfoModel;
import pro.quizer.quizer3.model.view.SyncViewModel;
import pro.quizer.quizer3.utils.Fonts;
import pro.quizer.quizer3.utils.StringUtils;
import pro.quizer.quizer3.utils.UiUtils;
import pro.quizer.quizer3.view.Anim;

import static pro.quizer.quizer3.MainActivity.TAG;

public class HomeFragment extends ScreenFragment implements View.OnClickListener {

    private Button btnStart;
    private Button btnQuotas;
    private TextView tvConfigAgreement;
    private TextView tvCurrentUser;
    private TextView tvConfigName;
    private TextView tvCoountAll;
    private TextView tvCountSent;

    private boolean isStartBtnPressed = false;
    private boolean isExit = false;
    private UserModelR mUserModel;

    public HomeFragment() {
        super(R.layout.fragment_home);
    }

    @Override
    protected void onReady() {
        RelativeLayout cont = (RelativeLayout) findViewById(R.id.cont_home_fragment);
        btnStart = (Button) findViewById(R.id.btn_start);
        btnQuotas = (Button) findViewById(R.id.btn_quotas);
        tvConfigAgreement = (TextView) findViewById(R.id.config_agreement);
        tvConfigName = (TextView) findViewById(R.id.config_name);
        tvCoountAll = (TextView) findViewById(R.id.count_all);
        tvCountSent = (TextView) findViewById(R.id.count_sent);
        tvCurrentUser = (TextView) findViewById(R.id.current_user);

        MainFragment.enableSideMenu();

        btnStart.setTypeface(Fonts.getFuturaPtBook());
        btnStart.setTransformationMethod(null);
        btnStart.setOnClickListener(this);
        btnQuotas.setTypeface(Fonts.getFuturaPtBook());
        btnQuotas.setTransformationMethod(null);
        btnQuotas.setOnClickListener(this);
        tvConfigAgreement.setTypeface(Fonts.getFuturaPtBook());
        tvConfigName.setTypeface(Fonts.getFuturaPtBook());
        tvCoountAll.setTypeface(Fonts.getFuturaPtBook());
        tvCountSent.setTypeface(Fonts.getFuturaPtBook());
        tvCurrentUser.setTypeface(Fonts.getFuturaPtBook());

        cont.startAnimation(Anim.getAppear(getContext()));
        btnStart.startAnimation(Anim.getAppearSlide(getContext(), 500));
        btnQuotas.startAnimation(Anim.getAppearSlide(getContext(), 500));
//        image.startAnimation(Anim.getSlideUpDown(getContext()));

        initViews();
    }

    public void initViews() {

        mUserModel = getCurrentUser();
        final ConfigModel config = mUserModel.getConfigR();
        final ProjectInfoModel projectInfo = config.getProjectInfo();

        initSyncInfoViews();

        tvConfigName.setText(projectInfo.getName());
        tvConfigAgreement.setText(projectInfo.getAgreement());


    }

    private void initSyncInfoViews() {
        if (getActivity() != null)
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    final View pView = getView();

                    if (pView == null) {
                        return;
                    }

                    final SyncViewModel syncViewModel = new SyncInfoExecutable(getContext()).execute();

                    UiUtils.setTextOrHide(tvCoountAll, (String
                            .format(getString(R.string.collected_questions),
                                    String.valueOf(syncViewModel.getmAllQuestionnaireModels().size()))));
                    UiUtils.setTextOrHide(tvCountSent, (String
                            .format(getString(R.string.questions_sent_from_device),
                                    String.valueOf(syncViewModel.getmSentQuestionnaireModelsFromThisDevice().size()))));

                }
            });
    }

    @Override
    public void onClick(View view) {
        if (view == btnStart) {

            if (!isStartBtnPressed) {
                isStartBtnPressed = true;
                Toast.makeText(getContext(), getString(R.string.start_questions), Toast.LENGTH_SHORT).show();
                try {
                    getDao().updateQuestionnaireStart(true, mUserModel.getUser_id());
                    replaceFragment(new ElementFragment());
                } catch (Exception e) {
                    showToast(getString(R.string.start_question_error));
                }

            }
        } else if (view == btnQuotas) {
            Toast.makeText(getContext(), "Quotas", Toast.LENGTH_SHORT).show();
            replaceFragment(new HomeFragment());
        }
    }

    @Override
    public boolean onBackPressed() {
        if (isExit && getActivity() != null) {
            getActivity().finish();
        } else {
            Toast.makeText(getContext(), "Для выхода нажмите \"Назад\" еще раз", Toast.LENGTH_SHORT).show();
            isExit = true;
        }
        return true;
    }
}

