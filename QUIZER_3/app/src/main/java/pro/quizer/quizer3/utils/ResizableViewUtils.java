package pro.quizer.quizer3.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import pro.quizer.quizer3.MainActivity;
import pro.quizer.quizer3.R;
import pro.quizer.quizer3.view.element.ResizableConstants;
import pro.quizer.quizer3.view.fragment.SettingsFragment;
import pro.quizer.quizer3.model.FontSizeModel;

public class ResizableViewUtils {

    public static void initTextSize(final TextView pTextView, final Context pContext, final int pDefFontSize) {
        final FontSizeModel fontSizeModel = SettingsFragment.FONT_SIZE_MODELS.get(((MainActivity) pContext).getFontSizePosition());

//        pTextView.setTextSize(pDefFontSize * fontSizeModel.getScale());
//        pTextView.setTextSize(pDefFontSize * fontSizeModel.getScale());
        switch (pDefFontSize) {
            case ResizableConstants.SMALL:
                pTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, pContext.getResources().getDimensionPixelSize(R.dimen._5sdp) * fontSizeModel.getScale());
                break;
            case ResizableConstants.MEDIUM:
                pTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, pContext.getResources().getDimensionPixelSize(R.dimen._6sdp) * fontSizeModel.getScale());
                break;
            case ResizableConstants.LARGE:
                pTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, pContext.getResources().getDimensionPixelSize(R.dimen._8sdp) * fontSizeModel.getScale());
                break;
        }


    }

    public static void initViewHeight(final ViewGroup pView, final Context pContext) {
        final int answerMargin = ((MainActivity) pContext).getAnswerMargin();

        pView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, (int) UiUtils.dpToPx(pContext, answerMargin)));
    }
}
