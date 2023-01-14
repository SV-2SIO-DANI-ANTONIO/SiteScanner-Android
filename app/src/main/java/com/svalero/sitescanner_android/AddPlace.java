package com.svalero.sitescanner_android;

import static com.svalero.sitescanner_android.db.Constants.DATABASE_NAME;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.room.Room;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteConstraintException;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.svalero.sitescanner_android.db.AppDatabase;
import com.svalero.sitescanner_android.domain.Place;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class AddPlace extends AppCompatActivity {

    private EditText etPlaceName;
    private EditText etPlaceAddress;
    private Place placeEdit;
    private Button registerButton;
    private Bitmap imageBitmap;
    private ImageView imageView;
    private double latitude;
    private double longitude;
    private boolean mapOK = false;
    private boolean imageOK = false;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_MAP_CAPTURE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_place);

        Intent intentFrom = getIntent();
        placeEdit =(Place) intentFrom.getSerializableExtra("place");

        checkCameraPermission();

        etPlaceName = findViewById(R.id.etSiteName);
        etPlaceAddress = findViewById(R.id.etSiteAddress);
        imageView = findViewById(R.id.imageView);
        registerButton = findViewById(R.id.registerButton);

        if(placeEdit != null){
            etPlaceAddress.setText(placeEdit.getAddress());
            etPlaceName.setText(placeEdit.getName());
            registerButton.setText(R.string.addPlaceEdit);
        }
    }

    public void register(View view){
        String name = etPlaceName.getText().toString();
        String address = etPlaceAddress.getText().toString();
        Place place = new Place();
        place.setAddress(address);
        place.setName(name);
        place.setLatitude(latitude);
        place.setLongitude(longitude);

        //Conseguimos la ruta de almacenamiento, si no existe, la creamos
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "SiteScanner");
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }

        //Le ponemos nombre al archivo y la extension
        File imageFile = new File(storageDir, System.currentTimeMillis() + ".jpg");
        Log.i("AddPlace", "register - filePath: " + imageFile);

        if(placeEdit != null) {
            place.setId(placeEdit.getId());

            final AppDatabase db = Room.databaseBuilder(this, AppDatabase.class, DATABASE_NAME).allowMainThreadQueries().build();
            try{
                if(imageOK){
                    //Guardamos el archivo en el almacenamiento
                    saveBitmapToFile(imageBitmap, imageFile);
                    place.setFilePath(imageFile.toString());
                } else {
                    place.setFilePath(placeEdit.getFilePath());
                }

                if (mapOK) {
                    place.setLongitude(latitude);
                    place.setLongitude(longitude);
                } else {
                    place.setLongitude(placeEdit.getLongitude());
                    place.setLongitude(placeEdit.getLatitude());
                }

                db.placeDao().update(place);
                Toast.makeText(this, R.string.correctEdit, Toast.LENGTH_LONG).show();
                //Intent intent = new Intent(this, MainActivity.class);
                //startActivity(intent);
            } catch (SQLiteConstraintException sce) {
                Snackbar.make(etPlaceName, R.string.errorRegister, BaseTransientBottomBar.LENGTH_LONG).show();
            }
        } else {
            final AppDatabase db = Room.databaseBuilder(this, AppDatabase.class, DATABASE_NAME).allowMainThreadQueries().build();
            try{
                if(mapOK) {
                    if (imageOK) {
                        saveBitmapToFile(imageBitmap, imageFile);
                        place.setFilePath(imageFile.toString());
                    }

                    place.setLongitude(latitude);
                    place.setLongitude(longitude);

                    db.placeDao().update(place);

                    //Intent intent = new Intent(this, MainActivity.class);
                    //startActivity(intent);
                } else {
                    Toast.makeText(this, R.string.errorMarker, Toast.LENGTH_LONG).show();
                }
            } catch (SQLiteConstraintException sce) {
                Snackbar.make(etPlaceName, R.string.errorRegister, BaseTransientBottomBar.LENGTH_LONG).show();
            }
        }
    }

    public void back(View view){
        //Intent intent = new Intent(this, MainActivity.class);
        //startActivity(intent);
    }

    public void makePhoto(View view){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    public void selectMap(View view){
        Intent intentMap = new Intent(this, SelectMap.class);
        startActivityForResult(intentMap, REQUEST_MAP_CAPTURE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");

            imageView.setImageBitmap(imageBitmap);
            imageOK = true;
        }
        if(requestCode == REQUEST_MAP_CAPTURE && resultCode == RESULT_OK){
            latitude = data.getDoubleExtra("latitude", 0.);
            longitude = data.getDoubleExtra("longitude", 0.);
            Log.i("AddPlace", "onActivityResult - latitude: " + latitude);
            Log.i("AddPlace", "onActivityResult - longitude: " + longitude);
            Toast.makeText(this, R.string.okMarker, Toast.LENGTH_LONG).show();
            mapOK = true;
        }
        if(requestCode == REQUEST_MAP_CAPTURE && resultCode == RESULT_CANCELED){
            Toast.makeText(this, R.string.noMarker, Toast.LENGTH_LONG).show();
        }
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.CAMERA)) {
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},226);
            }
        }
    }

    private void saveBitmapToFile(Bitmap bitmap, File file) {
        try {
            // Crea un archivo para la imagen en el directorio público de imágenes del almacenamiento externo
            file.createNewFile();

            // Crea un flujo de salida para el archivo
            FileOutputStream fos = new FileOutputStream(file);

            // Comprime el Bitmap y lo escribe en el flujo de salida
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);

            // Cierra el flujo de salida
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}