package com.example.meet;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class FeedViewHolder extends RecyclerView.ViewHolder {
    Context context;
    List<String> names;
    List<String> dateTimes;

    List<String> eventKeys;

    List<String> types;
    FeedAdapter adapter;

    ImageView typePhoto;
    TextView eventName;
    TextView eventTimeDate;
    Button addComment;
    RecyclerView recyclerView;

    public FeedViewHolder(@NonNull View itemView,Context context,List<String> names,List<String> dateTimes,List<String> types,List<String> eventKeys,FeedAdapter adapter) {
        super(itemView);
        this.context = context;
        this.names = names;
        this.dateTimes = dateTimes;
        this.adapter = adapter;
        this.types = types;
        this.eventKeys = eventKeys;

        typePhoto = itemView.findViewById(R.id.feed_type_photo);
        eventName = itemView.findViewById(R.id.feed_event_name);
        eventTimeDate = itemView.findViewById(R.id.feed_event_dateTime);
        addComment = itemView.findViewById(R.id.add_comment);
        recyclerView = itemView.findViewById(R.id.comments_recycler);

    }
}
