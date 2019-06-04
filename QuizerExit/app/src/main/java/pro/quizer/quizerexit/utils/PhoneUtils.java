package pro.quizer.quizerexit.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public final class PhoneUtils {

    public static void startCall(final Context context, final String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        context.startActivity(intent);
    }
}
