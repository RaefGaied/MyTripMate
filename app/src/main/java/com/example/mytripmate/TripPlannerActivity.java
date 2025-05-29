package com.example.mytripmate;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TripPlannerActivity extends AppCompatActivity implements OnTripActionListener {

    private RecyclerView tripRecyclerView;
    private TripAdapter tripAdapter;
    private TripDAO tripDAO;
    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "TripPrefs";
    private TextInputEditText editStartDate, editEndDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_trip_planner);


        initializeViews();
        setupDatabase();
        loadTripsFromDb();

        FloatingActionButton btnAdd = findViewById(R.id.btnAddTrip);

        btnAdd.setOnClickListener(v -> showAddTripDialog(null));
    }

    private void initializeViews() {
        tripRecyclerView = findViewById(R.id.tripRecyclerView);
        tripRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        tripAdapter = new TripAdapter(new ArrayList<>(), this);
        tripRecyclerView.setAdapter(tripAdapter);
    }

    private void setupDatabase() {
        tripDAO = TripDatabase.getDatabase(getApplicationContext()).tripDao();
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
    }

    private void showAddTripDialog(Trip tripToEdit) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_trip, null);
        editStartDate = dialogView.findViewById(R.id.editStartDate);
        editEndDate = dialogView.findViewById(R.id.editEndDate);
        setupDatePicker(editStartDate);
        setupDatePicker(editEndDate);

        builder.setView(dialogView)
                .setTitle(tripToEdit == null ? "Ajouter un voyage" : "Modifier le voyage")
                .setPositiveButton(tripToEdit == null ? "Ajouter" : "Enregistrer", null)
                .setNegativeButton("Annuler", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(dialogInterface -> {
            Button btn = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            TextInputEditText editDestination = dialogView.findViewById(R.id.editDestination);
            TextInputEditText editBudget = dialogView.findViewById(R.id.editBudget);
            TextInputEditText editNotes = dialogView.findViewById(R.id.editNotes);

            if (tripToEdit != null) {

                editDestination.setText(tripToEdit.getDestination());
                editStartDate.setText(tripToEdit.getStartDate());
                editEndDate.setText(tripToEdit.getEndDate());
                editBudget.setText(String.valueOf(tripToEdit.getBudget()));
                editNotes.setText(tripToEdit.getNotes());
            } else {

                editStartDate.setText(getCurrentDate());
                editEndDate.setText(getCurrentDate());
            }

            btn.setOnClickListener(view -> validateAndSaveTrip(tripToEdit, dialog,
                    editDestination.getText().toString(),
                    editStartDate.getText().toString(),
                    editEndDate.getText().toString(),
                    editBudget.getText().toString(),
                    editNotes.getText().toString()
            ));
        });

        dialog.show();
    }

    private void setupDatePicker(TextInputEditText dateEditText) {
        dateEditText.setOnClickListener(v -> showDatePicker(dateEditText));
    }

    private void showDatePicker(TextInputEditText targetField) {
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Sélectionnez une date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build();

        datePicker.addOnPositiveButtonClickListener(selection -> {
            String formattedDate = formatDate(selection);
            targetField.setText(formattedDate);
        });

        datePicker.show(getSupportFragmentManager(), "DATE_PICKER");
    }

    private String formatDate(long milliseconds) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date(milliseconds));
    }

    private String getCurrentDate() {
        return formatDate(System.currentTimeMillis());
    }

    private void validateAndSaveTrip(Trip tripToEdit, AlertDialog dialog,
                                     String destination, String startDate,
                                     String endDate, String budgetStr, String notes) {
        if (destination.isEmpty() || startDate.isEmpty() || endDate.isEmpty() || budgetStr.isEmpty()) {
            Toast.makeText(this, "Tous les champs sont obligatoires", Toast.LENGTH_SHORT).show();
            return;
        }

        double budget;
        try {
            budget = Double.parseDouble(budgetStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Budget invalide", Toast.LENGTH_SHORT).show();
            return;
        }

        if (tripToEdit == null) {
            Trip newTrip = new Trip(destination, startDate, endDate, budget, notes);
            saveToPreferences(destination, startDate, endDate, budgetStr, notes);
            insertTrip(newTrip);
        } else {

            tripToEdit.setDestination(destination);
            tripToEdit.setStartDate(startDate);
            tripToEdit.setEndDate(endDate);
            tripToEdit.setBudget(budget);
            tripToEdit.setNotes(notes);
            updateTrip(tripToEdit);
        }
        dialog.dismiss();
    }

    private void saveToPreferences(String destination, String startDate,
                                   String endDate, String budgetStr, String notes) {
        sharedPreferences.edit()
                .putString("destination", destination)
                .putString("startDate", startDate)
                .putString("endDate", endDate)
                .putString("budget", budgetStr)
                .putString("notes", notes)
                .apply();
    }

    private void insertTrip(Trip trip) {
        new Thread(() -> {
            tripDAO.insert(trip);
            runOnUiThread(this::loadTripsFromDb);
        }).start();
    }

    private void updateTrip(Trip trip) {
        new Thread(() -> {
            tripDAO.update(trip);
            runOnUiThread(this::loadTripsFromDb);
        }).start();
    }

    private void loadTripsFromDb() {
        new Thread(() -> {
            List<Trip> trips = tripDAO.getAllTrips();
            runOnUiThread(() -> tripAdapter.setTrips(trips));
        }).start();
    }

    @Override
    public void onEdit(Trip trip) {
        showAddTripDialog(trip);
    }

    @Override
    public void onDelete(Trip trip) {
        new AlertDialog.Builder(this)
                .setTitle("Supprimer le voyage")
                .setMessage("Voulez-vous vraiment supprimer ce voyage ?")
                .setPositiveButton("Oui", (dialog, which) -> {
                    new Thread(() -> {
                        tripDAO.delete(trip);
                        runOnUiThread(this::loadTripsFromDb);
                    }).start();
                })
                .setNegativeButton("Non", null)
                .show();
    }
}