package com.example.customer.data;

import com.example.customer.R;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Game implements Serializable {
    private long event_id;
    private long game_id;
    private String game_name;
    private int game_image;
    private LocalDateTime start_time;
    private LocalDateTime end_time;

    private long quiz_num;

    public Game(long event_id, long game_id, String game_name, int game_image, LocalDateTime start_time, LocalDateTime end_time, long quiz_num) {
        this.event_id = event_id;
        this.game_id = game_id;
        this.game_name = game_name;
        this.game_image = game_image;
        if(game_id == 1) {
            this.game_image = R.drawable.quiz;
        }
        else {
            this.game_image = R.drawable.shake;
        }
        this.start_time = start_time;
        this.end_time = end_time;
        this.quiz_num = quiz_num;

    }

    public long getEventId() {
        return event_id;
    }

    public long getGameId() {
        return game_id;
    }

    public String getGameName() {
        return game_name;
    }
    public int getGameImage() {
        return game_image;
    }
    public LocalDateTime getStartTime() {
        return start_time;
    }

    public LocalDateTime getEndTime() {
        return end_time;
    }
    public long getQuizNum() {
        return quiz_num;
    }

}
