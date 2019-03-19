package pro.quizer.quizerexit.fragment;

import android.os.Bundle;
import android.os.Parcel;
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
import pro.quizer.quizerexit.listener.QuotasClickListener;
import pro.quizer.quizerexit.model.config.ConfigModel;
import pro.quizer.quizerexit.model.config.ProjectInfoModel;
import pro.quizer.quizerexit.model.database.UserModel;
import pro.quizer.quizerexit.utils.UiUtils;

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

        final UserModel userModel = getBaseActivity().getUserByUserId(getBaseActivity().getCurrentUserId());

        new SendQuestionnairesByUserModelExecutable(getBaseActivity(), userModel, this, false).execute();
    }

    private void initView(final View pView) {
        final UserModel userModel = getBaseActivity().getUserByUserId(getBaseActivity().getCurrentUserId());
        final ConfigModel config = userModel.getConfig();
        final ProjectInfoModel projectInfo = config.getProjectInfo();

        final TextView configAgreement = pView.findViewById(R.id.config_agreement);
        final TextView configName = pView.findViewById(R.id.config_name);

        UiUtils.setTextOrHide(configName, projectInfo.getName());
        UiUtils.setTextOrHide(configAgreement, projectInfo.getAgreement());

        final Button startBtn = pView.findViewById(R.id.start);
        startBtn.setOnClickListener(new QuotasClickListener(getBaseActivity(), new ICallback() {
            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel parcel, int i) {

            }

            @Override
            public void onStarting() {
                showProgressBar();
            }

            @Override
            public void onSuccess() {
                start();
            }

            @Override
            public void onError(Exception pException) {
                start();
            }
        }));

        final Button quotasBtn = pView.findViewById(R.id.quotas);
        quotasBtn.setOnClickListener(new QuotasClickListener(getBaseActivity()));
    }

    private void start() {
        hideProgressBar();

        getBaseActivity().finish();
        getBaseActivity().startQuestionActivity();
    }

    @Override
    public void onStarting() {
//        showProgressBar();
    }

    @Override
    public void onSuccess() {
//        hideProgressBar();

        if (isAdded()) {
//            showToast(getString(R.string.success_send_questionnaries));
        }
    }

    @Override
    public void onError(final Exception pException) {
//        hideProgressBar();

        if (isAdded()) {
            showToast(getString(R.string.error_send_questionnaries) + "\n" + pException);
        }
    }
}