package pro.quizer.quizerexit.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import pro.quizer.quizerexit.R;
import pro.quizer.quizerexit.executable.ICallback;
import pro.quizer.quizerexit.executable.SettingViewModelExecutable;
import pro.quizer.quizerexit.model.view.SettingsViewModel;
import pro.quizer.quizerexit.utils.UiUtils;

public class SettingsFragment extends BaseFragment implements ICallback {

    private TextView mConfigDateView;
    private TextView mConfigIdView;
    private String mConfigDateString;
    private String mConfigIdString;

    public static Fragment newInstance() {
        final SettingsFragment fragment = new SettingsFragment();

        final Bundle bundle = new Bundle();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        initStrings();

        updateData(new SettingViewModelExecutable(getContext()).execute());
    }


    private void initViews(final View pView) {
        mConfigDateView = pView.findViewById(R.id.settings_date);
        mConfigIdView = pView.findViewById(R.id.settings_id);
    }

    private void initStrings() {
        mConfigDateString = getString(R.string.settings_date_string);
        mConfigIdString = getString(R.string.settings_id_string);
    }

    private void updateData(final SettingsViewModel pSettingsViewModel) {
        getBaseActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                UiUtils.setTextOrHide(mConfigDateView, String.format(mConfigDateString, pSettingsViewModel.getConfigDate()));
                UiUtils.setTextOrHide(mConfigIdView, String.format(mConfigIdString, pSettingsViewModel.getConfigId()));
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

        updateData(new SettingViewModelExecutable(getContext()).execute());
    }

    @Override
    public void onError(final Exception pException) {
        hideProgressBar();

        updateData(new SettingViewModelExecutable(getContext()).execute());
    }
}