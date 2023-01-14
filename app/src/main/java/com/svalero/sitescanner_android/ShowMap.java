package com.svalero.sitescanner_android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

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

public class ShowMap extends AppCompatActivity implements Style.OnStyleLoaded {

    private MapView mapView;
    private PointAnnotationManager pointAnnotationManager;
    private String username;
    private Long userID;
    private Place place;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_map);

        //Conseguir usuario logueado
        Intent intentFrom = getIntent();
        username = intentFrom.getStringExtra("username");
        userID = intentFrom.getLongExtra("userID", 0L);
        place = (Place) intentFrom.getSerializableExtra("place");

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
        addMarker(place.getLatitude(), place.getLongitude(), place.getName());
        setCameraPosition(place.getLatitude(), place.getLongitude());
    }

    private void addMarker(double latitude, double longitude, String title) {
        PointAnnotationOptions pointAnnotationOptions = new PointAnnotationOptions()
                .withPoint(Point.fromLngLat(longitude, latitude))
                .withIconImage(BitmapFactory.decodeResource(getResources(), R.drawable.b_marker))
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


}