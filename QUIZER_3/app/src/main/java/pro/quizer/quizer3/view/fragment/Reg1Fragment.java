package pro.quizer.quizer3.view.fragment;

import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import pro.quizer.quizer3.R;
import pro.quizer.quizer3.view.Anim;

public class Reg1Fragment extends ScreenFragment implements View.OnClickListener {
    private Button btnNext;

    public Reg1Fragment() {
        super(R.layout.fragment_reg1);
    }

    @Override
    protected void onReady() {
        RelativeLayout cont = (RelativeLayout) findViewById(R.id.cont_reg1_fragment);
        TextView info = (TextView) findViewById(R.id.reg_info);
        btnNext = (Button) findViewById(R.id.btn_next);
        try {
            info.setText(getCurrentUser().getConfigR().getRegistrationInfo());

            //TODO FOR TESTS
            info.setText("Сделайте фото стоя на голове и жонглируя телефонами");
        } catch (Exception e) {
            e.printStackTrace();
        }
        MainFragment.disableSideMenu();

        btnNext.setOnClickListener(this);

        cont.startAnimation(Anim.getAppear(getContext()));
        btnNext.startAnimation(Anim.getAppearSlide(getContext(), 500));

    }

    @Override
    public void onClick(View view) {
        if (view == btnNext) {
            replaceFragment(new Reg2Fragment());
        }
    }

    @Override
    public boolean onBackPressed() {
        replaceFragment(new HomeFragment());
        return true;
    }
}

