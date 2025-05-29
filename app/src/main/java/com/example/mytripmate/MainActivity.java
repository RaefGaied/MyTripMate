package com.example.mytripmate;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private TextView tvQuoteText;
    private TextView tvQuoteAuthor;
    private TextView tvWelcomeMessage;

    private static final String CHANNEL_ID = "MyTripMateChannel";
    private static final String PREFS_NAME = "UserPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        createNotificationChannel();

        CardView cvTripPlanner = findViewById(R.id.cvTripPlanner);
        CardView cvExpenses = findViewById(R.id.cvExpenses);
        CardView cvSouvenirs = findViewById(R.id.cvSouvenirs);
        CardView cvReminder = findViewById(R.id.cvReminder);
        ImageButton btnRefreshQuote = findViewById(R.id.btnRefreshQuote);

        tvQuoteText = findViewById(R.id.tvQuoteText);
        tvQuoteAuthor = findViewById(R.id.tvQuoteAuthor);
        tvWelcomeMessage = findViewById(R.id.tvWelcomeMessage);


        displayWelcomeMessage();


        cvTripPlanner.setOnClickListener(v -> startActivity(new Intent(this, TripPlannerActivity.class)));
        cvExpenses.setOnClickListener(v -> startActivity(new Intent(this, TopPlacesActivity.class)));
        cvSouvenirs.setOnClickListener(v -> startActivity(new Intent(this, SouvenirsActivity.class)));


        cvReminder.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (!canScheduleExactAlarms(this)) {
                    requestExactAlarmPermission();
                    return;
                }
            }
            scheduleReminderNotification();
            Toast.makeText(this, "Reminder set successfully!", Toast.LENGTH_SHORT).show();
        });


        btnRefreshQuote.setOnClickListener(v -> fetchTravelQuote());
        fetchTravelQuote();
    }

    private void fetchTravelQuote() {

        tvQuoteText.setText("\"Le voyage est la seule chose qu'on achète qui nous rend plus riche.\"");
        tvQuoteAuthor.setText("- Proverbe africain");


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://zenquotes.io/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        QuoteApiService service = retrofit.create(QuoteApiService.class);
        service.getRandomQuote().enqueue(new Callback<List<QuoteResponse>>() {
            @Override
            public void onResponse(Call<List<QuoteResponse>> call, Response<List<QuoteResponse>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    QuoteResponse quote = response.body().get(0);
                    tvQuoteText.setText("\"" + quote.getQuote() + "\"");
                    tvQuoteAuthor.setText("- " + quote.getAuthor());
                } else {
                    showError("Impossible de charger une nouvelle citation.");
                }
            }

            @Override
            public void onFailure(Call<List<QuoteResponse>> call, Throwable t) {
                showError("Erreur de connexion.");
            }
        });
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Trip Mate Notifications";
            String description = "Channel for trip reminders and notifications.";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private void scheduleReminderNotification() {
        try {
            Intent intent = new Intent(this, NotificationReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

            long triggerAtMillis = System.currentTimeMillis() + 6000;
            android.app.AlarmManager alarmManager = (android.app.AlarmManager) getSystemService(ALARM_SERVICE);
            if (alarmManager != null) {
                alarmManager.setRepeating(android.app.AlarmManager.RTC_WAKEUP,
                        triggerAtMillis,
                        6000,
                        pendingIntent);
            }
        } catch (SecurityException e) {
            Toast.makeText(this, "Permission to schedule exact alarms is required", Toast.LENGTH_LONG).show();
        }
    }

    private boolean canScheduleExactAlarms(Context context) {
        android.app.AlarmManager alarmManager = (android.app.AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        return alarmManager != null && alarmManager.canScheduleExactAlarms();
    }

    private void requestExactAlarmPermission() {
        Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
        startActivity(intent);
    }
    private void displayWelcomeMessage() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String username = prefs.getString("username", "Traveler"); // "Traveler" par défaut
        String welcomeMessage = "Welcome back, " + username + "!";
        tvWelcomeMessage.setText(welcomeMessage);
    }
}