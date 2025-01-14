package com.example.customer.data;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Game implements Serializable {
    private long event_id;
    private long game_id;
    private String game_name;
    private String type;
    private int game_image;
    private LocalDateTime start_time;
    private LocalDateTime end_time;

    public Game(long event_id, long game_id, String type, String game_name, int game_image, LocalDateTime start_time, LocalDateTime end_time) {
        this.event_id = event_id;
        this.game_id = game_id;
        this.type = type;
        this.game_name = game_name;
        this.game_image = game_image;
        this.start_time = start_time;
        this.end_time = end_time;

    }

    public long getEventId() {
        return event_id;
    }

    public long getGameId() {
        return game_id;
    }

    public String getType() {
        return type;
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



}
