package com.example.mytripmate;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WeatherResponse {

    @SerializedName("weather")
    public List<Weather> weather;

    @SerializedName("main")
    public Main main;

    public static class Weather {
        @SerializedName("description")
        public String description;
    }

    public static class Main {
        @SerializedName("temp")
        public float temp;
    }
}
