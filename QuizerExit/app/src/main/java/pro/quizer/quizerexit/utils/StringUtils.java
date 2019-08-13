package pro.quizer.quizerexit.utils;

import pro.quizer.quizerexit.Constants;

import static pro.quizer.quizerexit.utils.DateUtils.PATTERN_TOKEN;

public final class StringUtils {

    public static boolean isEmpty(final String pString) {
        return (pString == null || Constants.Strings.EMPTY.equals(pString));
    }

    public static boolean isNotEmpty(final CharSequence pString) {
        return pString != null && pString.length() > 0;
    }


    public static String generateToken() {
        final String currentTimeMillis = String.valueOf(DateUtils.getCurrentTimeMillis());
        final String currentDate = DateUtils.getCurrentFormattedDate(PATTERN_TOKEN);
        final String randomString = new RandomStringUtils(14).nextString();

        return currentDate + currentTimeMillis + randomString;
    }

    public static String cutString(String longString, int maxLenght) {
        return longString.substring(0, Math.min(longString.length(), maxLenght));
    }
}