package com.svalero.sitescanner_android.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.svalero.sitescanner_android.domain.Place;
import com.svalero.sitescanner_android.domain.Preference;

@Database(entities = {Place.class, Preference.class}, version = 1)

public abstract class AppDatabase extends RoomDatabase {

    public abstract PlaceDao placeDao();
    public abstract PreferenceDAO preferenceDAO();

}
