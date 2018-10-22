package pro.quizer.quizerexit.utils;

import pro.quizer.quizerexit.Constants;

public final class StringUtils {

    public static boolean isEmpty(final Object pString) {
        return (pString == null || Constants.Strings.EMPTY.equals(pString));
    }

    public static boolean isNotEmpty(final CharSequence pString) {
        return pString != null && pString.length() > 0;
    }
}