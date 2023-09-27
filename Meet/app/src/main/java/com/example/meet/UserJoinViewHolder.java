package com.example.meet;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class UserJoinViewHolder extends RecyclerView.ViewHolder {
    Context context;
    List<String> names;
    UserJoinAdapter adapter;

    TextView nameTextView;

    public UserJoinViewHolder(@NonNull View itemView,Context context, List<String> names, UserJoinAdapter adapter)
    {
        super(itemView);
        this.context = context;
        this.names = names;
        this.adapter = adapter;

        nameTextView = itemView.findViewById(R.id.user_join_name);
    }

}
