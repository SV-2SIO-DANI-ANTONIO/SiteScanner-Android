package com.svalero.sitescanner_android.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.svalero.sitescanner_android.domain.Preference;

@Dao
public interface PreferenceDAO {
    @Query("SELECT * FROM preference WHERE id=0")
    Preference getPreference();

    @Insert
    void insert(Preference preferences);

    @Update
    void update(Preference preferences);
}
