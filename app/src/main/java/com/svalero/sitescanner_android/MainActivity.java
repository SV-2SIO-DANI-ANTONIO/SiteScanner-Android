package com.svalero.sitescanner_android;

import static com.svalero.sitescanner_android.db.Constants.DATABASE_NAME;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.os.Bundle;

import com.svalero.sitescanner_android.adapter.PlaceAdapter;
import com.svalero.sitescanner_android.db.AppDatabase;
import com.svalero.sitescanner_android.domain.Place;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private List<Place> places;
    private PlaceAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        places = new ArrayList<>();

        RecyclerView recyclerView = findViewById(R.id.rvListPlaces);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new PlaceAdapter(this, places);
        recyclerView.setAdapter(adapter);
    }
    public void onResume() {
        super.onResume();
        places.clear();
        final AppDatabase db = Room.databaseBuilder(this, AppDatabase.class, DATABASE_NAME).allowMainThreadQueries().build();
        places.addAll(db.placeDao().getAll());
        adapter.notifyDataSetChanged();
    }
}