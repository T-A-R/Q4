package pro.quizer.quizerexit.utils;

import java.util.List;

import pro.quizer.quizerexit.Constants;
import pro.quizer.quizerexit.activity.BaseActivity;
import pro.quizer.quizerexit.executable.ICallback;
import pro.quizer.quizerexit.model.config.PhoneModel;
import pro.quizer.quizerexit.model.sms.SmsStage;

public final class SmsUtils {

    public static void sendSms(final BaseActivity pBaseActivity, final ICallback pCallback, final List<SmsStage> pSmsStages) {
        final StringBuilder sms = new StringBuilder();

        for (final SmsStage smsStage : pSmsStages) {
            sms.append(smsStage.toString());
        }

        final String smsWithPreffix = formatSmsPrefix(sms.toString(), pBaseActivity);

        pBaseActivity.showToast(smsWithPreffix);

        // TODO: 3/18/2019 on sending result
        for (final SmsStage smsStage : pSmsStages) {
            smsStage.markAsSent();
        }

        if (pCallback != null) {
            pCallback.onSuccess();
        }

        //
    }

    public static String formatSmsPrefix(final String pSms, final BaseActivity pBaseActivity) {
        final PhoneModel phoneModel = pBaseActivity.getCurrentUser().getConfig().getProjectInfo().getReserveChannel().getSelectedPhone();

        if (phoneModel != null) {
            final String preffix = phoneModel.getPreffix();

            return StringUtils.isEmpty(preffix) ? pSms : preffix + Constants.Strings.SPACE + pSms;
        } else {
            return pSms;
        }
    }
}
