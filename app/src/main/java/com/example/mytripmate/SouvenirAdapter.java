package com.example.mytripmate;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.List;

public class SouvenirAdapter extends RecyclerView.Adapter<SouvenirAdapter.SouvenirViewHolder> {

    private final Context context;
    private List<Souvenir> souvenirs;

    public SouvenirAdapter(Context context, List<Souvenir> souvenirs) {
        this.context = context;
        this.souvenirs = souvenirs;
    }

    public void setSouvenirs(List<Souvenir> newSouvenirs) {
        this.souvenirs = newSouvenirs;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SouvenirViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_souvenir_item, parent, false);
        return new SouvenirViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SouvenirViewHolder holder, int position) {
        Souvenir souvenir = souvenirs.get(position);

        holder.tvDescription.setText(souvenir.getDescription());

        // Charger la photo à partir du chemin enregistré
        File imgFile = new File(souvenir.getImageUri());
        if (imgFile.exists()) {
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            holder.imageView.setImageBitmap(myBitmap);
        }
    }

    @Override
    public int getItemCount() {
        return souvenirs != null ? souvenirs.size() : 0;
    }

    static class SouvenirViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView tvDescription;

        public SouvenirViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imgSouvenir);
            tvDescription = itemView.findViewById(R.id.tvDescription);
        }
    }
}
