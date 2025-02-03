package pro.quizer.quizer3.view.fragment;

import android.view.View;
import android.widget.TextView;

import java.util.Objects;

import pro.quizer.quizer3.BuildConfig;
import pro.quizer.quizer3.R;
import pro.quizer.quizer3.utils.EmailUtils;
import pro.quizer.quizer3.utils.UiUtils;
import pro.quizer.quizer3.view.Toolbar;

public class AboutFragment extends ScreenFragment {

    private Toolbar mToolbar;
    private int counter = 0;

    public AboutFragment() {
        super(R.layout.fragment_about);
    }

    @Override
    protected void onReady() {

        mToolbar = findViewById(R.id.toolbar);
        mToolbar.setTitle(getString(R.string.about_screen));
        mToolbar.showCloseView(v -> replaceFragment(new HomeFragment()));

        final TextView mVersionView = findViewById(R.id.version_view);
        UiUtils.setTextOrHide(mVersionView, String.format(getString(R.string.app_ver), BuildConfig.VERSION_NAME));

        findViewById(R.id.contacts_phone).setOnClickListener(v -> {
//                PhoneUtils.startCall(getContext(), "+79092144833");
        });
        findViewById(R.id.contacts_email).setOnClickListener(v -> EmailUtils.sendEmail(Objects.requireNonNull(getContext()), "sales@quizer.pro"));
        mVersionView.setOnClickListener(v -> showConfig());
    }

    private void showConfig() {
        counter++;
        if(counter == 5) {
            getMainActivity().copyToClipboard(getCurrentUser().getConfig());
            TextView tvConfig = findViewById(R.id.tv_config);
            tvConfig.setVisibility(View.VISIBLE);
            tvConfig.setText(getCurrentUser().getConfig());
//            tvConfig.setText(formatString(getCurrentUser().getConfig()));
        }

    }


    @Override
    public boolean onBackPressed() {
        replaceFragment(new HomeFragment());
        return true;
    }

    public String formatString(String text){

        StringBuilder json = new StringBuilder();
        String indentString = "";

        for (int i = 0; i < text.length(); i++) {
            char letter = text.charAt(i);
            switch (letter) {
                case '{':
                case '[':
                    json.append("\n" + indentString + letter + "\n");
                    indentString = indentString + "\t";
                    json.append(indentString);
                    break;
                case '}':
                case ']':
                    indentString = indentString.replaceFirst("\t", "");
                    json.append("\n" + indentString + letter);
                    break;
                case ',':
                    json.append(letter + "\n" + indentString);
                    break;

                default:
                    json.append(letter);
                    break;
            }
        }

        return json.toString();
    }
}

