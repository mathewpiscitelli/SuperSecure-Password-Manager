package com.example.password_manager.data.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/*
 * Represents an entry in the password manager.
 */
@Entity
public class Account {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    public int id;

    @NonNull
    public int userId;

    @ColumnInfo(name = "service_name")
    public String serviceName;

    @ColumnInfo(name = "username")
    public String username;

    @ColumnInfo(name = "password")
    public String password;

    public Account(int userId, String serviceName, String username, String password) {
        this.userId = userId;
        this.serviceName = serviceName;
        this.username = username;
        this.password = password;
    }

    public String getServiceName() { return serviceName; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
}
