package pro.quizer.quizer3.view.fragment;

import android.content.DialogInterface;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import pro.quizer.quizer3.MainActivity;
import pro.quizer.quizer3.R;
import pro.quizer.quizer3.adapter.SmsAdapter;
import pro.quizer.quizer3.database.models.SmsItemR;
import pro.quizer.quizer3.executable.ICallback;
import pro.quizer.quizer3.executable.SmsViewModelExecutable;
import pro.quizer.quizer3.model.config.ElementModelNew;
import pro.quizer.quizer3.model.sms.SmsStage;
import pro.quizer.quizer3.model.view.SmsViewModel;
import pro.quizer.quizer3.utils.SmsUtils;
import pro.quizer.quizer3.utils.UiUtils;
import pro.quizer.quizer3.view.Toolbar;

import static pro.quizer.quizer3.MainActivity.TAG;

public class SmsFragment extends ScreenFragment implements ICallback {

    private Toolbar mToolbar;
    private RecyclerView mSmsRecyclerView;
    private MainActivity mBaseActivity;
    private Map<Integer, ElementModelNew> mMap;
    private View mSendAllSmsBtn;

    public SmsFragment() {
        super(R.layout.fragment_sms);
    }

    @Override
    protected void onReady() {

        initViews();
        MainFragment.disableSideMenu();

    }

    private void refresh() {
        updateData(new SmsViewModelExecutable(mMap, mBaseActivity).execute());
    }

    private void initViews() {
        mBaseActivity = (MainActivity) getMainActivity();
        mMap = mBaseActivity.getMap(false);
        mToolbar = findViewById(R.id.toolbar);
        mToolbar.setTitle(getString(R.string.sync_screen));
        mToolbar.showCloseView(v -> replaceFragment(new SyncFragment()));
        mSmsRecyclerView = findViewById(R.id.sms_recycler_view);
        mSendAllSmsBtn = findViewById(R.id.sms_finished_btn);
        mSendAllSmsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final SmsViewModel mSmsViewModel = new SmsViewModelExecutable(mMap, mBaseActivity).execute();
                if (mSmsViewModel.getNotSentSms(mBaseActivity).size() != 0) {
                    List<String> mSmsNumbers = new ArrayList<>();
                    for (SmsItemR mSmsItem : mSmsViewModel.getNotSentSms(mBaseActivity)) {
                        mSmsNumbers.add(mSmsItem.getSmsNumber());
                    }

                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(mBaseActivity, R.style.AlertDialogTheme);
                    alertDialog.setCancelable(false);
                    alertDialog.setTitle(R.string.dialog_sms_sending);
                    alertDialog.setMessage(R.string.dialog_all_sms_sending_confirmation);
                    alertDialog.setPositiveButton(R.string.view_button_send, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            SmsUtils.sendSms(mBaseActivity, new ICallback() {
                                @Override
                                public void onStarting() {

                                }

                                @Override
                                public void onSuccess() {
                                    refresh();
                                }

                                @Override
                                public void onError(Exception pException) {

                                }
                            }, mSmsViewModel.getSmsStages(), mSmsNumbers);
                        }
                    });
                    alertDialog.setNegativeButton(R.string.view_cancel, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    if (mBaseActivity != null && !mBaseActivity.isFinishing()) {
                        alertDialog.show();
                    }
                } else {
                    showToast(getString(R.string.sms_list_empty));
                }
            }
        });
        refresh();
    }

    private void updateData(final SmsViewModel pSmsViewModel) {
        Log.d(TAG, "updateData: " + pSmsViewModel.toString());
        mBaseActivity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                final List<SmsStage> smsStages = pSmsViewModel.getReadyToSendStages();

                if (smsStages == null || smsStages.isEmpty()) {
                    showEmptyView(getString(R.string.view_empty_sms));
                    Log.d(TAG, getString(R.string.view_empty_sms));
                    return;
                } else {
                    Log.d(TAG, "SMS Stages: " + smsStages.size());
                    hideEmptyView();
                }

                final SmsAdapter mAdapter = new SmsAdapter(mBaseActivity, smsStages);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mBaseActivity);
                mSmsRecyclerView.setLayoutManager(mLayoutManager);
                mSmsRecyclerView.setAdapter(mAdapter);
                int currentStage = 0;

                List<SmsItemR> smsToSend = pSmsViewModel.getNotSentSms(mBaseActivity);
                if (smsToSend.size() > 0 && smsStages.size() > 0) {
                    for (int i = 0; i < smsStages.size(); i++) {
                        if (smsStages.get(i).getSmsAnswers().get(smsToSend.get(0).getSmsNumber()) != null) {
                            currentStage = i;
                            break;
                        }
                    }

                    ((LinearLayoutManager) mLayoutManager).scrollToPositionWithOffset(currentStage, 20);
                } else {

                    long alpha = smsStages.get(0).getTimeTo() - (System.currentTimeMillis() / 1000);
                    for (int i = 0; i < smsStages.size(); i++) {
                        long delta = smsStages.get(i).getTimeTo() - (System.currentTimeMillis() / 1000);
                        if (alpha > 0) {
                            if (delta < alpha) {
                                currentStage = i;
                                alpha = delta;
                            }
                        } else {
                            alpha = delta;
                            if (alpha > 0)
                                currentStage = i;
                        }
                    }

                    if (currentStage != 0)
                        ((LinearLayoutManager) mLayoutManager).scrollToPositionWithOffset(currentStage, 20);
                    else if (alpha < 0)
                        ((LinearLayoutManager) mLayoutManager).scrollToPositionWithOffset(smsStages.size(), 20);
                }
            }
        });
    }

    @Override
    public boolean onBackPressed() {
//        getMainActivity().setHomeFragmentStarted(false);
//        Log.d(TAG, "start Home: 1");
        replaceFragment(new HomeFragment());
        return true;
    }

    @Override
    public void onStarting() {
        if (isAdded()) {
            showToast(getString(R.string.notification_updating));
        }
    }

    @Override
    public void onSuccess() {
        if (isAdded()) {
            refresh();
        }
    }

    @Override
    public void onError(Exception pException) {
        if (isAdded()) {
            refresh();
        }
    }

    public void showEmptyView(final String pError) {
        final MainActivity activity = (MainActivity) getActivity();

        if (activity == null) {
            return;
        }

        activity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                UiUtils.setTextOrHide(getEmptyView(), pError);
            }
        });
    }

    @Nullable
    public TextView getEmptyView() {
        final View view = getView();

        if (view != null) {
            return view.findViewById(R.id.empty_text_label);
        } else {
            return null;
        }
    }

    public void hideEmptyView() {
        final MainActivity activity = (MainActivity) getActivity();

        if (activity == null) {
            return;
        }

        activity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                final View emptyView = getEmptyView();
                if (emptyView != null) {
                    emptyView.setVisibility(View.GONE);
                }
            }
        });
    }
}

