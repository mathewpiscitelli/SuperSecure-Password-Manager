package com.example.password_manager.data;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.room.Room;

import com.example.password_manager.data.model.Account;
import com.example.password_manager.data.model.AppDatabase;
import com.example.password_manager.data.model.User;

import java.util.List;

public class DatabaseInstance {
    private static final String DB_NAME = "database.sqlite";
    private static final String PREPOPULATED_DB_NAME = "database/app.sqlite";
    private static final String MSG_USER_EXISTS = "User already exists. Try again.";
    private static volatile DatabaseInstance db;
    private static AppDatabase dbHandle;

    // Private constructor
    private DatabaseInstance(Context context) {
        // Connect to DB
        dbHandle = Room.databaseBuilder(context, AppDatabase.class, DB_NAME)
                .createFromAsset(PREPOPULATED_DB_NAME)
                .fallbackToDestructiveMigration()
                .build();
    }

    // Singleton access to database
    public static DatabaseInstance getDbInstance(Context context) {
        if (db == null) {
            db = new DatabaseInstance(context);
        }
        return db;
    }

    /*
     * User queries
     */
    public LiveData<List<User>> getAllUsers() {
        return dbHandle.userDao().getAll();
    }

    public LiveData<Integer> getNumUsers() {
        return dbHandle.userDao().getNumUsers();
    }

    public LiveData<List<User>> getUser(String username, String passwordHash) {
        return dbHandle.userDao().getUser(username, passwordHash);
    }

    public List<User> getUserSync(String username, String passwordHash) {
        return dbHandle.userDao().getUserSync(username, passwordHash);
    }

    public List<User> getUserByNameSync(String username) {
        return dbHandle.userDao().getUserByNameSync(username);
    }

    public void createUser(String username, String passwordHash) throws Exception {
        // Ensure user does not already exists before inserting
        List<User> users = getUserByNameSync(username);
        if (users.size() != 0) {
            throw new Exception(MSG_USER_EXISTS);
        } else {
            User user = new User(username, passwordHash);
            dbHandle.userDao().insertAll(user);
        }

    }

    /*
     * Account queries
     */
    public LiveData<List<Account>> getAllAccounts() {
        return dbHandle.accountDao().getAll();
    }

    public LiveData<List<Account>> getAccountsByUser(int userId) {
        return dbHandle.accountDao().getAllByUser(userId);
    }

    public void addAccount(int userId, String serviceName, String username, String password) {
        Account account = new Account(userId, serviceName, username, password);
        dbHandle.accountDao().insertAll(account);
    }

    public void deleteAccount(int id) {
        dbHandle.accountDao().deleteById(id);
    }

    public void close() {
        if (dbHandle.isOpen()) {
            dbHandle.close();
        }
        db = null;
    }

}
