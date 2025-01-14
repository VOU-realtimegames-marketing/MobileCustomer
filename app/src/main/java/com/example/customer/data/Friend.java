package com.example.customer.data;

import java.io.Serializable;

public class Friend implements Serializable {
    private String name;
    private String full_name;
    private String role;

    public Friend(String name, String full_name, String role) {
        this.name = name;
        this.full_name = full_name;
        this.role = role;
    }


    public String getName() {
        return name;
    }
    public String getFullName() {
        return full_name;
    }
    public String getRole() {
        return role;
    }

}
