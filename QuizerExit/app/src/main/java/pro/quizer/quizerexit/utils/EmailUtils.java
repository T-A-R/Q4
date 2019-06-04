package pro.quizer.quizerexit.utils;

import android.content.Context;
import android.content.Intent;

public final class EmailUtils {

    private static final String MESSAGE_RFC_822 = "message/rfc822";

    public static void sendEmail(final Context context, final String to) {
        Intent intent = new Intent(Intent.ACTION_SEND);

        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{to});
        intent.setType(MESSAGE_RFC_822);

        context.startActivity(Intent.createChooser(intent, ""));
    }
}
