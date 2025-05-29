package com.example.mytripmate;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageButton;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.TripViewHolder> {

    private List<Trip> tripList;
    private final Map<String, String> weatherCache = new HashMap<>();
    private final OnTripActionListener actionListener;

    public TripAdapter(List<Trip> tripList, OnTripActionListener actionListener) {
        this.tripList = tripList;
        this.actionListener = actionListener;
    }

    public void setTrips(List<Trip> trips) {
        this.tripList = trips;
        notifyDataSetChanged();
    }

    private Retrofit getRetrofitClient() {
        return new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/data/2.5/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    @NonNull
    @Override
    public TripViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trip_item, parent, false);
        return new TripViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TripViewHolder holder, int position) {
        Trip trip = tripList.get(position);
        String destination = trip.getDestination();

        holder.tvDestination.setText(destination);
        holder.tvDates.setText(trip.getStartDate() + " - " + trip.getEndDate());
        holder.tvBudget.setText("Budget: $" + trip.getBudget());
        holder.tvNotes.setText("Notes: " + trip.getNotes());

        // Action buttons
        holder.btnEdit.setOnClickListener(v -> actionListener.onEdit(trip));
        holder.btnDelete.setOnClickListener(v -> actionListener.onDelete(trip));

        if (weatherCache.containsKey(destination)) {
            holder.tvWeather.setText(weatherCache.get(destination));
        } else {
            holder.tvWeather.setText("Chargement météo...");

            WeatherApiService apiService = getRetrofitClient().create(WeatherApiService.class);
            String apiKey = "cac28766847e26031e196d931b95445b";

            WeakReference<TripViewHolder> holderRef = new WeakReference<>(holder);

            Call<WeatherResponse> call = apiService.getWeather(destination, apiKey, "metric");

            call.enqueue(new Callback<WeatherResponse>() {
                @Override
                public void onResponse(@NonNull Call<WeatherResponse> call, @NonNull Response<WeatherResponse> response) {
                    TripViewHolder viewHolder = holderRef.get();
                    if (viewHolder == null) return;

                    if (response.isSuccessful() && response.body() != null) {
                        WeatherResponse body = response.body();

                        String description = (body.weather != null && !body.weather.isEmpty())
                                ? body.weather.get(0).description
                                : "Description inconnue";

                        float tempCelsius = (body.main != null) ? body.main.temp : 0;
                        String weatherText = "Météo: " + description + ", " + Math.round(tempCelsius) + "°C";

                        weatherCache.put(destination, weatherText);
                        viewHolder.tvWeather.setText(weatherText);
                    } else {
                        handleWeatherError(holderRef, destination, "Météo: indisponible");
                    }
                }

                @Override
                public void onFailure(@NonNull Call<WeatherResponse> call, @NonNull Throwable t) {
                    handleWeatherError(holderRef, destination, "Météo: erreur");
                }
            });
        }
    }

    private void handleWeatherError(WeakReference<TripViewHolder> holderRef, String destination, String message) {
        Log.e("TripAdapter", message);
        weatherCache.put(destination, message);
        TripViewHolder viewHolder = holderRef.get();
        if (viewHolder != null) {
            viewHolder.tvWeather.setText(message);
        }
    }

    @Override
    public int getItemCount() {
        return (tripList != null) ? tripList.size() : 0;
    }

    public static class TripViewHolder extends RecyclerView.ViewHolder {
        TextView tvDestination, tvDates, tvBudget, tvNotes, tvWeather;
        ImageButton btnEdit, btnDelete;

        public TripViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDestination = itemView.findViewById(R.id.tvDestination);
            tvDates = itemView.findViewById(R.id.tvDates);
            tvBudget = itemView.findViewById(R.id.tvBudget);
            tvNotes = itemView.findViewById(R.id.tvNotes);
            tvWeather = itemView.findViewById(R.id.tvWeather);
            btnEdit = itemView.findViewById(R.id.btn_update);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}
