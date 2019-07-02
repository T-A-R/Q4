package pro.quizer.quizerexit.view.resizeble.button;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Button;

import pro.quizer.quizerexit.view.resizeble.ResizableViewUtils;


public abstract class AbstractResizableButton extends Button {

    public AbstractResizableButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        ResizableViewUtils.initTextSize(this, context, getDefaultFontSize());
        setCustomFont(context);
    }

    public AbstractResizableButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        ResizableViewUtils.initTextSize(this, context, getDefaultFontSize());
        setCustomFont(context);

    }

    public AbstractResizableButton(Context context) {
        super(context);
        ResizableViewUtils.initTextSize(this, context, getDefaultFontSize());
        setCustomFont(context);
    }

    public abstract int getDefaultFontSize();

    private void setCustomFont(Context ctx) {
//        try {
//            setTypeface(Typeface.createFromAsset(ctx.getAssets(), "fonts/Heliosextc.otf"));
//        } catch (Exception e) {
//
//        }
    }
}
