package pro.quizer.quizer3.view.element.textview;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import pro.quizer.quizer3.utils.ResizableViewUtils;

//import pro.quizer.quizer3.view.element.ResizableViewUtils;

public abstract class AbstractResizableTextView extends android.support.v7.widget.AppCompatTextView {

    public AbstractResizableTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        ResizableViewUtils.initTextSize(this, context, getDefaultFontSize());
//        if (AVIA)
//            setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/arial.ttf"));
    }

    public AbstractResizableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        ResizableViewUtils.initTextSize(this, context, getDefaultFontSize());
//        if (AVIA)
//            setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/arial.ttf"));

    }

    public AbstractResizableTextView(Context context) {
        super(context);
        ResizableViewUtils.initTextSize(this, context, getDefaultFontSize());
//        if (AVIA)
//            setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/arial.ttf"));
    }

    public abstract int getDefaultFontSize();
}
