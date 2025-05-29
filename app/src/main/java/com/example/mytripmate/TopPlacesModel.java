package com.example.mytripmate;

public class TopPlacesModel {
    private String name;
    private String location;
    private int imageRes;
    private boolean isFavorite;

    public TopPlacesModel(String name, String location, int imageRes) {
        this.name = name;
        this.location = location;
        this.imageRes = imageRes;
        this.isFavorite = false;
    }


    public String getName() { return name; }
    public String getLocation() { return location; }
    public int getImageRes() { return imageRes; }
    public boolean isFavorite() { return isFavorite; }
    public void setFavorite(boolean favorite) { isFavorite = favorite; }
}