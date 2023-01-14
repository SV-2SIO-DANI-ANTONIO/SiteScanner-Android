package com.svalero.sitescanner_android.domain;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(indices = {@Index(value = {"id"}, unique = true)})

public class Place implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private String name;
    private String description;
    private String address;
    private double latitude;
    private double longitude;

    public Place(String name, String description, String address, double latitude, double longitude) {
        this.name = name;
        this.description = description;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
