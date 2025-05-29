package com.example.mytripmate;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

public class RegisterPage extends AppCompatActivity {

    private TripDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registerpage);

        db = TripDatabase.getDatabase(getApplicationContext());
    }

    public void RegisterAccount(View view) {
        EditText nameEditText = findViewById(R.id.etName);
        EditText emailEditText = findViewById(R.id.etEmail);
        EditText passwordEditText = findViewById(R.id.etPassword);
        EditText confirmPasswordEditText = findViewById(R.id.etConfirmPassword);

        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(this, "Tous les champs doivent être remplis", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Entrez une adresse email valide", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "Le mot de passe doit avoir au moins 6 caractères", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Les mots de passe ne correspondent pas", Toast.LENGTH_SHORT).show();
            return;
        }


        User newUser = new User(name, email, password);


        new Thread(() -> {
            db.userDao().insertUser(newUser);
            runOnUiThread(() -> {
                Toast.makeText(this, "Enregistrement réussi !", Toast.LENGTH_SHORT).show();
                reset(view);
            });
        }).start();
    }

    public void reset(View view) {
        EditText etName = findViewById(R.id.etName);
        EditText etEmail = findViewById(R.id.etEmail);
        EditText etPassword = findViewById(R.id.etPassword);
        EditText etConfirmPassword = findViewById(R.id.etConfirmPassword);

        etName.setText("");
        etEmail.setText("");
        etPassword.setText("");
        etConfirmPassword.setText("");
    }
}
