package com.divofmod.quizer.model.API;

public class Answers {

    private Integer answer_id;
    private Integer duration_time_question;
    private String text_open_answer;

    public Answers() {
    }

    public Answers(final String pAnswerId, final String pDurationTimeQuestion, final String pTextOpenAnswer) {
        answer_id = Integer.valueOf(pAnswerId);
        duration_time_question = Integer.valueOf(pDurationTimeQuestion);
        text_open_answer = pTextOpenAnswer;
    }

    public Integer getAnswerId() {
        return answer_id;
    }

    public void setAnswerId(final Integer pAnswerId) {
        answer_id = pAnswerId;
    }

    public Integer getDurationTimeQuestion() {
        return duration_time_question;
    }

    public void setDurationTimeQuestion(final Integer pDurationTimeQuestion) {
        duration_time_question = pDurationTimeQuestion;
    }

    public String getTextOpenAnswer() {
        return text_open_answer;
    }

    public void setTextOpenAnswer(final String pTextOpenAnswer) {
        text_open_answer = pTextOpenAnswer;
    }
}
