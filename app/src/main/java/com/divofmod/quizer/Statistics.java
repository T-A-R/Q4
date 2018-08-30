package com.divofmod.quizer;

import android.graphics.Color;

import java.util.Random;

public class Statistics {

    private String mTitle;
    private String mPercent;
    private int mColor;

    public Statistics(String title, String percent) {
        mTitle = title;
        mPercent = percent;
        Random random = new Random();
        mColor = Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256));
    }

    public String getTitle() {
        return mTitle;
    }

    public String getPercent() {
        return mPercent;
    }

    public int getColor() {
        return mColor;
    }
}