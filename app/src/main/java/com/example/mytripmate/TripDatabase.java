package com.example.mytripmate;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Trip.class, User.class, Souvenir.class}, version = 3, exportSchema = false)
public abstract class TripDatabase extends RoomDatabase {

    private static volatile TripDatabase INSTANCE;

    public abstract TripDAO tripDao();
    public abstract UserDAO userDao();
    public abstract SouvenirDao souvenirDao();

    public static TripDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (TripDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    TripDatabase.class,
                                    "trip_database"
                            )
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}

