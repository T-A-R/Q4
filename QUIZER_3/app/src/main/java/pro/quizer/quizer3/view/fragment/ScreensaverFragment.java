package pro.quizer.quizer3.view.fragment;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import pro.quizer.quizer3.utils.Fonts;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import pro.quizer.quizer3.R;
import pro.quizer.quizer3.model.User;
import pro.quizer.quizer3.view.Anim;

public class ScreensaverFragment extends SmartFragment implements View.OnClickListener {

    private View cont;
    private View bg;
    private View bgFull;
    private TextView title;
    private ArrayList<ImageView> imgs = new ArrayList<>();

    private Timer timer;
    private boolean visible;
    private int turn;

    public ScreensaverFragment() {
        super(R.layout.fragment_screensaver);
    }

    @Override
    protected void onReady() {
        cont = findViewById(R.id.cont);
        bg = findViewById(R.id.bg);
        bgFull = findViewById(R.id.bg_full);
        title = (TextView) findViewById(R.id.title);
        imgs.add((ImageView) findViewById(R.id.img1));
        imgs.add((ImageView) findViewById(R.id.img2));
        imgs.add((ImageView) findViewById(R.id.img3));
        imgs.add((ImageView) findViewById(R.id.img4));

        title.setTypeface(Fonts.getFuturaPtBook());

        cont.setOnClickListener(this);

        hide();
    }

    public void show(String title, boolean full) {

        bg.post(() -> {
            if (visible) {
                return;
            }

            visible = true;

            this.title.setText(title);
            this.title.startAnimation(Anim.getAppearSlide(getContext(), 1000));

            if (User.getUser().isDelegateMode()) {
                for (int i = 0; i < imgs.size(); i++) {
                    imgs.get(i).setImageResource(R.drawable.img_anim_wait_blue);
                }
            } else {
                for (int i = 0; i < imgs.size(); i++) {
                    imgs.get(i).setImageResource(R.drawable.img_anim_wait);
                }
            }

            for (int i = 0; i < imgs.size(); i++) {
                imgs.get(i).setVisibility(View.GONE);
            }

            cont.setVisibility(View.VISIBLE);

            if (full) {
                cont.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
                bgFull.setVisibility(View.VISIBLE);
            } else {
                cont.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
                bgFull.setVisibility(View.GONE);
            }

            turn = 0;
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    bg.post(() -> {
                        Context context = getContext();
                        if (context == null) {
                            hide();
                            return;
                        }

                        if (turn >= imgs.size()) {
                            if (timer != null) {
                                timer.cancel();
                                timer = null;
                            }
                            return;
                        }

                        final ImageView img = imgs.get(turn);
                        img.startAnimation(Anim.getWait(getContext()));
                        img.setVisibility(View.VISIBLE);

                        turn++;
                    });
                }
            }, 0, 2000);
        });
    }

    public void hide() {
        bg.post(() -> {
            if (timer != null) {
                timer.cancel();
                timer = null;
            }
            bg.clearAnimation();
            title.clearAnimation();
            for (int i = 0; i < imgs.size(); i++) {
                imgs.get(i).clearAnimation();
            }
            cont.setVisibility(View.GONE);
            visible = false;
        });
    }

    @Override
    public void onClick(View view) {

    }
}

