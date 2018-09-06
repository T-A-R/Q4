package com.divofmod.quizer.fragment;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.divofmod.quizer.R;
import com.divofmod.quizer.Utils.SmsUtils;
import com.divofmod.quizer.callback.CompleteCallback;
import com.divofmod.quizer.model.Sms.SmsDatabaseModel;

public class SmsFragment extends Fragment {

    public static final int DELAY_MILLIS = 1750;
    TextView mMessage;
    View mSendButton;
    View mProgressFrame;
    TextView mCount;
    private SmsDatabaseModel mSmsDatabaseModel;
    private SQLiteDatabase mSQLiteDatabase;
    private int mReadyToSend;
    private CompleteCallback mCompleteCallback;

    public static SmsFragment newInstance(final SmsDatabaseModel pSmsDatabaseModel, final SQLiteDatabase pSQLiteDatabase, final int pReadyToSend, final CompleteCallback pCompleteCallback) {
        final SmsFragment smsFragment = new SmsFragment();

        smsFragment.setModel(pSmsDatabaseModel);
        smsFragment.setSQLiteDatabase(pSQLiteDatabase);
        smsFragment.setReadyToSendCount(pReadyToSend);
        smsFragment.setCompleteCallback(pCompleteCallback);

        return smsFragment;
    }

    private void setCompleteCallback(final CompleteCallback pCompleteCallback) {
        mCompleteCallback = pCompleteCallback;
    }

    private void setReadyToSendCount(final int pReadyToSend) {
        mReadyToSend = pReadyToSend;
    }

    private void setModel(final SmsDatabaseModel pSmsDatabaseModel) {
        mSmsDatabaseModel = pSmsDatabaseModel;
    }

    private void setSQLiteDatabase(final SQLiteDatabase pSQLiteDatabase) {
        mSQLiteDatabase = pSQLiteDatabase;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sms, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
    }

    private void initViews(final View pView) {
        mCount = pView.findViewById(R.id.count);
        mSendButton = pView.findViewById(R.id.send);
        mMessage = pView.findViewById(R.id.message);
        mProgressFrame = pView.findViewById(R.id.progressFrame);
        mProgressFrame.setVisibility(View.GONE);

        mCount.setText(mReadyToSend + "");
        mMessage.setText(mSmsDatabaseModel.getMessage());
        mSendButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View v) {
                mSendButton.setVisibility(View.INVISIBLE);
                mProgressFrame.setVisibility(View.VISIBLE);

                SmsUtils.sendSMS(true, getActivity(), mSmsDatabaseModel, mSQLiteDatabase, null, null, null);

                final Handler handler = new Handler();

                handler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        getActivity().getSupportFragmentManager().beginTransaction().remove(SmsFragment.this).commit();

                        if (mCompleteCallback != null) {
                            mCompleteCallback.onComplete();
                        }
                    }
                }, DELAY_MILLIS);
            }
        });
    }
}