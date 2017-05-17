package com.divofmod.quizer;

import android.content.Context;
import android.view.Gravity;
import android.widget.TextView;

abstract class TableHelper {
    public static int getMaxValue(int[] array) {
        Integer maximum = 0;

        for (int i : array) {

            maximum = maximum > i ? maximum : i;
        }
        return maximum;
    }

    public static String doShifts(String string, Context context) {
        if (string.isEmpty())
            return string;
        int minCharsBeforeShift = context.getResources().getInteger(R.integer.min_chars_before_shift);
        StringBuilder temp = new StringBuilder();
        int index = 0;
        for (int i = minCharsBeforeShift; i < string.length(); i++) {
            if (string.charAt(i) == ' ') {
                temp.append(string.substring(index, i)).append("\n");
                index = i + 1;
                i += minCharsBeforeShift;
            }
        }
        temp.append(string.substring(index, string.length()));
        return new String(temp);
    }

    public static TextView makeTableRowWithText(String text, Context context) {
        TextView recyclableTextView = new TextView(context);
        recyclableTextView.setText(TableHelper.doShifts(text, context));
        recyclableTextView.setGravity(Gravity.CENTER);
        return recyclableTextView;
    }
}
