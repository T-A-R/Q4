package pro.quizer.quizerexit.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import pro.quizer.quizerexit.R;

public class UiUtils {

    private static final String STATUS_BAR_HEIGHT = "status_bar_height";
    private static final String DIMEN = "dimen";
    private static final String ANDROID = "android";

    public static void setEnabled(final Context pContext, final View pView, final boolean pIsEnabled) {
        if (pIsEnabled) {
            pView.setEnabled(true);
            pView.setBackgroundColor(ContextCompat.getColor(pContext, R.color.brand_color));
        } else {
            pView.setEnabled(false);
            pView.setBackgroundColor(ContextCompat.getColor(pContext, R.color.gray));
        }
    }

    public static float pxToDp(final Context context, final int px) {
        return px / context.getResources().getDisplayMetrics().density;
    }

    public static float dpToPx(final Context context, final int dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }

    public static int getStatusBarHeight(final Context pContext) {
        final int resourceId = pContext.getResources().getIdentifier(STATUS_BAR_HEIGHT, DIMEN, ANDROID);

        return resourceId > 0 ? pContext.getResources().getDimensionPixelSize(resourceId) : 0;
    }

    private static Drawable getDrawable(final Context pContext, final int pDrawableId) {
        return ContextCompat.getDrawable(pContext, pDrawableId);
    }

    public static Bitmap getBitmap(final Context pContext, final int pDrawableId) {
        final Drawable drawable = getDrawable(pContext, pDrawableId);

        return ((BitmapDrawable) drawable).getBitmap();
    }

    private static void setVisibility(final View pView, final int pVisibility) {
        if (pView != null && pView.getVisibility() != pVisibility) {
            pView.setVisibility(pVisibility);
        }
    }

    public static void setTextOrHide(final TextView pTextView, final String pString) {
        if (pTextView != null) {
            if (!StringUtils.isEmpty(pString)) {
                pTextView.setText(pString);
                setVisibility(pTextView, View.VISIBLE);
            } else {
                setVisibility(pTextView, View.GONE);
            }
        }
    }

    public static void setStrikeThruTextView(final boolean pIsStrikeThru, final TextView... pTextViews) {
        for (final TextView textView : pTextViews) {
            if (pIsStrikeThru) {
                textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                textView.setPaintFlags(textView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            }
        }
    }


    public static void showKeyboard(final Context pContext, final View pView) {
        final InputMethodManager imm = (InputMethodManager) pContext.getSystemService(Context.INPUT_METHOD_SERVICE);

        if (imm != null) {
            imm.showSoftInput(pView, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    public static void hideKeyboard(final Context pContext, final View pView) {
        final InputMethodManager imm = (InputMethodManager) pContext.getSystemService(Activity.INPUT_METHOD_SERVICE);

        if (imm != null) {
            imm.hideSoftInputFromWindow(pView.getWindowToken(), 0);
        }
    }
}
