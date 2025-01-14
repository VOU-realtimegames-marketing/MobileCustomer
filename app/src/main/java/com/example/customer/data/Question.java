package com.example.customer.data;

public class Question {
    private long event_id;
    private long games_id;
    private int number;
    private int index;
    private String question;
    private String option1;
    private String option2;
    private String option3;
    private String option4;

    public Question(long event_id, long games_id, int number, int index, String question, String option1, String option2, String option3, String option4) {
        this.event_id = event_id;
        this.games_id = games_id;
        this.number = number;
        this.index = index;
        this.question = question;
        this.option1 = option1;
        this.option2 = option2;
        this.option3 = option3;
        this.option4 = option4;
    }

    public long getEvent_id() {
        return event_id;
    }

    public long getGames_id() {
        return games_id;
    }

    public int getNumber() {
        return number;
    }

    public int getIndex() {
        return index;
    }

    public String getQuestion() {
        return question;
    }

    public String getOption1() {
        return option1;
    }

    public String getOption2() {
        return option2;
    }

    public String getOption3() {
        return option3;
    }

    public String getOption4() {
        return option4;
    }

    public String getAnswer() {
        return "Paris";
    }

}
