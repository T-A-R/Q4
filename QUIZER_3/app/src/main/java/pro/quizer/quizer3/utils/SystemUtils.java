package pro.quizer.quizer3.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import pro.quizer.quizer3.MainActivity;
import pro.quizer.quizer3.R;

import static android.content.Context.CLIPBOARD_SERVICE;

public final class SystemUtils {

    private static final String SMS = "SMS";

    public static void openBrowser(final Context context, final String url) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        context.startActivity(browserIntent);
    }

    public static void copyText(final String pText, final MainActivity pBaseActivity) {
        ClipboardManager clipboard = (ClipboardManager) pBaseActivity.getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(SMS, pText);

        if (clipboard != null) {
            clipboard.setPrimaryClip(clip);
        }

        pBaseActivity.showToastfromActivity(pBaseActivity.getString(R.string.notification_sms_copied));
    }
}