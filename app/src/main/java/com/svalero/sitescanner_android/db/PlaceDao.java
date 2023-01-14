package com.svalero.sitescanner_android.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.svalero.sitescanner_android.domain.Place;

import java.util.List;

@Dao
public interface PlaceDao {
    @Query("SELECT * FROM place")
    List<Place> getAll();

    @Query("SELECT * FROM place WHERE id = :id")
    Place getById(long id);

    @Insert
    void insert(Place place);

    @Delete
    void delete(Place place);

    @Update
    void update(Place place);
}
