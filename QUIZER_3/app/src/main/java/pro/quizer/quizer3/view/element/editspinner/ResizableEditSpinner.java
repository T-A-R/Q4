package pro.quizer.quizer3.view.element.editspinner;

import android.content.Context;
import android.util.AttributeSet;

import com.reginald.editspinner.EditSpinner;

//import static pro.quizer.quizerexit.view.resizable.ResizableConstants.SMALL;

//public class ResizableEditSpinner extends AbstractResizableEditSpinner {
public class ResizableEditSpinner extends EditSpinner {

    public ResizableEditSpinner(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public ResizableEditSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ResizableEditSpinner(Context context) {
        super(context);
    }

}
