package com.example.meet;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardViewHolder> {
    Context context;
    List<String> names;
    List<String> points;

    public LeaderboardAdapter(Context context,List<String> names,List<String> points)
    {
        this.context = context;
        this.names = names;
        this.points = points;
    }


    @NonNull
    @Override
    public LeaderboardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.leaderboard_card_item,parent,false);
        return new LeaderboardViewHolder(view,context,names,points,this);
    }

    @Override
    public void onBindViewHolder(@NonNull LeaderboardViewHolder holder, int position) {
        holder.userName.setText(names.get(position));
        holder.userPoints.setText(points.get(position));

        StorageReference reference = FirebaseStorage.getInstance().getReference();
        reference.child("UserPhotos").child(names.get(position))
                .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(context)
                                .load(uri)
                                .error(R.drawable.ic_launcher_background)
                                .circleCrop()
                                .into(holder.userPhoto);
                    }
                });
    }

    @Override
    public int getItemCount() {
        return names.size();
    }
}
