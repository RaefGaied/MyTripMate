package com.example.mytripmate;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class SouvenirsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SouvenirAdapter adapter;
    private List<Souvenir> souvenirs;
    private TripDatabase db;
    private String currentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_souvenirs);

        recyclerView = findViewById(R.id.recyclerViewSouvenirs);
        Button btnCamera = findViewById(R.id.btnTakePhoto);

        db = TripDatabase.getDatabase(this);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        loadSouvenirs();

        btnCamera.setOnClickListener(view -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                takePhoto();
            } else {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA);
            }
        });
    }

    private void loadSouvenirs() {
        new Thread(() -> {
            souvenirs = db.souvenirDao().getAllSouvenirs(); // Utilisation du DAO
            runOnUiThread(() -> {
                adapter = new SouvenirAdapter(this, souvenirs);
                recyclerView.setAdapter(adapter);
            });
        }).start();
    }

    ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    takePhoto();
                } else {
                    Toast.makeText(this, "Permission caméra refusée", Toast.LENGTH_SHORT).show();
                }
            });

    private void takePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile;
        try {
            photoFile = createImageFile();
        } catch (IOException e) {
            Toast.makeText(this, "Erreur de fichier", Toast.LENGTH_SHORT).show();
            return;
        }

        Uri photoURI = FileProvider.getUriForFile(this, "com.example.mytripmate.fileprovider", photoFile);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
        capturePhotoLauncher.launch(takePictureIntent);
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(fileName, ".jpg", storageDir);
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private final ActivityResultLauncher<Intent> capturePhotoLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    showDescriptionDialog();
                }
            });

    private void showDescriptionDialog() {
        final EditText input = new EditText(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("Ajouter une description")
                .setView(input)
                .setPositiveButton("Enregistrer", (dialog, which) -> {
                    String description = input.getText().toString();
                    saveSouvenir(description, currentPhotoPath);
                })
                .setNegativeButton("Annuler", null);
        builder.show();
    }

    private void saveSouvenir(String description, String photoPath) {
        Souvenir souvenir = new Souvenir(photoPath, description);
        new Thread(() -> {
            db.souvenirDao().insert(souvenir);
            runOnUiThread(this::loadSouvenirs);
        }).start();
    }
}
