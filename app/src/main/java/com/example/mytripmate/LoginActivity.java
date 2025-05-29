package com.example.mytripmate;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private TripDatabase db;
    private EditText etEmail, etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        etEmail = findViewById(R.id.etLogin);
        etPassword = findViewById(R.id.etPassword);

        TextView tvSignUp = findViewById(R.id.tvSignUp);

        tvSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterPage.class);
            startActivity(intent);
        });

        db = TripDatabase.getDatabase(getApplicationContext());
    }

    public void loginUser(View view) {

        if (etEmail == null || etPassword == null) {
            Toast.makeText(this, "Erreur: Champs non initialisés", Toast.LENGTH_SHORT).show();
            return;
        }

        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Les champs email et mot de passe sont obligatoires", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Entrez une adresse email valide", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            try {
                User user = db.userDao().loginUser(email, password);
                runOnUiThread(() -> {
                    if (user != null) {

                        getSharedPreferences("UserPrefs", MODE_PRIVATE)
                                .edit()
                                .putString("username", user.getName())
                                .apply();

                        Toast.makeText(this, "Connexion réussie !", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(this, "Email ou mot de passe incorrect", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Erreur de connexion", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                });
            }
        }).start();
    }

    public void goToRegister(View view) {
        Intent intent = new Intent(LoginActivity.this, RegisterPage.class);
        startActivity(intent);
    }

    public void resetFields(View view) {
        EditText usernameEditText = findViewById(R.id.etLogin);
        EditText passwordEditText = findViewById(R.id.etPassword);


        usernameEditText.setText("");
        passwordEditText.setText("");

        Toast.makeText(this, "Fields have been reset", Toast.LENGTH_SHORT).show();
    }

}