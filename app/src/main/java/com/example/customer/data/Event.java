package com.example.customer.data;



import java.io.Serializable;
import java.time.LocalDateTime;


public class Event implements Serializable {
    private String event_id;
    private String[] games_id;
    private String event_name;
    private int event_image;
    private float voucher_quantity;
    private LocalDateTime start_time;
    private LocalDateTime end_time;

    public Event(String event_id, String[] games_id, String event_name, int event_image, float voucher_quantity, LocalDateTime start_time, LocalDateTime end_time){
        this.event_id = event_id;
        this.games_id = games_id;
        this.event_name = event_name;
        this.event_image = event_image;
        this.voucher_quantity = voucher_quantity;
        this.start_time = start_time;
        this.end_time = end_time;
    }
    public String getEventId(){
        return event_id;
    }
    public String[] getGamesId(){
        return games_id;
    }
    public String getEventName() {
        return event_name;
    }
    public int getEventImage() {
        return event_image;
    }
    public float getVoucherQuantity() {
        return voucher_quantity;
    }
    public LocalDateTime getStartTime() {
        return start_time;
    }
    public LocalDateTime getEndTime() {
        return end_time;
    }

}
