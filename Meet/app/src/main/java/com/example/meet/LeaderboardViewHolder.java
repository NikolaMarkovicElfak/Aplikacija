package com.example.meet;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class LeaderboardViewHolder extends RecyclerView.ViewHolder {
    Context context;
    List<String> names;
    List<String> points;
    LeaderboardAdapter adapter;

    TextView userName;
    TextView userPoints;
    CircleImageView userPhoto;

    public LeaderboardViewHolder(@NonNull View itemView,Context context,List<String> names,List<String> points,LeaderboardAdapter adapter) {
        super(itemView);
        this.context = context;
        this.names = names;
        this.points = points;
        this.adapter = adapter;

        userName = itemView.findViewById(R.id.leaderboard_username);
        userPoints = itemView.findViewById(R.id.leaderboard_points);
        userPhoto = itemView.findViewById(R.id.leaderboard_photo);
    }
}
