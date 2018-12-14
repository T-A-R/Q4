package pro.quizer.quizerexit.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import pro.quizer.quizerexit.R;
import pro.quizer.quizerexit.activity.BaseActivity;

public class BaseFragment extends Fragment {

    private BaseActivity mBaseActivity;

    public BaseFragment() {

    }

    @Nullable
    public BaseActivity getBaseActivity() {
        return mBaseActivity;
    }

    public View onCreateView(@NonNull LayoutInflater pInflater, ViewGroup pContainer, Bundle pSavedInstanceState) {
        return null;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final Context context = getContext();
        if (context instanceof BaseActivity) {
            mBaseActivity = (BaseActivity) context;
        }
    }

    public void showToast(final CharSequence message) {
        getBaseActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
            }
        });
    }

    public View getProgressBar() {
        final View view = getView();

        if (view != null) {
            return view.findViewById(R.id.progressBar);
        } else {
            return null;
        }
    }

    public void hideProgressBar() {
        getBaseActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                getProgressBar().setVisibility(View.GONE);
            }
        });
    }

    public void showProgressBar() {
        getBaseActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                getProgressBar().setVisibility(View.VISIBLE);
            }
        });
    }

}
