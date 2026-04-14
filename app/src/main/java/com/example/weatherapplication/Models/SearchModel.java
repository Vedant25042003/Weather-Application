package com.example.weatherapplication.Models;

import com.google.gson.annotations.SerializedName;

public class SearchModel {

    @SerializedName("name")
    private String Name;

    public String getName() {
        return Name;
    }
}
