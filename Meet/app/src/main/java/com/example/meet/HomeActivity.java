package com.example.meet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class HomeActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{

    private BottomNavigationView bottomNavigationView;
    private FirebaseDatabase database;
    private FirebaseAuth auth;
    private MaterialToolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        findViews();
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout,new HomeFragment()).commit();
        toolbar.setOnMenuItemClickListener(this::toolbarClick);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
    }

    private boolean toolbarClick(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.menu_logout:
                finish();
                break;
            case R.id.menu_leaderboard:
                Intent intent = new Intent(HomeActivity.this,LeaderboardActivity.class);
                startActivity(intent);
                break;
        }
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        switch (item.getItemId())
        {
            case R.id.menu_home:
                fragment = new HomeFragment();
                break;
            case R.id.menu_map:
                Intent intent = new Intent(HomeActivity.this,MapsActivity.class);
                startActivity(intent);
                break;
            case R.id.menu_profile:
                fragment = new ProfileFragment();
                break;
        }
        if(fragment != null)
        {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout,fragment).commit();
        }
        return  true;
    }

    private void findViews()
    {
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        toolbar = findViewById(R.id.toolbar);
    }
}