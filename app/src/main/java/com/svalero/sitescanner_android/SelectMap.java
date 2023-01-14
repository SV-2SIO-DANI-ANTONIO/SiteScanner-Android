package com.svalero.sitescanner_android;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.mapbox.geojson.Point;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

    public void saveMap(View view){
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
                .withIconImage(BitmapFactory.decodeResource(getResources(), R.mipmap.red_marker));
        pointAnnotationManager.create(pointAnnotationOptions);
    }

    private void removeAllMarkers() {
        pointAnnotationManager.deleteAll();
    }

    public void back(View view){
        Intent data = new Intent();
        setResult(RESULT_CANCELED, data);
        finish();
    }

    public void removeMarkers(View view){
        removeAllMarkers();
    }
}