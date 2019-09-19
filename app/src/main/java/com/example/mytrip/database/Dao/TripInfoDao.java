package com.example.mytrip.database.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.mytrip.database.entity.TripInfo;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface TripInfoDao {

    @Query("SELECT * FROM TripInfo")
    List<TripInfo> getAll();

    @Insert(onConflict = REPLACE)
    void insert(TripInfo tripInfo);

    @Query("UPDATE TripInfo SET startAddressName=:startAddressName, startAddress = :startAddress, startDateTime=:startTime," +
            " destinationAddressName = :destAddressName, destinationAddress = :destAddress, endDateTime=:destTime WHERE id =:id")
    void update(int id, String startAddressName, String startAddress, int startTime, String destAddressName, String destAddress, int destTime);

    @Update
    void update(TripInfo tripInfo);

    @Delete
    void delete(TripInfo tripInfo);

    @Query("DELETE FROM TripInfo WHERE id = :tId")
    void deleteById(int tId);

    @Query("DELETE FROM TripInfo")
    void truncateTable();

}
