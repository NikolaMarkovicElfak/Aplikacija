package com.example.meet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;

//Klasa za registraciju novog korisnika
public class SignupActivity extends AppCompatActivity {

    public static final int IMAGE_GALLERY = 1; //int identifikator za poziv galerije pri prijemu rezultata aktivnosti
    public static final int IMAGE_CAMERA = 2; // int identifikator za poziv kamere pri prijemu rezultata aktivnosti

    private FirebaseDatabase database; //Firebase instanca
    private DatabaseReference reference; // Firebase referenca
    private FirebaseAuth auth; // Autentifikacija za kreiranje novog korisnika
    private StorageReference storageReference; // Firebase Storage reference

    private ImageView userPhoto; // slika koju korisnik unosi iz kamere/galerije
    private TextInputEditText emailTextInput; //email
    private TextInputEditText usernameTextInput; //username
    private TextInputEditText passwordTextInput; //password
    private TextInputEditText phoneTextInput; //phone
    private MaterialButton cameraButton; //button za pokretanje kamere
    private MaterialButton galleryButton; //button za pokretanje galerije
    private MaterialButton submitButton; //button za kreiranje novog naloga

    private Bitmap photo = null; //Ukoliko se koristi kamera drzimo bitmap
    private Uri photoURI = null; //Ukoliko se koristi galerija drzimo URI na sliku
    private int photoAdded = 0; // 0 - slika nije dodata , 1- dodata je iz galerije , 2-dodata je preko kamere


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        storageReference = FirebaseStorage.getInstance().getReference();
        auth = FirebaseAuth.getInstance();

        findViews(); // pronalazenje sa layouta
        setListeners(); // postavljanje onClick listenera
    }

    private void setListeners() //postavljamo sve funkcije listenere za buttone koje koristimo
    {
        galleryButton.setOnClickListener(this::galleryClick);
        cameraButton.setOnClickListener(this::cameraClick);
        submitButton.setOnClickListener(this::submitClick);
    }

    private void findViews() // pronalazimo sve elemente koje koristimo na tom layoutu
    {
        userPhoto = findViewById(R.id.user_image);
        emailTextInput = findViewById(R.id.signup_email);
        usernameTextInput = findViewById(R.id.signup_username);
        passwordTextInput = findViewById(R.id.signup_password);
        phoneTextInput = findViewById(R.id.signup_phone);
        cameraButton = findViewById(R.id.camera_button);
        galleryButton = findViewById(R.id.gallery_button);
        submitButton = findViewById(R.id.submit_button);
    }

    private void galleryClick(View view) // kreiramo Intent koji pali galeriju i pribavlja odabranu sliku
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), IMAGE_GALLERY);
    }

    //Kada se zavrsi aktivnost za kameru/galeriju rezultat u data postavljamo u photo ili photoURI
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == IMAGE_GALLERY) {
            Uri imageURI = data.getData();
            photoURI = imageURI;
            userPhoto.setImageURI(imageURI);
            photoAdded = 1;
            photo = null;
        }
        if(resultCode == RESULT_OK && requestCode == IMAGE_CAMERA)
        {
            photo = (Bitmap) data.getExtras().get("data");
            userPhoto.setImageBitmap(photo);
            photoAdded = 2;
            photoURI = null;
        }
    }

    // Nova aktivnost za pokretanje kamere
    private void cameraClick(View view)
    {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, IMAGE_CAMERA);
    }

    /*
    Kreiranje novog korisnika,provera da li su sva polja popunjena i da li je dodata slika sa kamere ili galerije
    Dodavanje novog korisnika uz pomoc email i sifre
    Dodavanje broja telefona u realtime database
    Ukoliko je slika iz galerije salje se uz URI a ukoliko iz kamere onda se u vidu Stream-a salje kao niz bajtova
    Email mora da bude formatiran kao email inace nece raditi i sifra mora da ima minimum 6 karaktera
    Greske se prikazuju na ekranu u vidu Toast poruke
     */
    private void submitClick(View view)
    {
        if(checkInfo())
        {
            String username = usernameTextInput.getText().toString();
            String email = emailTextInput.getText().toString();
            String password = passwordTextInput.getText().toString();
            String phone = phoneTextInput.getText().toString();

            auth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful())
                            {
                                reference.child("PhoneNumbers").child(username).setValue(phone);

                                FirebaseUser user = auth.getCurrentUser();
                                UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(username)
                                        .build();
                                user.updateProfile(request);

                                if(photoAdded == 1)
                                {
                                    storageReference.child("UserPhotos")
                                            .child(username)
                                            .putFile(photoURI);
                                }
                                if(photoAdded == 2)
                                {
                                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                    photo.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                    byte[] data = baos.toByteArray();
                                    storageReference.child("UserPhotos")
                                            .child(username)
                                            .putBytes(data);
                                }
                                Toast.makeText(SignupActivity.this, "Account Created", Toast.LENGTH_SHORT).show();
                                finish();
                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(SignupActivity.this,"Error :" +  e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    //Funkcija za proveru polja da li je neko od njih prazno
    private boolean checkInfo()
    {
        if(photoAdded == 0)
        {
            Toast.makeText(this, "Please provide photo for your profile", Toast.LENGTH_LONG).show();
            return false;
        }
        if(emailTextInput.getText().toString().trim().length() == 0)
        {
            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(passwordTextInput.getText().toString().trim().length() == 0)
        {
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(usernameTextInput.getText().toString().trim().length() == 0)
        {
            Toast.makeText(this, "Please enter username", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(phoneTextInput.getText().toString().trim().length() == 0)
        {
            Toast.makeText(this, "Please enter phone number", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}