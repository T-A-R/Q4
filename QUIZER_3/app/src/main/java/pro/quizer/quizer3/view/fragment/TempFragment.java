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

import static pro.quizer.quizer3.MainActivity.TAG;

public class TempFragment extends ScreenFragment implements View.OnClickListener {

    private Button btnSend;
    private EditText etKey;

    public TempFragment() {
        super(R.layout.fragment_key);
    }

    @Override
    protected void onReady() {

        initViews();
        MainFragment.disableSideMenu();

    }

    public void initViews() {

        FrameLayout cont = (FrameLayout) findViewById(R.id.cont_key_fragment);
        btnSend = (Button) findViewById(R.id.btn_send_activation);
        etKey = (EditText) findViewById(R.id.et_activation);

        etKey.setTypeface(Fonts.getFuturaPtMedium());
        btnSend.setTypeface(Fonts.getFuturaPtBook());
        btnSend.setTransformationMethod(null);

        btnSend.setOnClickListener(this);

        cont.startAnimation(Anim.getAppear(getContext()));
        btnSend.startAnimation(Anim.getAppearSlide(getContext(), 500));
    }

    @Override
    public void onClick(View view) {
        if (view == btnSend) {

        }
    }



    @Override
    public boolean onBackPressed() {
        return true;
    }
}

