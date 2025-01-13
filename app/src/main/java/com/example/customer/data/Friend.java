package com.example.customer.data;

import java.io.Serializable;

public class Friend implements Serializable {
    private long user_id;
    private String name;
    private String full_name;
    private String email;
    private String phone;
    private int avatar;
    private String role;
    private String status;

    public Friend(long user_id, String name, String full_name, String email, String phone, int avatar, String role, String status) {
        this.user_id = user_id;
        this.name = name;
        this.full_name = full_name;
        this.email = email;
        this.phone = phone;
        this.avatar = avatar;
        this.role = role;
        this.status = status;
    }

    public long getUserId() {
        return user_id;
    }
    public String getName() {
        return name;
    }
    public String getFullName() {
        return full_name;
    }
    public String getEmail() {
        return email;
    }
    public String getPhone() {
        return phone;
    }
    public int getAvatar() {
        return avatar;
    }
    public String getRole() {
        return role;
    }
    public String getStatus() {
        return status;
    }

}
