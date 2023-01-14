package com.svalero.sitescanner_android;

import static com.svalero.sitescanner_android.db.Constants.DATABASE_NAME;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.svalero.sitescanner_android.adapter.PlaceAdapter;
import com.svalero.sitescanner_android.db.AppDatabase;
import com.svalero.sitescanner_android.domain.Place;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ArrayList<Place> places;
    private PlaceAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkExternalStoragePermission();

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
        Log.i("Place: ", "Pasa por aqui");
        for (Place place : places) {
            Log.i("Place: ", place.toString());
        }
        adapter.notifyDataSetChanged();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar, menu);
        return true;
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menuMainAddPlace) {
            Intent intent = new Intent(this, AddPlace.class);
            startActivity(intent);
            return true;
        } else if (item.getItemId() == R.id.menuMainAllMarkers) {
            Intent intent = new Intent(this, ShowMapAll.class);
            intent.putExtra("places", places);
            startActivity(intent);
            return true;
        }
        return false;
    }

    private void checkExternalStoragePermission() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 225);
            }
        }
    }
}