package com.example.mytripmate;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class TopPlacesActivity extends AppCompatActivity implements TopPlacesAdapter.OnPlaceClickListener {

    private RecyclerView rvTopPlaces;
    private TopPlacesAdapter placesAdapter;
    private List<TopPlacesModel> allPlaces = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_places);

        initializeViews();
        setupAllPlaces();
    }

    private void initializeViews() {
        rvTopPlaces = findViewById(R.id.rvTopPlaces);

        // Configure RecyclerView with 2-column grid
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        rvTopPlaces.setLayoutManager(gridLayoutManager);
        rvTopPlaces.setHasFixedSize(true);
    }

    private void setupAllPlaces() {

        allPlaces.add(new TopPlacesModel("Taj Mahal", "Agra", R.drawable.agra_taj_mahal));
        allPlaces.add(new TopPlacesModel("Golden Temple", "Amritsar", R.drawable.amritsar_golden_temple));
        allPlaces.add(new TopPlacesModel("India Gate", "New Delhi", R.drawable.new_delhi_india_gate));
        allPlaces.add(new TopPlacesModel("Lake Pichola", "Udaipur", R.drawable.udaipur_lake_pichola));
        allPlaces.add(new TopPlacesModel("Ghats", "Varanasi", R.drawable.varanasi_ghat));
        allPlaces.add(new TopPlacesModel("Victoria Memorial", "Kolkata", R.drawable.kolkata_victoria_memorial_hall));

        placesAdapter = new TopPlacesAdapter(allPlaces, this);
        rvTopPlaces.setAdapter(placesAdapter);
    }


    @Override
    public void onPlaceClick(TopPlacesModel place) {

    }

    @Override
    public void onFavoriteClick(TopPlacesModel place, int position) {
        place.setFavorite(!place.isFavorite());
        placesAdapter.notifyItemChanged(position);
    }
}