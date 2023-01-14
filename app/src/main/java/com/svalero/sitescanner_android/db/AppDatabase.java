package com.svalero.sitescanner_android.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.svalero.sitescanner_android.domain.Place;

@Database(entities = {Place.class}, version = 1)

public abstract class AppDatabase extends RoomDatabase {

    public abstract PlaceDao placeDao();

}
