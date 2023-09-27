package com.example.meet;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileFragment extends Fragment {

    private TextView name;
    private TextView email;
    private TextView phone;
    private TextView points;
    private CircleImageView profileIMG;

    public ProfileFragment() {
        // Required empty public constructor
    }


    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        email = view.findViewById(R.id.email_display);
        name = view.findViewById(R.id.name_display);
        phone = view.findViewById(R.id.phone_display);
        points = view.findViewById(R.id.point_display);
        profileIMG = view.findViewById(R.id.profile_image);

        email.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail().toString());
        name.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("PhoneNumbers").child(FirebaseAuth.getInstance().getCurrentUser().getDisplayName())
                .get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if(task.isSuccessful())
                        {
                            phone.setText(task.getResult().getValue(String.class));
                        }
                    }
                });

        reference.child("Points").child(FirebaseAuth.getInstance().getCurrentUser().getDisplayName())
                .get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if(task.isSuccessful())
                        {
                            if(task.getResult().getValue(Long.class)!= null)
                            {
                                Long pointsLong = task.getResult().getValue(Long.class);
                                points.setText(Long.toString(pointsLong));
                            }
                            else{
                                points.setText(Integer.toString(0));
                            }
                        }
                    }
                });

        StorageReference storage = FirebaseStorage.getInstance().getReference();
        storage.child("UserPhotos").child(FirebaseAuth.getInstance().getCurrentUser().getDisplayName())
                .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        if(getActivity() == null)
                        {
                            return;
                        }
                        Glide.with(ProfileFragment.this)
                                .load(uri)
                                .error(R.drawable.ic_launcher_background)
                                .circleCrop()
                                .into(profileIMG);
                    }
                });
        return view;
    }
}