package by.elementshop.utils;

import by.elementshop.Constants;

public final class StringUtils {

    public static boolean isEmpty(final String pString) {
        return (pString == null || Constants.Strings.EMPTY.equals(pString));
    }

    public static boolean isNotEmpty(final CharSequence pString) {
        return pString != null && pString.length() > 0;
    }
}