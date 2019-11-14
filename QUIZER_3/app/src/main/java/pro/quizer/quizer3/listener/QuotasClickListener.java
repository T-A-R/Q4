package pro.quizer.quizer3.listener;

import android.util.Log;
import android.view.View;

import pro.quizer.quizer3.MainActivity;
import pro.quizer.quizer3.executable.ICallback;
import pro.quizer.quizer3.executable.UpdateQuotasExecutable;

import static pro.quizer.quizer3.MainActivity.TAG;

public class QuotasClickListener implements View.OnClickListener {

    private final MainActivity mMainActivity;
    private final ICallback mCallback;

    public QuotasClickListener(final MainActivity pMainActivity, final ICallback pCallback) {
        mMainActivity = pMainActivity;
        mCallback = pCallback;
    }

    public QuotasClickListener(final MainActivity pMainActivity) {
        this(pMainActivity, new ICallback() {

            @Override
            public void onStarting() {
//                mMainActivity.showProgressBar();
            }

            @Override
            public void onSuccess() {
//                pMainActivity.hideProgressBar();
//
//                pMainActivity.showQuotasFragment();
            }

            @Override
            public void onError(Exception pException) {
//                pMainActivity.hideProgressBar();
////                pBaseActivity.showToast(pException.toString());
//                pMainActivity.showQuotasFragment();
            }
        });
    }

    @Override
    public void onClick(View view) {
        Log.d(TAG, "onClick: QQQQQQQQQQQQQQQQQQQQQQQQQQ");
        new UpdateQuotasExecutable(mMainActivity, mCallback).execute();
    }
}
