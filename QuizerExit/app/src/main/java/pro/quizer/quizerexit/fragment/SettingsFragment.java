package pro.quizer.quizerexit.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import pro.quizer.quizerexit.R;
import pro.quizer.quizerexit.executable.ICallback;
import pro.quizer.quizerexit.model.view.SyncViewModel;

public class SettingsFragment extends BaseFragment implements ICallback {

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

//        updateData(new SyncInfoExecutable().execute());
    }


    private void initViews(final View pView) {

    }

    private void initStrings() {

    }

    private void updateData(final SyncViewModel pSyncViewModel) {
        getBaseActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {

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

//        updateData(new SyncInfoExecutable().execute());
    }

    @Override
    public void onError(Exception pException) {
        hideProgressBar();

//        updateData(new SyncInfoExecutable().execute());
    }
}