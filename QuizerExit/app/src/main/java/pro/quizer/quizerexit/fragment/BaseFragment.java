package pro.quizer.quizerexit.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class BaseFragment extends Fragment {

    public BaseFragment() {
    }

    public View onCreateView(@NonNull LayoutInflater pInflater, ViewGroup pContainer, Bundle pSavedInstanceState) {
        return null;
    }

    public void showToast(final CharSequence message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }
}
