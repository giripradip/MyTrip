package com.example.mytrip.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.mytrip.database.Dao.TripInfoDao;
import com.example.mytrip.database.entity.TripInfo;

@Database(entities = {TripInfo.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "myTripInfo";

    private static AppDatabase sInstance;

    public abstract TripInfoDao tripInfoDao();

    /**
     * --------Function to initialize database if required ----
     **/
    public static AppDatabase getInstance(final Context context) {
        if (sInstance == null) {
            synchronized (AppDatabase.class) {
                if (sInstance == null) {
                    sInstance = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, DATABASE_NAME).build();
                }
            }
        }
        return sInstance;
    }
}

