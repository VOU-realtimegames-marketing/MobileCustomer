package com.example.customer.data;

import java.time.LocalDateTime;

public class Voucher {
    private long voucher_id;
    private long event_id;
    private String qr_code;
    private String type;
    private String status;
    private long discount;
    private LocalDateTime expires_at;


    public Voucher(long voucher_id, long event_id, String qr_code, String type, String status, long discount, LocalDateTime expires_at) {
        this.voucher_id = voucher_id;
        this.event_id = event_id;
        this.qr_code = qr_code;
        this.type = type;
        this.status = status;
        this.discount = discount;
        this.expires_at = expires_at;
    }

    public long getVoucherId() {
        return voucher_id;
    }

    public long getEventId() {
        return event_id;
    }

    public String getQrCode() {
        return qr_code;
    }

    public String getType() {
        return type;
    }

    public String getStatus() {
        return status;
    }

    public long getDiscount() {
        return discount;
    }

    public LocalDateTime getExpiresAt() {
        return expires_at;
    }

}
