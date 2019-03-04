package pro.quizer.quizerexit.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class SizeLimitScrollView extends ScrollView implements ISizeLimitView {

    private int mMaxHeight = MATCH_PARENT;

    public SizeLimitScrollView(Context context) {
        super(context);
    }

    public SizeLimitScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SizeLimitScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setMaxHeight(int maxHeight) {
        mMaxHeight = maxHeight;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(mMaxHeight, MeasureSpec.AT_MOST));
    }
}
