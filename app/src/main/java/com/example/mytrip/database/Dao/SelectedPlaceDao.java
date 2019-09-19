package com.example.mytrip.database.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.mytrip.database.entity.SelectedPlace;

import java.util.List;

import static androidx.room.OnConflictStrategy.ABORT;
import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface SelectedPlaceDao {

    @Query("SELECT * FROM SelectedPlace")
    List<SelectedPlace> getAllSelectedPlace();

    @Insert(onConflict = ABORT)
    void insert(SelectedPlace selectedPlace);

    @Query("UPDATE SelectedPlace SET isFavourite = :fav WHERE id =:sId")
    void update(int sId, int fav);

    @Update
    void update(SelectedPlace selectedPlace);

    @Delete
    void delete(SelectedPlace selectedPlace);

    @Query("DELETE FROM SelectedPlace WHERE id = :sId")
    void deleteById(int sId);

    @Query("DELETE FROM SelectedPlace")
    void truncateTable();
}
