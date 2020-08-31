package com.example.password_manager.data.model;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface AccountDao {
    @Query("SELECT * FROM account")
    LiveData<List<Account>> getAll();

    @Query("SELECT * FROM account WHERE userId = :userId")
    LiveData<List<Account>> getAllByUser(int userId);

    @Query("SELECT * FROM account WHERE id IN (:accountIds)")
    LiveData<List<Account>> loadAllByIds(int[] accountIds);

    @Query("SELECT * FROM account WHERE id = :accountId")
    Account loadById(int accountId);

    @Query("SELECT * FROM account WHERE service_name LIKE :searchTerm")
    Account searchByService(String searchTerm);

    @Query("DELETE FROM account WHERE id = :accountId")
    void deleteById(int accountId);

    @Insert
    void insertAll(Account... accounts);

    @Delete
    void delete(Account account);
}
