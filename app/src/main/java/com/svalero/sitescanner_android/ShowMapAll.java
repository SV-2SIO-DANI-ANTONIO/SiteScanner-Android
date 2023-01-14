package com.svalero.sitescanner_android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineRequest;
import com.mapbox.android.core.location.LocationEngineResult;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.geojson.Point;
import com.mapbox.maps.CameraOptions;
import com.mapbox.maps.MapView;
import com.mapbox.maps.Style;
import com.mapbox.maps.plugin.annotation.AnnotationConfig;
import com.mapbox.maps.plugin.annotation.AnnotationPlugin;
import com.mapbox.maps.plugin.annotation.AnnotationPluginImplKt;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManagerKt;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions;
import com.svalero.sitescanner_android.domain.Place;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class ShowMapAll extends AppCompatActivity implements Style.OnStyleLoaded {


    private double wayLatitude;
    private double wayLongitude;
    private MapView mapView;
    private PointAnnotationManager pointAnnotationManager;
    private List<Place> places;
    private FusedLocationProviderClient fusedLocationClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_map_all);
        Intent intentFrom = getIntent();
        places = (ArrayList<Place>) intentFrom.getSerializableExtra("places");
        mapView = findViewById(R.id.mapView);
        mapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS, this);
        initializePointAnnotationManager();


    }

    private void initializePointAnnotationManager() {
        AnnotationPlugin annotationPlugin = AnnotationPluginImplKt.getAnnotations(mapView);
        AnnotationConfig annotationConfig = new AnnotationConfig();
        pointAnnotationManager = PointAnnotationManagerKt.createPointAnnotationManager(annotationPlugin, annotationConfig);
    }

    @Override
    public void onStyleLoaded(@NonNull Style style) {

        for (Place place : places) {
            addMarker(place.getLatitude(), place.getLongitude(), place.getName(), R.mipmap.red_marker_foreground);
        }
        gps();
    }

    private void addMarker(double latitude, double longitude, String title, int id) {
        PointAnnotationOptions pointAnnotationOptions = new PointAnnotationOptions()
                .withPoint(Point.fromLngLat(longitude, latitude))
                .withIconImage(BitmapFactory.decodeResource(getResources(), id))
                .withTextField(title);
        pointAnnotationManager.create(pointAnnotationOptions);
    }

    private void setCameraPosition(double latitude, double longitude) {
        CameraOptions cameraPosition = new CameraOptions.Builder()
                .center(Point.fromLngLat(longitude, latitude))
                .pitch(45.0)
                .zoom(15.5)
                .bearing(-17.6)
                .build();
        mapView.getMapboxMap().setCamera(cameraPosition);
    }


    @SuppressLint("MissingPermission")
    private void gps() {
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            wayLongitude = location.getLongitude();
                            wayLatitude = location.getLatitude();
                            Log.i("gps: ", "+++++++++++");
                            Log.i("gps: ", String.valueOf(location.getLongitude()));
                            Log.i("gps: ", String.valueOf(location.getLatitude()));
                            Log.i("gps: ", String.valueOf(location));

                            setCameraPosition(wayLatitude, wayLongitude);
                            addMarker(wayLatitude, wayLongitude, "", R.drawable.b_marker);

                        }
                    }
                });

    }

}

