package com.divofmod.quizer.fragment;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.divofmod.quizer.R;
import com.divofmod.quizer.model.Sms.SmsDatabaseModel;

public class SmsFragment extends Fragment {

    TextView mMessage;
    View mSendButton;
    private static SmsDatabaseModel mSmsDatabaseModel;
    private static SQLiteDatabase mSQLiteDatabase;

    public static SmsFragment newInstance(final SmsDatabaseModel pSmsDatabaseModel, final SQLiteDatabase pSQLiteDatabase) {
        mSmsDatabaseModel = pSmsDatabaseModel;
        mSQLiteDatabase = pSQLiteDatabase;

        final SmsFragment smsFragment = new SmsFragment();
        smsFragment.setModel(pSmsDatabaseModel);
        smsFragment.setSQLiteDatabase(pSQLiteDatabase);

        return smsFragment;
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
        mSendButton = pView.findViewById(R.id.send);
        mMessage = pView.findViewById(R.id.message);

        mMessage.setText(mSmsDatabaseModel.getMessage());
        mSendButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View v) {
//                SmsUtils.sendSMS(true, getActivity(), mSmsDatabaseModel, mSQLiteDatabase, null, null);

                getActivity().getSupportFragmentManager().beginTransaction().remove(SmsFragment.this).commit();
            }
        });
    }
}