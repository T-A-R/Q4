package pro.quizer.quizer3.view.screens;

import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import pro.quizer.quizer3.R;
import pro.quizer.quizer3.view.Anim;
import pro.quizer.quizer3.utils.Fonts;
import pro.quizer.quizer3.view.fragment.MainFragment;
import pro.quizer.quizer3.view.fragment.ScreenFragment;
import pro.quizer.quizer3.view.fragment.SmartFragment;


public class PageFragment extends ScreenFragment implements SmartFragment.Listener {
    private ArrayList<SmartFragment> cards;

    private RelativeLayout cont;
    private ScrollView scroll;
    private Button btn1;
    private Button btn2;
    private Button btn3;
    private LinearLayout linear;

//    private PageFragment pickFragment;
    private SmartFragment openedFragment;

    private boolean wins = false;
    private boolean cardsInited = false;
    private int curTab;
    private boolean wasBackPressed = false;

    public PageFragment() {
        super(R.layout.fragment_page);
    }

    @Override
    protected void onReady() {

        cont = (RelativeLayout) findViewById(R.id.cont);
        scroll = (ScrollView) findViewById(R.id.scroll);
        TextView txtTitle = (TextView) findViewById(R.id.title);
        btn1 = (Button) findViewById(R.id.btn_1);
        btn2 = (Button) findViewById(R.id.btn_2);
        btn3 = (Button) findViewById(R.id.btn_3);
        linear = (LinearLayout) findViewById(R.id.linear);

        txtTitle.setTypeface(Fonts.getFuturaPtMedium());
        btn1.setTypeface(Fonts.getFuturaPtBook());
        btn1.setTransformationMethod(null);
        btn2.setTypeface(Fonts.getFuturaPtBook());
        btn2.setTransformationMethod(null);
        btn3.setTypeface(Fonts.getFuturaPtBook());
        btn3.setTransformationMethod(null);

//        btn1.setText(isDelegateScreen() ? R.string.raffles_btn_maquette : R.string.raffles_btn_current);
//        btn2.setText(isDelegateScreen() ? R.string.raffles_btn_active : R.string.raffles_btn_win);
//
//        btn1.setBackgroundResource(isDelegateScreen() ? R.drawable.bg_btn_blue_22 : R.drawable.bg_btn_green_22);
//        btn2.setBackgroundResource(isDelegateScreen() ? R.drawable.bg_btn_blue_22 : R.drawable.bg_btn_green_22);
//        btn3.setBackgroundResource(isDelegateScreen() ? R.drawable.bg_btn_blue_22 : R.drawable.bg_btn_green_22);

        txtTitle.startAnimation(Anim.getAppearSlide(getContext()));
        btn1.startAnimation(Anim.getAppearSlide(getContext(), 500));
        btn2.startAnimation(Anim.getAppearSlide(getContext(), 700));
        btn3.startAnimation(Anim.getAppearSlide(getContext(), 900));
        linear.startAnimation(Anim.getAppear(getContext(), 1500));

        btn1.setOnClickListener(v -> setTab(1));
        btn2.setOnClickListener(v -> setTab(2));
        btn3.setOnClickListener(v -> setTab(3));

        MainFragment.enableSideMenu(true);

        setTab(1);
    }

    @Override
    public boolean isDelegateScreen() {
        return getUser().isDelegateMode();
    }

    @Override
    public boolean isMenuShown() {
        return true;
    }

    public void setTab(int i) {
        curTab = i;
        setBtnPressed(btn1, i == 1);
        setBtnPressed(btn2, i == 2);
        setBtnPressed(btn3, i == 3);

        if (!getUser().isDelegateMode()) {
            setMenuCursor(i == 2 ? 3 : 2);
        }



    }

    private void setBtnPressed(Button btn, boolean pressed) {
        btn.setBackgroundResource(!pressed ? R.drawable.bg_btn_trans : (isDelegateScreen() ? R.drawable.bg_btn_blue_22 : R.drawable.bg_btn_dark));
        btn.setTextColor(!pressed ? Color.GRAY : Color.WHITE);
        btn.setEnabled(!pressed);
    }

//    @Override
//    public void onRestBtn(RaffleCardFragment card) {
//        replaceFragment(new PlaceFragment().setPlace(card.getEvent().getPlace()));
//    }
//
//    @Override
//    public void onPickBtn(RaffleCardFragment card) {
//        showPickFragment(card.getEvent());
//    }
//
//    @Override
//    public void onEditBtn(RaffleCardFragment card) {
//        showAddRaffleFragment(card.getEvent());
//    }


    @Override
    public void fragmentIntent(SmartFragment fragment, String intent) {
        FragmentActivity activity = getActivity();
        if (activity == null)
            return;
    }

    @Override
    public boolean onBackPressed() {

//        if (-----) {
//            replaceFragment(new -----);
//            return true;
//        }

        if (wasBackPressed) {
            return super.onBackPressed();
        }

        wasBackPressed = true;
        Toast.makeText(getContext(), R.string.doubleclick_exit, Toast.LENGTH_SHORT).show();
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        showMenu();
        MainFragment.enableSideMenu(true);
    }

}

