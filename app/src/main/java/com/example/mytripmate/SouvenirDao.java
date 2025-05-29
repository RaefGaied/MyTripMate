package com.example.mytripmate;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface SouvenirDao {

    @Insert
    void insert(Souvenir souvenir);

    @Query("SELECT * FROM souvenirs ORDER BY id DESC")
    List<Souvenir> getAllSouvenirs();
}
