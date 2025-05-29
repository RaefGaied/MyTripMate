package com.example.mytripmate;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import kotlinx.coroutines.flow.Flow;

@Dao
public interface TripDAO{

    @Insert
    void insert(Trip trip);

    @Update
    void update(Trip trip);

    @Delete
    void delete(Trip trip);

    @Query("SELECT * FROM trips")
    List<Trip> getAllTrips();

    @Query("SELECT * FROM trips WHERE id = :id")
    Flow<Trip> getTripById(int id); // version Flow

    @Query("SELECT * FROM trips WHERE id = :id")
    Trip getTripByIdSync(int id); // version synchronisée

    @Query("UPDATE trips SET destination = :destination, start_date = :startDate, end_date = :endDate, budget = :budget, notes = :notes WHERE id = :id")
    void updateTripFields(int id, String destination, String startDate, String endDate, double budget, String notes);

    @Query("DELETE FROM trips WHERE id = :tripId")
    void deleteTripById(int tripId);
}
