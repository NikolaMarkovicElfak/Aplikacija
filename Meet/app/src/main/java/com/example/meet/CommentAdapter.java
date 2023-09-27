package com.example.meet;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentViewHolder> {
    Context context;
    List<String> comments;

    public CommentAdapter(Context context,List<String> comments)
    {
        this.comments = comments;
        this.context = context;
    }


    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.comment_layout,parent,false);
        return new CommentViewHolder(view,context,comments,this);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        holder.commentText.setText(comments.get(position));
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }
}
