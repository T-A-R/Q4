package pro.quizer.quizerexit.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class SizeLimitRelativeLayout extends RelativeLayout implements ISizeLimitView {

    private int mMaxHeight = MATCH_PARENT;

    public SizeLimitRelativeLayout(Context context) {
        super(context);
    }

    public SizeLimitRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SizeLimitRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setMaxHeight(int maxHeight) {
        mMaxHeight = maxHeight;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(mMaxHeight, View.MeasureSpec.AT_MOST));
    }
}
