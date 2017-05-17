package com.divofmod.quizer.QuizHelper;


import java.util.HashMap;

public class Audio {
    private HashMap<Integer, String> mAudioRecordQuestions;
    private String mAudioRecordLimitTime;
    private String mAudioSampleRate;

    public Audio(String[] audioRecordQuestions, String audioRecordLimitTime, String audioSampleRate) {
        mAudioRecordQuestions = new HashMap<>();
        for (String audioRecordQuestion : audioRecordQuestions)
            mAudioRecordQuestions.put(Integer.parseInt(audioRecordQuestion), null);
        mAudioRecordLimitTime = audioRecordLimitTime;
        mAudioSampleRate = audioSampleRate;
    }

    public HashMap<Integer, String> getAudioRecordQuestions() {
        return mAudioRecordQuestions;
    }

    public void setAudioRecordQuestions(Integer number, String fileName) {
        mAudioRecordQuestions.put(number, fileName);
    }

    public String getAudioRecordLimitTime() {
        return mAudioRecordLimitTime;
    }

    public String getAudioSampleRate() {
        return mAudioSampleRate;
    }
}
