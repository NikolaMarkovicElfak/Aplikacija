package com.example.meet;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {


    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_home, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.home_recycler);
        List<String> names = new ArrayList<>();
        List<String> dateTimes = new ArrayList<>();
        List<String> types = new ArrayList<>();
        List<String> eventsForUser = new ArrayList<>();
        FeedAdapter adapter = new FeedAdapter(getActivity(),names,dateTimes,types,eventsForUser);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();


        reference.child("UserJoins").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful())
                {
                    String user = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                    for(DataSnapshot snapshot : task.getResult().getChildren())
                    {
                        for(DataSnapshot snapshot1 : snapshot.getChildren())
                        {
                            UserJoin userJoin = snapshot1.getValue(UserJoin.class);
                            if(userJoin.getUserName().equals(user))
                            {
                                eventsForUser.add(userJoin.getEventKey());
                            }
                        }
                    }
                }
            }
        });

        reference.child("Events").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful())
                {
                    int position = 0;
                    for(DataSnapshot snapshot : task.getResult().getChildren())
                    {
                        if(eventsForUser.contains(snapshot.getKey()))
                        {
                            EventModel eventModel = snapshot.getValue(EventModel.class);
                            names.add(eventModel.getEventName());
                            dateTimes.add(eventModel.getDate() + " at " + eventModel.getTime());
                            types.add(eventModel.getType());
                            adapter.notifyItemInserted(position++);
                        }
                    }
                }
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this.requireContext()));
        recyclerView.setAdapter(adapter);

        return view;
    }
}