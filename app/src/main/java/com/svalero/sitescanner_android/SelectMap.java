package com.svalero.sitescanner_android;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.mapbox.maps.plugin.gestures.GesturesPlugin;
import com.mapbox.maps.plugin.gestures.GesturesUtils;

public class SelectMap extends AppCompatActivity implements Style.OnStyleLoaded {

    private MapView mapView;
    private PointAnnotationManager pointAnnotationManager;
    private Point point;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        gps();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_map);
        mapView = findViewById(R.id.mapView);
        mapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS, this);

        GesturesPlugin gesturesPlugin = GesturesUtils.getGestures(mapView);
        gesturesPlugin.addOnMapClickListener(point -> {
            removeAllMarkers();
            this.point = point;
            addMarker(point);
            return true;
        });

        initializePointAnnotationManager();
    }

    private void initializePointAnnotationManager() {
        AnnotationPlugin annotationPlugin = AnnotationPluginImplKt.getAnnotations(mapView);
        AnnotationConfig annotationConfig = new AnnotationConfig();
        pointAnnotationManager = PointAnnotationManagerKt.createPointAnnotationManager(annotationPlugin, annotationConfig);
    }

    @Override
    public void onStyleLoaded(@NonNull Style style) {

    }

    public void saveMap(View view) {
        if (point == null) {
            Toast.makeText(this, R.string.mapMessage, Toast.LENGTH_LONG).show();
        } else {
            Intent data = new Intent();
            data.putExtra("latitude", point.latitude());
            data.putExtra("longitude", point.longitude());
            setResult(RESULT_OK, data);
            finish();
        }
    }

    private void addMarker(Point point) {
        PointAnnotationOptions pointAnnotationOptions = new PointAnnotationOptions()
                .withPoint(point)
                .withIconImage(BitmapFactory.decodeResource(getResources(), R.mipmap.red_marker_foreground));
        pointAnnotationManager.create(pointAnnotationOptions);
    }

    private void removeAllMarkers() {
        pointAnnotationManager.deleteAll();
    }

    public void back(View view) {
        Intent data = new Intent();
        setResult(RESULT_CANCELED, data);
        finish();
    }

    public void removeMarkers(View view) {
        removeAllMarkers();
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


                            setCameraPosition(location.getLatitude(), location.getLongitude());

                        }
                    }
                });

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
}