package com.example.password_manager.data.model;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {User.class, Account.class}, version = 17)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDao userDao();
    public abstract AccountDao accountDao();
}

