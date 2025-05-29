package com.example.mytripmate;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class TopPlacesAdapter extends RecyclerView.Adapter<TopPlacesAdapter.PlaceViewHolder> {

    private final List<TopPlacesModel> places;
    private final OnPlaceClickListener listener;

    public interface OnPlaceClickListener {
        void onPlaceClick(TopPlacesModel place);
        void onFavoriteClick(TopPlacesModel place, int position);
    }

    public TopPlacesAdapter(List<TopPlacesModel> places, OnPlaceClickListener listener) {
        this.places = places;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PlaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_top_place, parent, false);
        return new PlaceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaceViewHolder holder, int position) {
        TopPlacesModel place = places.get(position);
        holder.bind(place, position, listener);
    }

    @Override
    public int getItemCount() {
        return places.size();
    }

    static class PlaceViewHolder extends RecyclerView.ViewHolder {
        private final CardView cardView;
        private final ImageView ivPlace;
        private final ImageView ivFavorite;
        private final TextView tvName;
        private final TextView tvLocation;

        public PlaceViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardPlace);
            ivPlace = itemView.findViewById(R.id.ivPlace);
            ivFavorite = itemView.findViewById(R.id.ivFavorite);
            tvName = itemView.findViewById(R.id.tvPlaceName);
            tvLocation = itemView.findViewById(R.id.tvPlaceLocation);
        }

        public void bind(TopPlacesModel place, int position, OnPlaceClickListener listener) {
            ivPlace.setImageResource(place.getImageRes());
            tvName.setText(place.getName());
            tvLocation.setText(place.getLocation());

            ivFavorite.setImageResource(
                    place.isFavorite() ?
                            R.drawable.ic_favorite_filled :
                            R.drawable.ic_favorite_border
            );

            cardView.setOnClickListener(v -> listener.onPlaceClick(place));
            ivFavorite.setOnClickListener(v -> listener.onFavoriteClick(place, position));
        }
    }
}