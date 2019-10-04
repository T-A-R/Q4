package pro.quizer.quizer3.view.fragment;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;
import pro.quizer.quizer3.R;
import pro.quizer.quizer3.utils.Fonts;
import pro.quizer.quizer3.utils.StringUtils;
import pro.quizer.quizer3.view.Anim;

public class ElementFragment extends ScreenFragment implements View.OnClickListener {
    private Button btnSend;
    private EditText etKey;

    private boolean isKeyBtnPressed = false;
    private boolean isExit = false;

    public ElementFragment() {
        super(R.layout.fragment_key);
    }

    @Override
    protected void onReady() {
        FrameLayout cont = (FrameLayout) findViewById(R.id.cont_key_fragment);
        LinearLayout image = (LinearLayout) findViewById(R.id.cont_image);
        btnSend = (Button) findViewById(R.id.btn_send_activation);
        etKey = (EditText) findViewById(R.id.et_activation);

        MainFragment.disableSideMenu();

        etKey.setTypeface(Fonts.getFuturaPtMedium());
        btnSend.setTypeface(Fonts.getFuturaPtBook());
        btnSend.setTransformationMethod(null);

        btnSend.setOnClickListener(this);

        cont.startAnimation(Anim.getAppear(getContext()));
        btnSend.startAnimation(Anim.getAppearSlide(getContext(), 500));
//        image.startAnimation(Anim.getSlideUpDown(getContext()));

        getUser().setFirstStart(false);
        getUser().setDelegateMode(false);
    }

    @Override
    public void onClick(View view) {
        if (view == btnSend) {
            showScreensaver(false);
            final String key = etKey.getText().toString();

            if (StringUtils.isEmpty(key)) {
                showToast(getString(R.string.empty_key));
                hideScreensaver();
//                hideProgressBar();
                return;
            }
            if (!isKeyBtnPressed) {
                isKeyBtnPressed = true;
            }
        }
    }



    @Override
    public boolean onBackPressed() {
        if (isExit) {
            getActivity().finish();
        } else {
            Toast.makeText(getContext(), "Для выхода нажмите \"Назад\" еще раз", Toast.LENGTH_SHORT).show();
            isExit = true;
        }
        return true;
    }
}

