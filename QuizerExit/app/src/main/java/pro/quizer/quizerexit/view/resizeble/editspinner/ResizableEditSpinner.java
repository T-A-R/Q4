package pro.quizer.quizerexit.view.resizeble.editspinner;

import android.content.Context;
import android.util.AttributeSet;

import pro.quizer.quizerexit.view.resizeble.ResizableViewUtils;
import pro.quizer.quizerexit.view.resizeble.edittext.AbstractResizableEditText;

import static pro.quizer.quizerexit.view.resizable.ResizableConstants.SMALL;

public class ResizableEditSpinner extends AbstractResizableEditSpinner {

    public ResizableEditSpinner(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public ResizableEditSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ResizableEditSpinner(Context context) {
        super(context);
    }

    @Override
    public int getDefaultFontSize() {
        return SMALL;
    }
}
