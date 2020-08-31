package com.example.password_manager.data.model;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface UserDao {
    @Query("SELECT * FROM user")
    LiveData<List<User>> getAll();

    @Query("SELECT * FROM user WHERE UPPER(username) = UPPER(:username) and password_hash = :passwordHash")
    LiveData<List<User>> getUser(String username, String passwordHash);

    @Query("SELECT * FROM user WHERE UPPER(username) = UPPER(:username) and password_hash = :passwordHash")
    List<User> getUserSync(String username, String passwordHash);

    @Query("SELECT * FROM user WHERE UPPER(username) = UPPER(:username)")
    List<User> getUserByNameSync(String username);

    @Query("SELECT COUNT(id) FROM user")
    LiveData<Integer> getNumUsers();

    @Insert
    void insertAll(User... users);

    @Delete
    void delete(User user);
}
