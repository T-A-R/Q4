package pro.quizer.quizerexit.listener;

import android.os.Parcel;
import android.view.View;

import pro.quizer.quizerexit.activity.BaseActivity;
import pro.quizer.quizerexit.executable.ICallback;
import pro.quizer.quizerexit.executable.UpdateQuotasExecutable;

public class QuotasClickListener implements View.OnClickListener {

    private final BaseActivity mBaseActivity;
    private final ICallback mCallback;

    public QuotasClickListener(final BaseActivity pBaseActivity, final ICallback pCallback) {
        mBaseActivity = pBaseActivity;
        mCallback = pCallback;
    }

    public QuotasClickListener(final BaseActivity pBaseActivity) {
        this(pBaseActivity, new ICallback() {

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel parcel, int i) {

            }

            @Override
            public void onStarting() {
                pBaseActivity.showProgressBar();
            }

            @Override
            public void onSuccess() {
                pBaseActivity.hideProgressBar();

                pBaseActivity.showQuotasFragment();
            }

            @Override
            public void onError(Exception pException) {
                pBaseActivity.hideProgressBar();
                pBaseActivity.showToast(pException.toString());
                pBaseActivity.showQuotasFragment();
            }
        });
    }

    @Override
    public void onClick(View view) {
        new UpdateQuotasExecutable(mBaseActivity, mCallback).execute();
    }
}
