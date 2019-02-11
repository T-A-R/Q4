package pro.quizer.quizerexit.view.resizeble.margin;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import pro.quizer.quizerexit.view.resizeble.ResizableViewUtils;


public class MarginView extends ViewGroup {

    public MarginView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        ResizableViewUtils.initViewHeight(this, context);
    }

    @Override
    protected void onLayout(boolean b, int i, int i1, int i2, int i3) {

    }

    public MarginView(Context context, AttributeSet attrs) {
        super(context, attrs);
        ResizableViewUtils.initViewHeight(this, context);

    }

    public MarginView(Context context) {
        super(context);
        ResizableViewUtils.initViewHeight(this, context);
    }
}
