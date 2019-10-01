package pro.quizer.quizer3.view.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Objects;

import pro.quizer.quizer3.R;
import pro.quizer.quizer3.model.User;
import pro.quizer.quizer3.view.Anim;

public class MenuFragment extends SmartFragment implements View.OnTouchListener, ViewTreeObserver.OnGlobalLayoutListener {

    private RelativeLayout cont;
    private RelativeLayout bg;
    private ImageView cursor;

    private ArrayList<ImageView> imgs = new ArrayList<>();
    private ArrayList<TextView> txts = new ArrayList<>();

    private Listener listener;
    private boolean opened = false;
    private int curIndex = 0;
    private int previousIndex;

    public MenuFragment() {
        super(R.layout.menu);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onReady() {
        cont = (RelativeLayout) findViewById(R.id.cont);
        bg = (RelativeLayout) findViewById(R.id.bg);
        cursor = (ImageView) findViewById(R.id.cursor);

        imgs.add((ImageView) findViewById(R.id.img1));
        imgs.add((ImageView) findViewById(R.id.img2));
        imgs.add((ImageView) findViewById(R.id.img3));
        imgs.add((ImageView) findViewById(R.id.img4));
        imgs.add((ImageView) findViewById(R.id.img5));

        txts.add((TextView) findViewById(R.id.txt1));
        txts.add((TextView) findViewById(R.id.txt2));
        txts.add((TextView) findViewById(R.id.txt3));
        txts.add((TextView) findViewById(R.id.txt4));
        txts.add((TextView) findViewById(R.id.txt5));

        onModeChanged();

        bg.setOnTouchListener(this);

        Objects.requireNonNull(getView()).getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @Override
    public void onGlobalLayout() {
        Objects.requireNonNull(getView()).getViewTreeObserver().removeOnGlobalLayoutListener(this);
        cont.setVisibility(View.GONE);
    }

    public void setPreviousCursor() {
        if (curIndex == 0)
            setCursor(previousIndex);
    }

    public void setCursor(int index) {
        if (curIndex == index)
            return;

        Context context = getContext();
        if (context == null)
            return;

        float sect = (float)bg.getWidth() / 5;
        int offset = cursor.getWidth() / 2;
        float fromX = (curIndex + 0.5f) * sect - offset;
        float toX = (index + 0.5f) * sect - offset;
        TranslateAnimation translateAnimation = new TranslateAnimation(Animation.ABSOLUTE, fromX,  Animation.ABSOLUTE, toX, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
        translateAnimation.setFillAfter(true);
        translateAnimation.setDuration(opened ? 200 : 1);
        translateAnimation.setInterpolator(new DecelerateInterpolator());
        cursor.startAnimation(translateAnimation);

        for (int i = 0; i < imgs.size(); i++) {
            imgs.get(i).setColorFilter(i == index ? Color.WHITE : ContextCompat.getColor(context, R.color.menuGray));
            txts.get(i).setTextColor(i == index ? Color.WHITE : ContextCompat.getColor(context, R.color.menuGray));
        }
        previousIndex = curIndex;
        curIndex = index;
    }

    public void onModeChanged() {
        boolean delegateMenu = User.getUser().isDelegateMode();

        txts.get(1).setText(delegateMenu ? R.string.menu_delegate_raffles : R.string.menu_player_map);
        txts.get(2).setText(delegateMenu ? R.string.menu_delegate_add : R.string.menu_player_raffles);
        txts.get(3).setText(delegateMenu ? R.string.menu_delegate_winners : R.string.menu_player_wins);

        imgs.get(1).setImageResource(delegateMenu ? R.drawable.ico_menu_present : R.drawable.ico_menu_map);
        imgs.get(2).setImageResource(delegateMenu ? R.drawable.ico_menu_add : R.drawable.ico_menu_present);

        cursor.setImageResource(delegateMenu ? R.drawable.bg_circle_blue : R.drawable.bg_circle_dark);
    }

    public void show(boolean s) {
        if (s)
            show();
        else
            hide();
    }

    public void show() {

            if (opened)
                return;
            MainFragment.enableSideMenu();
            cont.setVisibility(View.VISIBLE);
            cont.startAnimation(Anim.getAnimation(getContext(), R.anim.menu_show));
            opened = true;

    }

    public void hide() {
        if (!opened)
            return;
        MainFragment.disableSideMenu();
        cont.startAnimation(Anim.getAnimation(getContext(), R.anim.menu_hide, () -> {
            cont.clearAnimation();
            cont.setVisibility(View.GONE);
        }));
        opened = false;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() != MotionEvent.ACTION_DOWN)
            return false;

        float k =  motionEvent.getX() / bg.getWidth();
        int index = (int) (k * 5);
        if (listener != null)
            listener.onMenuClick(index);

        return true;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public interface Listener {
        void onMenuClick(int index);
    }
}
