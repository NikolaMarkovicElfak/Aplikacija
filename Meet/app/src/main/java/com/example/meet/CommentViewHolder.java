package com.example.meet;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CommentViewHolder extends RecyclerView.ViewHolder {
    Context context;
    List<String> comments;
    CommentAdapter adapter;

    TextView commentText;


    public CommentViewHolder(@NonNull View itemView,Context context,List<String> comments,CommentAdapter adapter) {
        super(itemView);
        this.comments = comments;
        this.context = context;
        this.adapter = adapter;

        commentText = itemView.findViewById(R.id.comment_text);
    }
}
