package pro.quizer.quizerexit.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import pro.quizer.quizerexit.R;
import pro.quizer.quizerexit.activity.BaseActivity;
import pro.quizer.quizerexit.adapter.SmsAdapter;
import pro.quizer.quizerexit.database.model.SmsItemR;
import pro.quizer.quizerexit.executable.ICallback;
import pro.quizer.quizerexit.executable.SmsViewModelExecutable;
import pro.quizer.quizerexit.model.config.ElementModel;
import pro.quizer.quizerexit.model.sms.SmsStage;
import pro.quizer.quizerexit.model.view.SmsViewModel;
import pro.quizer.quizerexit.utils.SmsUtils;

import static pro.quizer.quizerexit.activity.BaseActivity.TAG;

public class SmsFragment extends BaseFragment implements ICallback {

    private RecyclerView mSmsRecyclerView;
    private BaseActivity mBaseActivity;
    private Map<Integer, ElementModel> mMap;
    private View mSendAllSmsBtn;

    public static Fragment newInstance() {
        final SmsFragment fragment = new SmsFragment();

        final Bundle bundle = new Bundle();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sms, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);

        refresh();
    }

    private void refresh() {
        updateData(new SmsViewModelExecutable(mMap, mBaseActivity).execute());
    }

    private void initViews(final View pView) {
        mBaseActivity = (BaseActivity) pView.getContext();
        mMap = mBaseActivity.getMap();
        mSmsRecyclerView = pView.findViewById(R.id.sms_recycler_view);
        mSendAllSmsBtn = pView.findViewById(R.id.sms_finished_btn);
        mSendAllSmsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final SmsViewModel mSmsViewModel = new SmsViewModelExecutable(mMap, mBaseActivity).execute();
                if (mSmsViewModel.getNotSentSms().size() != 0) {
                    List<String> mSmsNumbers = new ArrayList<>();
                    for (SmsItemR mSmsItem : mSmsViewModel.getNotSentSms()) {
                        mSmsNumbers.add(mSmsItem.getSmsNumber());
                    }

                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(mBaseActivity, R.style.AlertDialogTheme);
                    alertDialog.setCancelable(false);
                    alertDialog.setTitle(R.string.DIALOG_SMS_SENDING);
                    alertDialog.setMessage(R.string.DIALOG_ALL_SMS_SENDING_CONFIRMATION);
                    alertDialog.setPositiveButton(R.string.VIEW_BUTTON_SEND, new DialogInterface.OnClickListener() {

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
                    alertDialog.setNegativeButton(R.string.VIEW_CANCEL, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    if (!mBaseActivity.isFinishing()) {
                        alertDialog.show();
                    }
                } else {
                    showToast(getString(R.string.SMS_LIST_EMPTY));
                }
            }
        });
    }

    private void updateData(final SmsViewModel pSmsViewModel) {
        mBaseActivity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                final List<SmsStage> smsStages = pSmsViewModel.getReadyToSendStages();

                if (smsStages == null || smsStages.isEmpty()) {
                    showEmptyView(getString(R.string.VIEW_EMPTY_SMS));

                    return;
                }

                hideEmptyView();

                final SmsAdapter mAdapter = new SmsAdapter(mBaseActivity, smsStages);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mBaseActivity);
                mSmsRecyclerView.setLayoutManager(mLayoutManager);
                mSmsRecyclerView.setAdapter(mAdapter);
                int currentStage = 0;

                List<SmsItemR> smsToSend = pSmsViewModel.getNotSentSms();
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
    public void onStarting() {
        if (isAdded()) {
            showToast(getString(R.string.NOTIFICATION_UPDATING));
        }

//        showProgressBar();
    }

    @Override
    public void onSuccess() {
//        hideProgressBar();

        if (isAdded()) {
            refresh();
        }
    }

    @Override
    public void onError(final Exception pException) {
//        hideProgressBar();

        if (isAdded()) {
            refresh();
        }
    }


}