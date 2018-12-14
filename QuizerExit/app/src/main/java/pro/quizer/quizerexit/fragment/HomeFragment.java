package pro.quizer.quizerexit.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import pro.quizer.quizerexit.R;
import pro.quizer.quizerexit.executable.ICallback;
import pro.quizer.quizerexit.executable.SendQuestionnairesByUserModelExecutable;
import pro.quizer.quizerexit.model.config.ConfigModel;
import pro.quizer.quizerexit.model.config.ProjectInfoModel;
import pro.quizer.quizerexit.model.database.UserModel;

public class HomeFragment extends BaseFragment implements ICallback {

    public static Fragment newInstance() {
        final HomeFragment fragment = new HomeFragment();

        final Bundle bundle = new Bundle();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView(view);
    }

    private void initView(final View pView) {
        final UserModel userModel = getBaseActivity().getUserByUserId(getBaseActivity().getCurrentUserId());
        final ConfigModel config = userModel.getConfig();
        final ProjectInfoModel projectInfo = config.getProjectInfo();

        final TextView configAgreement = pView.findViewById(R.id.config_agreement);
        final TextView configName = pView.findViewById(R.id.config_name);

//        // TODO: 01.12.2018 remove debug
//        final TextView currentUser = findViewById(R.id.current_user);
//        currentUser.setVisibility(View.VISIBLE);
//        currentUser.setText("login: " + userModel.login + " \n" +
//                "password: " + userModel.password + "\n" +
//                "config_id: " + userModel.config_id + "\n" +
//                "user_id: " + userModel.user_id + "\n" +
//                "user_project_id: " + userModel.user_project_id + "\n" +
//                "role_id: " + userModel.role_id);

        configName.setText(projectInfo.getName());
        configAgreement.setText(projectInfo.getAgreement());

        // TODO: 12.12.2018 remove stubLoadQuiz
        final Button stubLoadQuiz = pView.findViewById(R.id.stub_load);
        stubLoadQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SendQuestionnairesByUserModelExecutable(getBaseActivity(), userModel, HomeFragment.this).execute();
            }
        });

        final Button startBtn = pView.findViewById(R.id.start);
        startBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View pView) {
                getBaseActivity().finish();
                getBaseActivity().startQuestionActivity();
            }
        });
    }

    @Override
    public void onStarting() {
        showProgressBar();
    }

    @Override
    public void onSuccess() {
        hideProgressBar();

        showToast(getString(R.string.success_send_questionnaries));
    }

    @Override
    public void onError(Exception pException) {
        hideProgressBar();

        showToast(pException.toString());
        showToast(getString(R.string.error_send_questionnaries));
    }
}