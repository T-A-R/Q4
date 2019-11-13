package pro.quizer.quizerexit.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.AppCompatSeekBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import pro.quizer.quizerexit.BuildConfig;
import pro.quizer.quizerexit.R;
import pro.quizer.quizerexit.utils.EmailUtils;
import pro.quizer.quizerexit.utils.PhoneUtils;
import pro.quizer.quizerexit.utils.UiUtils;

public class AboutFragment extends BaseFragment {

    public static Fragment newInstance() {
        final AboutFragment fragment = new AboutFragment();

        final Bundle bundle = new Bundle();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_about, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final TextView mVersionView = view.findViewById(R.id.version_view);
        UiUtils.setTextOrHide(mVersionView, String.format(getString(R.string.VIEW_APP_VERSION), BuildConfig.VERSION_NAME));

        view.findViewById(R.id.contacts_phone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                PhoneUtils.startCall(getContext(), "+79092144833");
            }
        });
        view.findViewById(R.id.contacts_email).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EmailUtils.sendEmail(getContext(), "sales@quizer.pro");
            }
        });

//        initViews(view);
    }
}