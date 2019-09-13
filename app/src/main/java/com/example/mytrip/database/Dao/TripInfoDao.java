package com.example.mytrip.database.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.mytrip.database.entity.TripInfo;

import java.util.List;

@Dao
public interface TripInfoDao {

    @Query("SELECT * FROM TripInfo")
    List<TripInfo> getAll();

    @Insert
    void insert(TripInfo tripInfo);

    @Delete
    void delete(TripInfo tripInfo);

}
