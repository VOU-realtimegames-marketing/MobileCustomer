package com.example.customer.data;

import java.time.LocalDateTime;

public class Voucher {
    private long voucher_id;
    private long event_id;
    private String event_name;
    private String type;
    private String status;
    private LocalDateTime expires_at;
    private long voucher_quantity;

    public Voucher(long voucher_id, long event_id, String event_name, String type, String status, LocalDateTime expires_at, long voucher_quantity) {
        this.voucher_id = voucher_id;
        this.event_id = event_id;
        this.event_name = event_name;
        this.type = type;
        this.status = status;
        this.expires_at = expires_at;
        this.voucher_quantity = voucher_quantity;
    }

    public long getVoucherId() {
        return voucher_id;
    }

    public long getEventId() {
        return event_id;
    }

    public String getEventName() {
        return event_name;
    }

    public String getType() {
        return type;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getExpiresAt() {
        return expires_at;
    }
    public long getVoucherQuantity() {
        return voucher_quantity;
    }
}
