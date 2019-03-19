package pro.quizer.quizerexit.utils;

import android.content.ClipData;
import android.content.ClipboardManager;

import pro.quizer.quizerexit.R;
import pro.quizer.quizerexit.activity.BaseActivity;

import static android.content.Context.CLIPBOARD_SERVICE;

public final class SystemUtils {

    private static final String SMS = "SMS";

    public static void copyText(final String pText, final BaseActivity pBaseActivity) {
        ClipboardManager clipboard = (ClipboardManager) pBaseActivity.getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(SMS, pText);

        if (clipboard != null) {
            clipboard.setPrimaryClip(clip);
        }

        pBaseActivity.showToast(pBaseActivity.getString(R.string.sms_is_copied));
    }
}
