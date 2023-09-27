package com.example.meet;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class UserJoinAdapter extends RecyclerView.Adapter<UserJoinViewHolder> {
    Context context;
    List<String> names;

    public UserJoinAdapter(Context context,List<String> names)
    {
        this.context = context;
        this.names = names;
    }

    @NonNull
    @Override
    public UserJoinViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_user_card,parent,false);
        return new UserJoinViewHolder(view,context,names,this);
    }

    @Override
    public void onBindViewHolder(@NonNull UserJoinViewHolder holder, int position) {
        holder.nameTextView.setText(names.get(position));
    }

    @Override
    public int getItemCount() {
        return names.size();
    }
}
