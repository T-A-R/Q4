package pro.quizer.quizerexit.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Parcel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import pro.quizer.quizerexit.BuildConfig;
import pro.quizer.quizerexit.Constants;
import pro.quizer.quizerexit.R;
import pro.quizer.quizerexit.executable.ICallback;
import pro.quizer.quizerexit.executable.SendQuestionnairesByUserModelExecutable;
import pro.quizer.quizerexit.listener.QuotasClickListener;
import pro.quizer.quizerexit.model.config.ConfigModel;
import pro.quizer.quizerexit.model.config.ProjectInfoModel;
import pro.quizer.quizerexit.model.database.UserModel;
import pro.quizer.quizerexit.utils.SystemUtils;
import pro.quizer.quizerexit.utils.UiUtils;

import static pro.quizer.quizerexit.activity.BaseActivity.IS_AFTER_AUTH;

public class HomeFragment extends BaseFragment implements ICallback {

    private UserModel mUserModel;

    public static Fragment newInstance(final boolean pIsCanShowUpdateDialog) {
        final HomeFragment fragment = new HomeFragment();

        final Bundle bundle = new Bundle();
        bundle.putBoolean(IS_AFTER_AUTH, pIsCanShowUpdateDialog);
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

        final Bundle bundle = getArguments();

        if (bundle != null && bundle.getBoolean(IS_AFTER_AUTH)) {
            checkUpdates(bundle, mUserModel.getConfig());
        }

        new SendQuestionnairesByUserModelExecutable(getBaseActivity(), mUserModel, this, false).execute();
    }

    private void checkUpdates(final Bundle bundle, final ConfigModel configModel) {
        final boolean isHasUpdate = configModel.getLastAppVersionCode() > BuildConfig.VERSION_CODE;

        if (!isHasUpdate) {
            return;
        }

        final boolean isCriticalUpdate = configModel.isIsCriticalUpdate();
        final String newAppVersion = configModel.getLastAppVersion();
        final String apkUrl = configModel.getApkUrl();

        final String header = getString(R.string.DIALOG_NEW_APP_VERSION_IS_AVAILABLE) + Constants.Strings.SPACE + newAppVersion;
        final String message = isCriticalUpdate ?
                getString(R.string.DIALOG_CRITICAL_UPDATE_TITLE)
                :
                getString(R.string.DIALOG_UPDATE_TITLE);

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setTitle(header);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setPositiveButton(getString(R.string.DIALOG_BUTTON_UPDATE), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SystemUtils.openBrowser(getContext(), apkUrl);
            }
        });

        if (!isCriticalUpdate) {
            alertDialogBuilder.setNegativeButton(getString(R.string.VIEW_CANCEL), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
        }

        alertDialogBuilder.show();

        if (!isCriticalUpdate) {
            bundle.putBoolean(IS_AFTER_AUTH, false);
        }
    }

    private void initView(final View pView) {
        mUserModel = getBaseActivity().getUserByUserId(getBaseActivity().getCurrentUserId());
        final ConfigModel config = mUserModel.getConfig();
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
            showToast(getString(R.string.NOTIFICATION_NO_CONNECTION_SAVING_QUIZ) + "\n" + pException);
        }
    }
}