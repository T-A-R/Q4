package pro.quizer.quizer3.view.fragment;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import pro.quizer.quizer3.R;
import pro.quizer.quizer3.view.Anim;

public class Reg4Fragment extends ScreenFragment implements View.OnClickListener {
    private Button btnFinish;

    public Reg4Fragment() {
        super(R.layout.fragment_reg4_auto);
    }

    @Override
    protected void onReady() {
        RelativeLayout cont = (RelativeLayout) findViewById(R.id.cont_reg4_fragment);
        ImageView image = (ImageView) findViewById(R.id.quizer_logo);
        btnFinish = (Button) findViewById(R.id.btn_next);

        MainFragment.disableSideMenu();

        btnFinish.setOnClickListener(this);

        cont.startAnimation(Anim.getAppear(getContext()));
        btnFinish.startAnimation(Anim.getAppearSlide(getContext(), 500));

    }

    @Override
    public void onClick(View view) {
        if (view == btnFinish) {

        }
    }

    @Override
    public boolean onBackPressed() {
        replaceFragment(new HomeFragment());
        return true;
    }
}

