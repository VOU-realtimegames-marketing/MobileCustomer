package com.example.customer.data;

public class Answer {
    private Boolean isCorrect = false;
    private Boolean isWin = false;
    private String answer;
    public Answer(Boolean isCorrect, Boolean isWin, String answer) {
        this.isCorrect = isCorrect;
        this.isWin = isWin;
        this.answer = answer;
    }
    public Boolean getIsCorrect() {
        return isCorrect;
    }
    public Boolean getIsWin() {
        return isWin;
    }

    public String getAnswer() {
        return answer;
    }
}
