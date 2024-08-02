package pro.quizer.quizer3.utils;

import pro.quizer.quizer3.Constants;

import static pro.quizer.quizer3.utils.DateUtils.PATTERN_TOKEN;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    public static String getJson(Object object) {
        Gson gson = new Gson();
        return gson.toJson(object);
    }

    public static List<Integer> getListIntFromString(String string) {
        JSONArray array = null;
        try {
            array = new JSONArray(string);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (array != null) {
            Integer[] numbers = new Integer[array.length()];
            for (int i = 0; i < array.length(); ++i) {
                numbers[i] = array.optInt(i);
            }
            return Arrays.asList(numbers);
        } else return new ArrayList<>();
    }
}
