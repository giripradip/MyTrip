package com.example.mytrip.database.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
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

    @Query("UPDATE TripInfo SET startAddressId = :startAddressId, startAddressName=:startAddressName, startAddress = :startAddress, startDateTime=:startTime ,destinationAddressId = :destAddressId," +
            " destinationAddressName = :destAddressName, destinationAddress = :destAddress, endDateTime=:destTime WHERE id =:id")
    void update(int id, String startAddressId, String startAddressName, String startAddress, int startTime, String destAddressId,
                String destAddressName, String destAddress, int destTime);

    @Delete
    void delete(TripInfo tripInfo);

    @Query("DELETE FROM TripInfo WHERE id = :tId")
    void deleteById(int tId);

    @Query("DELETE FROM TripInfo")
    void truncateTable();

}
