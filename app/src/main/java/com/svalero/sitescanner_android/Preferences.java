package com.svalero.sitescanner_android;

import static com.svalero.sitescanner_android.db.Constants.DATABASE_NAME;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.svalero.sitescanner_android.db.AppDatabase;
import com.svalero.sitescanner_android.domain.Preference;

public class Preferences extends AppCompatActivity {

    private CheckBox checkBoxMarkers;
    private CheckBox checkBoxVisited;
    private Preference preference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);


        final AppDatabase db = Room.databaseBuilder(this, AppDatabase.class, DATABASE_NAME).allowMainThreadQueries().build();

        try{
            preference = db.preferenceDAO().getPreference();
            Log.i("Prefences - onCreate" , "Preferencias cargadas!");
        } catch (SQLiteConstraintException sce) {
            Log.i("Prefences - onCreate" , "No hay preferencias");
            return;
        }

        db.close();

        checkBoxVisited = findViewById(R.id.defaultVisitedChecked);
        checkBoxMarkers = findViewById(R.id.defaultAllMarkers);

        checkBoxMarkers.setChecked(preference.isDefaultAllMarkers());
        checkBoxVisited.setChecked(preference.isDefaultVisitedSelected());
    }

    public void save(View view){
        final AppDatabase db = Room.databaseBuilder(this, AppDatabase.class, DATABASE_NAME).allowMainThreadQueries().build();
        Preference preferenceNew = new Preference();
        preferenceNew.setId(preference.getId());
        preferenceNew.setDefaultAllMarkers(checkBoxMarkers.isChecked());
        preferenceNew.setDefaultVisitedSelected(checkBoxVisited.isChecked());
        try {
            db.preferenceDAO().update(preferenceNew);
            Toast.makeText(this, R.string.preferceSaved, Toast.LENGTH_LONG).show();
        } catch (SQLiteConstraintException sce) {
            Log.i("Prefences - save" , "Algo ha ocurrido malo");
        }
    }

    public void back(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}