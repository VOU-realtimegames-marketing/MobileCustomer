package com.example.customer.data;



import java.io.Serializable;
import java.time.LocalDateTime;


public class Event implements Serializable {
    private long event_id;
    private long games_id;
    private long store_id;
    private String event_name;
    private int event_image;
    private int voucher_quantity;
    private LocalDateTime start_time;
    private LocalDateTime end_time;
    private String game_type;
    private String store_name;

    public Event(long event_id, long games_id, long store_id, String event_name, int event_image, int voucher_quantity, LocalDateTime start_time, LocalDateTime end_time, String game_type, String store_name){
        this.event_id = event_id;
        this.games_id = games_id;
        this.store_id = store_id;
        this.event_name = event_name;
        this.event_image = event_image;
        this.voucher_quantity = voucher_quantity;
        this.start_time = start_time;
        this.end_time = end_time;
        this.game_type = game_type;
        this.store_name = store_name;
    }
    public long getEventId(){
        return event_id;
    }
    public long getGamesId(){
        return games_id;
    }
    public long getStoreId(){
        return store_id;
    }
    public String getEventName() {
        return event_name;
    }
    public int getEventImage() {
        return event_image;
    }
    public int getVoucherQuantity() {
        return voucher_quantity;
    }
    public LocalDateTime getStartTime() {
        return start_time;
    }
    public LocalDateTime getEndTime() {
        return end_time;
    }
    public String getGameType() {
        return game_type;
    }
    public String getStoreName() {
        return store_name;
    }
}
