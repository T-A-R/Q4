package com.divofmod.quizer.QuizHelper;

//Класс для определения затраченного времени
public abstract class StopWatch {
    //Время начала
    private static long start;
    private static long globalStart;
    private static long statisticsStart;

    //Объявляем начало отсчета
    public static void setStart() {
        start = System.currentTimeMillis();
    }

    public static void setGlobalStart() {
        globalStart = System.currentTimeMillis();
    }

    public static void setStatisticsStart() {
        statisticsStart = System.currentTimeMillis();
    }

    //возвращаем разницу между временем начала и завершения текущего вопроса
    public static String getTime() {
        return String.valueOf((System.currentTimeMillis() - start) / 1000);
    }

    public static String getGlobalTime() {
        return String.valueOf((System.currentTimeMillis() - globalStart) / 1000);
    }

    public static long getStatisticsTime() {
        return System.currentTimeMillis() - statisticsStart;
    }
}