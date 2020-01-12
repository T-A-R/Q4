package pro.quizer.quizer3.utils;

public final class FontUtils {

    public static String VERY_SMALL = "очень маленький шрифт";
    public static String SMALL = "маленький шрифт";
    public static String MEDIUM = "средний шрифт";
    public static String BIG = "большой шрифт";
    public static String VERY_BIG = "очень большой шрифт";

    public static String getCurrentFontName(int position) {
        switch (position) {
            case 0:
                return VERY_SMALL;

            case 1:
                return SMALL;

            case 2:
                return MEDIUM;

            case 3:
                return BIG;

            case 4:
                return VERY_BIG;

            default:
                return MEDIUM;

        }
    }
}
