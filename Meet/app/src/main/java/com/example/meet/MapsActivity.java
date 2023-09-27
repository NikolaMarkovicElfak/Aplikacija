package com.example.meet;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.media.metrics.Event;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.meet.databinding.ActivityMapsBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.slider.RangeSlider;
import com.google.android.material.slider.Slider;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,GoogleMap.OnMarkerClickListener,GoogleMap.OnMarkerDragListener {
    private static final String KEY = "AIzaSyBwCrbHCcoJx4TrMWE4AuAYiCu-uxIhyoE"; //Api key

    private static final String FINE_LOCATION = android.Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private boolean locationPermissionGranted = false; //Flag za dozvolu koriscenja lokacije
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1; // request code za lokaciju
    private EditText search; //search polje
    private ImageView gps; // gps dugme
    private ImageView createMarker; // dugme za dodavanje markera
    private ImageView eventsButton; // pribavljanje svih eventa
    private ImageView filterButton; // filter dugme
    private String address; //adresa u pretrazi
    private double currentLat = 0; //drzi trenutni Latitude koji se koristi
    private double currentLong = 0; //drzi trenutni Longitude koji se koristi
    private GoogleMap mMap; // mapa
    private ActivityMapsBinding binding;
    private FusedLocationProviderClient fusedLocationProviderClient; //client za obradu lokacije uredjaja
    private int Hour;
    private int Minute;
    private int Year,Month,Day;
    String type = ""; //tip dogadjaja
    int maxRange; //range za filter
    private ActivityResultLauncher<Intent> startAutocomplete = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent intent = result.getData();
                    if (intent != null) {
                        Place place = Autocomplete.getPlaceFromIntent(intent);
                        address = place.getAddress();
                        search.setText(address);
                        this.currentLat = place.getLatLng().latitude;
                        this.currentLong = place.getLatLng().longitude;
                        moveCamera(place.getLatLng(),15f,place.getName(),place.getId());
                    }
                } else if (result.getResultCode() == Activity.RESULT_CANCELED) {
                    // The user canceled the operation.

                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        getLocationPermission(); // zahteva dozvolu za lokaciju

        fusedLocationProviderClient = (FusedLocationProviderClient) LocationServices.getFusedLocationProviderClient(this);
        Places.initialize(getApplicationContext(),KEY);
        PlacesClient placesClient = Places.createClient(this);
        findViews();
        initListeners();
        hideSoftKeyboard();
    }

    private void findViews()
    {
        gps = findViewById(R.id.gps);
        search = findViewById(R.id.inputSearch);
        createMarker = findViewById(R.id.markerButton);
        eventsButton = findViewById(R.id.load_events);
        filterButton = findViewById(R.id.filter_button);
    }

    private void initListeners()
    {
        gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDeviceLocation();
            }
        });

        search.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                startAutocomplete();
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAutocomplete();
            }
        });

        createMarker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.clear();
                getDeviceLocation();
                MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(currentLat,currentLong)).draggable(true);
                mMap.addMarker(markerOptions);
                Toast.makeText(MapsActivity.this, "drag the marker on the map", Toast.LENGTH_SHORT).show();
            }
        });

        eventsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.clear();
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                reference.child("Events").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if(task.isSuccessful())
                        {
                            for(DataSnapshot snapshot : task.getResult().getChildren())
                            {
                                EventModel event = snapshot.getValue(EventModel.class);
                                Marker mark = mMap.addMarker(new MarkerOptions().position(new LatLng(event.getLat(),event.getLng())));
                                mark.setTag(snapshot.getKey());
                                BitmapDrawable bitmapDrawable = null;
                                if(event.getType().equals("Sports"))
                                {
                                    bitmapDrawable = (BitmapDrawable) getResources().getDrawable(R.drawable.sports);
                                }
                                else if(event.getType().equals("Coffee"))
                                {
                                    bitmapDrawable = (BitmapDrawable) getResources().getDrawable(R.drawable.coffee);
                                }
                                else{
                                    bitmapDrawable = (BitmapDrawable) getResources().getDrawable(R.drawable.party);
                                }
                                Bitmap b = bitmapDrawable.getBitmap();
                                Bitmap markerIcon = Bitmap.createScaledBitmap(b,100,100,false);
                                mark.setIcon(BitmapDescriptorFactory.fromBitmap(markerIcon));
                            }
                        }
                    }
                });
            }
        });

        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(MapsActivity.this);
                dialog.setCancelable(true);
                dialog.setContentView(R.layout.filter_dialog);
                dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

                Slider slider = dialog.findViewById(R.id.range_picker);
                TextView display = dialog.findViewById(R.id.range_display);

                CheckBox coffee = dialog.findViewById(R.id.coffee_checkbox);
                CheckBox sports = dialog.findViewById(R.id.sports_checkbox);
                CheckBox party = dialog.findViewById(R.id.party_checkbox);

                slider.addOnChangeListener(new Slider.OnChangeListener() {
                    @Override
                    public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                        int val = (int) value;
                        display.setText(Float.toString(value) + "m");
                        maxRange = val;
                    }
                });

                MaterialButton filterButton = dialog.findViewById(R.id.filter_button);
                filterButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                         mMap.clear();
                         DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                         reference.child("Events").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                             @Override
                             public void onComplete(@NonNull Task<DataSnapshot> task) {
                                 for(DataSnapshot snapshot : task.getResult().getChildren())
                                 {
                                     EventModel eventModel = snapshot.getValue(EventModel.class);
                                     if(eventModel.getType().equals("Coffee") && !coffee.isChecked())
                                     {
                                         continue;
                                     }
                                     if(eventModel.getType().equals("Sports") && !sports.isChecked())
                                     {
                                         continue;
                                     }
                                     if(eventModel.getType().equals("Party") && !party.isChecked())
                                     {
                                         continue;
                                     }

                                     Toast.makeText(MapsActivity.this, Integer.toString(maxRange), Toast.LENGTH_SHORT).show();
                                     LatLng markerPosition = new LatLng(eventModel.getLat(),eventModel.getLng());
                                     getDeviceLocation();
                                     LatLng devicePosition = new LatLng(currentLat,currentLong);
                                     double distance = calculateDistance(devicePosition,markerPosition);
                                     if(distance > maxRange)
                                     {
                                         continue;
                                     }

                                     Marker mark = mMap.addMarker(new MarkerOptions().position(markerPosition));
                                     mark.setTag(snapshot.getKey());
                                     BitmapDrawable bitmapDrawable = null;
                                     if(eventModel.getType().equals("Sports"))
                                     {
                                         bitmapDrawable = (BitmapDrawable) getResources().getDrawable(R.drawable.sports);
                                     }
                                     else if(eventModel.getType().equals("Coffee"))
                                     {
                                         bitmapDrawable = (BitmapDrawable) getResources().getDrawable(R.drawable.coffee);
                                     }
                                     else{
                                         bitmapDrawable = (BitmapDrawable) getResources().getDrawable(R.drawable.party);
                                     }
                                     Bitmap b = bitmapDrawable.getBitmap();
                                     Bitmap markerIcon = Bitmap.createScaledBitmap(b,100,100,false);
                                     mark.setIcon(BitmapDescriptorFactory.fromBitmap(markerIcon));
                                 }
                                 dialog.dismiss();
                             }
                         });
                    }
                });

                dialog.show();
            }
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (locationPermissionGranted) {
            getDeviceLocation();

            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.setOnMarkerClickListener(this);
            mMap.setOnMarkerDragListener(this);
        }

    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void moveCamera(LatLng latLng,float zoom,String title,String snippet)
    {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom));
        if(!title.equals("My Location"))
        {
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(latLng)
                    .title(title)
                    .snippet(snippet);
            mMap.addMarker(markerOptions);
        }
        hideSoftKeyboard();
    }

    private void getDeviceLocation()
    {
        try{
            Task location = fusedLocationProviderClient.getLastLocation();
            location.addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful())
                    {
                        Location currentLocation = (Location) task.getResult();
                        currentLat = currentLocation.getLatitude();
                        currentLong = currentLocation.getLongitude();
                        moveCamera(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude()),
                                15f
                                ,"My Location","");
                    }
                }
            });
        }
        catch (SecurityException e)
        {
            Log.d("EXCEPTION :",e.getMessage());
        }
    }

    private void startAutocomplete()
    {
        // Set the fields to specify which types of place data to
        // return after the user has made a selection.
        List<Place.Field> fields = Arrays.asList(Place.Field.ID,Place.Field.ADDRESS, Place.Field.NAME,Place.Field.LAT_LNG);
        // Start the autocomplete intent.
        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                .build(this);
        startAutocomplete.launch(intent);
    }

    private void hideSoftKeyboard()
    {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        if(marker.getTag() == null)
        {
            Dialog dialog = new Dialog(this);
            dialog.setTitle("Create Event");
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.add_event_dialog);
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

            TextInputEditText eventName = dialog.findViewById(R.id.event_name_input);
            MaterialButton pickTimeBtn = dialog.findViewById(R.id.pick_time_button);
            MaterialButton pickDateBtn = dialog.findViewById(R.id.pick_date_button);
            MaterialButton createEventBtn = dialog.findViewById(R.id.create_event_button);
            ImageView coffee = dialog.findViewById(R.id.coffee_event);
            ImageView sports = dialog.findViewById(R.id.sports_event);
            ImageView party = dialog.findViewById(R.id.party_event);
            TextView timeDisplay = dialog.findViewById(R.id.time_display);
            TextView dateDisplay = dialog.findViewById(R.id.date_display);


            pickTimeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            Hour = hourOfDay;
                            Minute = minute;
                            timeDisplay.setText(Integer.toString(Hour) + " : " + Integer.toString(Minute));
                        }
                    };

                    TimePickerDialog timePickerDialog = new TimePickerDialog(MapsActivity.this,onTimeSetListener,Hour,Minute,true);
                    timePickerDialog.setTitle("Select time");
                    timePickerDialog.show();
                }
            });

            pickDateBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            Month = month + 1;
                            dateDisplay.setText(dayOfMonth+"/"+Month+"/"+year);
                        }
                    };
                    Calendar calendar = Calendar.getInstance();
                    int year = calendar.get(Calendar.YEAR);
                    int month = calendar.get(Calendar.MONTH);
                    int day = calendar.get(Calendar.DAY_OF_MONTH);

                    DatePickerDialog datePickerDialog = new DatePickerDialog(MapsActivity.this,dateSetListener,year,month,day);
                    datePickerDialog.show();
                }

            });


            coffee.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    coffee.setBackgroundColor(getResources().getColor(com.google.android.libraries.places.R.color.quantum_yellow));
                    sports.setBackgroundColor(Color.parseColor("#00000000"));
                    party.setBackgroundColor(Color.parseColor("#00000000"));
                    type = "Coffee";
                }
            });

            sports.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    coffee.setBackgroundColor(Color.parseColor("#00000000"));
                    sports.setBackgroundColor(getResources().getColor(com.google.android.libraries.places.R.color.quantum_yellow));
                    party.setBackgroundColor(Color.parseColor("#00000000"));
                    type = "Sports";
                }
            });

            party.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    coffee.setBackgroundColor(Color.parseColor("#00000000"));
                    sports.setBackgroundColor(Color.parseColor("#00000000"));
                    party.setBackgroundColor(getResources().getColor(com.google.android.libraries.places.R.color.quantum_yellow));
                    type = "Party";
                }
            });

            createEventBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(eventName.getText().toString().trim().length() == 0)
                    {
                        Toast.makeText(MapsActivity.this, "Enter event name", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(type.equals(""))
                    {
                        Toast.makeText(MapsActivity.this, "Select type of event", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(dateDisplay.getText().toString().trim().equals("/"))
                    {
                        Toast.makeText(MapsActivity.this, "Enter date of event", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(timeDisplay.getText().toString().trim().equals("/"))
                    {
                        Toast.makeText(MapsActivity.this, "Enter time of event", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String name = eventName.getText().toString();
                    String date = dateDisplay.getText().toString();
                    String time = timeDisplay.getText().toString();
                    EventModel newEvent = new EventModel(name,time,date,type,currentLat,currentLong);
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                    String key = reference.child(type).push().getKey();
                    reference.child("Events").child(key).setValue(newEvent);
                    type = "";
                    Toast.makeText(MapsActivity.this, "Event created successfully", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            });

            dialog.show();
        }
        else{
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            String key = (String) marker.getTag();
            Dialog dialog = new Dialog(this);
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.event_info_dialog);
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

            TextView dateTime = dialog.findViewById(R.id.event_info_datetime);
            TextView eventName = dialog.findViewById(R.id.event_info_name);
            ImageView photo = dialog.findViewById(R.id.event_info_photo);
            reference.child("Events").child(key).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    EventModel eventModel = task.getResult().getValue(EventModel.class);
                    dateTime.setText(eventModel.getDate() + " at " + eventModel.getTime());
                    eventName.setText(eventModel.getEventName());
                    if(eventModel.getType().equals("Sports"))
                    {
                        photo.setBackgroundResource(R.drawable.sports);
                    }
                    if(eventModel.getType().equals("Coffee"))
                    {
                        photo.setBackgroundResource(R.drawable.coffee);
                    }
                }
            });
            MaterialButton joinButton = dialog.findViewById(R.id.join_button);
            RecyclerView recyclerView = dialog.findViewById(R.id.event_recycler);
            List<String> names = new ArrayList<>();
            UserJoinAdapter adapter = new UserJoinAdapter(MapsActivity.this,names);


            reference.child("UserJoins").child(key).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if(task.isSuccessful())
                    {
                        int position = 0;
                        for(DataSnapshot snapshot : task.getResult().getChildren())
                        {
                            UserJoin join = snapshot.getValue(UserJoin.class);
                            if(join.getEventKey().equals(key))
                            {
                                names.add(join.getUserName());
                                adapter.notifyItemInserted(position);
                                position++;
                            }
                        }
                    }
                }
            });

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setAdapter(adapter);

            joinButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UserJoin newJoin = new UserJoin(FirebaseAuth.getInstance().getCurrentUser().getDisplayName(),key);
                    reference.child("UserJoins").child(key).child(newJoin.getUserName()).setValue(newJoin);
                    //POINTS
                    reference.child("Points").child(FirebaseAuth.getInstance().getCurrentUser().getDisplayName()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if(task.isSuccessful())
                            {
                                if(task.getResult().getValue(Integer.class)!= null)
                                {
                                    int currentPoints = task.getResult().getValue(Integer.class);
                                    currentPoints+= 10;
                                    reference.child("Points").child(FirebaseAuth.getInstance().getCurrentUser().getDisplayName())
                                            .setValue(currentPoints);
                                }
                                else{
                                    reference.child("Points").child(FirebaseAuth.getInstance().getCurrentUser().getDisplayName())
                                            .setValue(10);
                                }
                            }

                        }
                    });
                    dialog.dismiss();
                    Toast.makeText(MapsActivity.this, "Joined", Toast.LENGTH_SHORT).show();
                }
            });
            dialog.show();
        }
        return true;
    }


    @Override
    public void onMarkerDrag(@NonNull Marker marker) {
        currentLat = marker.getPosition().latitude;
        currentLong = marker.getPosition().longitude;
    }

    @Override
    public void onMarkerDragEnd(@NonNull Marker marker) {
        currentLat = marker.getPosition().latitude;
        currentLong = marker.getPosition().longitude;
    }

    @Override
    public void onMarkerDragStart(@NonNull Marker marker) {
        currentLat = marker.getPosition().latitude;
        currentLong = marker.getPosition().longitude;
    }

    private double calculateDistance(LatLng currLocation, LatLng otherLocation) {
        double currLatRad = Math.toRadians(currLocation.latitude);
        double currLonRad = Math.toRadians(currLocation.longitude);
        double otherLatRad = Math.toRadians(otherLocation.latitude);
        double otherLonRad = Math.toRadians(otherLocation.longitude);

        final double earthRadius = 6371000;

        double dLat = otherLatRad - currLatRad;
        double dLon = otherLonRad - currLonRad;
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(currLatRad) * Math.cos(otherLatRad) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return earthRadius * c;
    }
}