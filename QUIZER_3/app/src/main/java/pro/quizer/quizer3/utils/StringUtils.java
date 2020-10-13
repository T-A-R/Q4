package pro.quizer.quizer3.utils;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import pro.quizer.quizer3.Constants;
import pro.quizer.quizer3.model.SubString;

import static pro.quizer.quizer3.utils.DateUtils.PATTERN_TOKEN;

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

    public static SubString findSubstring(String smallString, String fullString) {
        int k = 0;
        int start = 0;
        final String valueTAG = "value";
        final String titleTAG = "title";

        for (int i = 0; i < fullString.length(); i++) {
            if (smallString.charAt(k) == fullString.charAt(i)) {
                if (k == 0) start = i;
                k++;

                if (k == smallString.length()) {
                    for (int n = i + 1; n < fullString.length(); n++) {
                        if (fullString.charAt(n) == '<') {
                            Log.d("T-L", "NULL: 1");
                            return null;
                        }
                        if (fullString.charAt(n) == '>') {
                            String value;
                            String type;
                            Integer relativeId = null;
                            try {
                                value = fullString.substring(start, n + 1);
                                if (value.contains(valueTAG)) {
                                    type = valueTAG;
                                } else if (value.contains(titleTAG)) {
                                    type = titleTAG;
                                } else {
                                    Log.d("T-L", "NULL: 2");
                                    return null;
                                }

                                for (int m = 6; m < value.length(); m++) {
                                    if (value.charAt(m) == '.') {
                                        String id = value.substring(6, m);
                                        relativeId = Integer.parseInt(id);
                                        break;
                                    }
                                }

                                if (relativeId == null) {
                                    Log.d("T-L", "NULL: 3");
                                    return null;
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.d("T-L", "NULL: 4");
                                return null;
                            }
                            return new SubString(true, start, n, value, type, relativeId);
                        }
                    }

                }
            } else {
                k = 0;
                start = 0;
            }
        }
        return null;
    }

    public static List<String> findExpressions(String startString) {
        List<String> expressions = new ArrayList<>();

        for (int i = 0; i < startString.length() - 4; i++) {
            if (startString.charAt(i) == '<' && startString.charAt(i + 1) == '#') {
                for (int k = i + 2; k < startString.length() - 1; k++) {
                    if (startString.charAt(k) == '#' && startString.charAt(k + 1) == '>') {
                        expressions.add(startString.substring(i, k + 1));
                        i = k + 2;
                        if (i > startString.length() - 4) {
                            return expressions;
                        }
                    }
                }
            }
        }

        return expressions;
    }

    public static String convertExpression(String expression) {
        String text = "";


        return text;
    }
}
