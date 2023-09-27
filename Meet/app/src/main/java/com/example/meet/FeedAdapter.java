package com.example.meet;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class FeedAdapter extends RecyclerView.Adapter<FeedViewHolder> {
    Context context;
    List<String> names;
    List<String> dateTimes;
    List<String> eventKeys;

    List<String> types;

    public FeedAdapter(Context context,List<String> names,List<String> dateTimes,List<String> types,List<String> eventKeys)
    {
        this.context = context;
        this.names = names;
        this.dateTimes = dateTimes;
        this.types = types;
        this.eventKeys = eventKeys;
    }

    @NonNull
    @Override
    public FeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.feed_card,parent,false);
        return new FeedViewHolder(view,context,names,dateTimes,types,eventKeys,this);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedViewHolder holder, int position) {
        int pos = holder.getAdapterPosition();
        String type = types.get(pos);
        if(type.equals("Sports"))
        {
            holder.typePhoto.setBackgroundResource(R.drawable.sports);
        }
        if(type.equals("Coffee"))
        {
            holder.typePhoto.setBackgroundResource(R.drawable.coffee);
        }

        holder.eventName.setText(names.get(pos));
        holder.eventTimeDate.setText(dateTimes.get(pos));

        holder.addComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(context);
                dialog.setCancelable(true);
                dialog.setContentView(R.layout.add_comment_layout);
                dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

                EditText comment = dialog.findViewById(R.id.comment_input);
                Button addComment = dialog.findViewById(R.id.add_comment);

                addComment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(comment.getText().toString().trim().length() == 0)
                        {
                            Toast.makeText(context, "Enter a comment", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                        reference.child("Comments")
                                .child(eventKeys.get(pos))
                                .child(FirebaseAuth.getInstance().getCurrentUser().getDisplayName())
                                .setValue(comment.getText().toString());

                        reference.child("Points").child(FirebaseAuth.getInstance().getCurrentUser().getDisplayName())
                                        .get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                                        if(task.isSuccessful())
                                        {
                                            if(task.getResult().getValue(Integer.class)!=null)
                                            {
                                                int currentValue = task.getResult().getValue(Integer.class);
                                                currentValue+=10;
                                                reference.child("Points").child(FirebaseAuth.getInstance().getCurrentUser().getDisplayName())
                                                        .setValue(currentValue);
                                            }
                                            else{
                                                reference.child("Points").child(FirebaseAuth.getInstance().getCurrentUser().getDisplayName())
                                                        .setValue(10);
                                            }

                                        }

                                    }
                                });
                        dialog.dismiss();
                        Toast.makeText(context, "Comment added", Toast.LENGTH_SHORT).show();
                    }
                });
                dialog.show();
            }
        });

        List<String> comments = new ArrayList<>();
        CommentAdapter adapter = new CommentAdapter(context,comments);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        holder.recyclerView.setLayoutManager(new LinearLayoutManager(context));
        holder.recyclerView.setAdapter(adapter);

        reference.child("Comments").child(eventKeys.get(pos)).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful())
                {
                    int adapterPosition = 0;
                    for(DataSnapshot snapshot : task.getResult().getChildren())
                    {
                        comments.add(snapshot.getValue(String.class));
                        adapter.notifyItemInserted(adapterPosition++);
                    }
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return names.size();
    }
}
