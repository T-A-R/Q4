package pro.quizer.quizer3.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import pro.quizer.quizer3.R;

public class UiUtils {

    private static final String STATUS_BAR_HEIGHT = "status_bar_height";
    private static final String DIMEN = "dimen";
    private static final String ANDROID = "android";

    static int sDisplayWidth = -1;
    static int sDisplayHeight = -1;

    @TargetApi(value = Build.VERSION_CODES.HONEYCOMB_MR2)
    private static void initDisplayDimensions(final Context pContext) {
        final WindowManager wm = (WindowManager) pContext.getSystemService(Context.WINDOW_SERVICE);
        final Display display = wm.getDefaultDisplay();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            final Point size = new Point();
            display.getRealSize(size);
            sDisplayWidth = size.x;
            sDisplayHeight = size.y;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            final Point size = new Point();
            display.getSize(size);
            sDisplayWidth = size.x;
            sDisplayHeight = size.y;
        } else {
            sDisplayWidth = display.getWidth();
            sDisplayHeight = display.getHeight();
        }
    }

    public static int getDisplayHeight(final Context pContext) {
//        if (sDisplayHeight == -1) {
            initDisplayDimensions(pContext);
//        }
        return sDisplayHeight;
    }

    public static int getDisplayWidth(final Context pContext) {
//        if (sDisplayWidth == -1) {
            initDisplayDimensions(pContext);
//        }
        return sDisplayWidth;
    }

    public static void setButtonEnabled(final View pView, final boolean pIsEnabled) {
        final Context context = pView.getContext();

        if (pIsEnabled) {
            pView.setEnabled(true);
            pView.setBackground(ContextCompat.getDrawable(context, R.drawable.button_background_green));
        } else {
            pView.setEnabled(false);
            pView.setBackground(ContextCompat.getDrawable(context, R.drawable.button_background_gray));
        }
    }

    public static void setButtonEnabledRed(final View pView, final boolean pIsEnabled) {
        final Context context = pView.getContext();

        if (pIsEnabled) {
            pView.setEnabled(true);
            pView.setBackground(ContextCompat.getDrawable(context, R.drawable.button_background_red));
        } else {
            pView.setEnabled(false);
            pView.setBackground(ContextCompat.getDrawable(context, R.drawable.button_background_gray));
        }
    }

    public static void setButtonEnabledLightGreen(final View pView, final boolean pIsNormal) {
        final Context context = pView.getContext();

        if (pIsNormal) {
            pView.setEnabled(true);
            pView.setBackground(ContextCompat.getDrawable(context, R.drawable.button_background_green_light));
        } else {
            pView.setEnabled(false);
            pView.setBackground(ContextCompat.getDrawable(context, R.drawable.button_background_gray));
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
                pTextView.setText(Html.fromHtml(String.valueOf(pString)));
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
