package pro.quizer.quizer3.view;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import pro.quizer.quizer3.R;

public class Anim {

    static public void animateAppear(View view) {
        if (view == null || view.getContext() == null)
            return;

        Animation animation = view.getAnimation();
        if (animation != null) {
            animation.setAnimationListener(null);
            view.clearAnimation();
        }

        view.setVisibility(View.VISIBLE);
        animation = AnimationUtils.loadAnimation(view.getContext(), R.anim.appear);
        view.startAnimation(animation);
    }

    static public Animation getAppear(Context context) {
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.appear);
        animation.setAnimationListener(null);
        return animation;
    }

    static public Animation getAppear(Context context, long startOffset) {
        Animation animation = getAppear(context);
        animation.setStartOffset(startOffset);
        return animation;
    }

    static public Animation getAppearSlide(Context context) {
        return AnimationUtils.loadAnimation(context, R.anim.appear_slide);
    }

    static public Animation getSlideUpDown(Context context) {
        return AnimationUtils.loadAnimation(context, R.anim.slide_up_down);
    }

    static public Animation getAppearSlide(Context context, long startOffset) {
        Animation animation = getAppearSlide(context);
        animation.setStartOffset(startOffset);
        return animation;
    }

    static public Animation getDisappear(Context context) {
        return AnimationUtils.loadAnimation(context, R.anim.disappear);
    }

    static public Animation getDisappear(Context context, final Runnable onEnd) {
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.disappear);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {  }
            @Override
            public void onAnimationEnd(Animation animation) {
                animation.setAnimationListener(null);
                onEnd.run();
            }
            @Override
            public void onAnimationRepeat(Animation animation) { }
        });
        return animation;
    }

    static public void disappearAndInvisible(Context context, final View view) {
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.disappear);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {  }
            @Override
            public void onAnimationEnd(Animation animation) {
                view.clearAnimation();
                view.setVisibility(View.INVISIBLE);
            }
            @Override
            public void onAnimationRepeat(Animation animation) { }
        });
        view.clearAnimation();
        view.setAnimation(animation);
    }

    static public Animation getDisappearSlide(Context context) {
        return AnimationUtils.loadAnimation(context, R.anim.disappear_slide);
    }

    static public Animation getWait(Context context) {
        return AnimationUtils.loadAnimation(context, R.anim.wait);
    }

    static public Animation getBalloon(Context context) {
        return AnimationUtils.loadAnimation(context, R.anim.rise);
    }

    static public Animation getPopUp(Context context) {
        return AnimationUtils.loadAnimation(context, R.anim.popup_email);
    }

    static public Animation getPopUpTwo(Context context) {
        return AnimationUtils.loadAnimation(context, R.anim.popup_up);
    }

    static public Animation getPopUpThree(Context context) {
        return AnimationUtils.loadAnimation(context, R.anim.popup_data);
    }

    static public Animation getPopUpFour(Context context) {
        return AnimationUtils.loadAnimation(context, R.anim.popup_data_down);
    }

    static public Animation getFragmentSlideUp(Context context) {
        return AnimationUtils.loadAnimation(context, R.anim.slide_up);
    }

    static public Animation getFragmentSlideDown(Context context) {
        return AnimationUtils.loadAnimation(context, R.anim.slide_bottom);
    }

    static public Animation getAnimation(Context context, int src) {
        return AnimationUtils.loadAnimation(context, src);
    }

    static public Animation getAnimation(Context context, int src, final Runnable onEnd) {
        Animation animation = AnimationUtils.loadAnimation(context, src);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {  }
            @Override
            public void onAnimationEnd(Animation animation) {
                onEnd.run();
            }
            @Override
            public void onAnimationRepeat(Animation animation) { }
        });
        return animation;
    }
}

