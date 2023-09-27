package com.example.meet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {
    private MaterialButton SignupButton; // dugme koje vodi na activity za registraciju
    private MaterialButton LoginButton; // dugme za login
    private TextInputEditText emailTextEdit; // email polje
    private TextInputEditText passwordTextEdit; // password polje

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SignupButton = findViewById(R.id.signup_button);
        SignupButton.setOnClickListener(this::signupClicked);
        LoginButton = findViewById(R.id.login_button);
        LoginButton.setOnClickListener(this::loginClicked);
        emailTextEdit = findViewById(R.id.email_login);
        passwordTextEdit = findViewById(R.id.password_login);
    }

    private void signupClicked(View view) // novi activity za registraciju korisnika
    {
        Intent intent = new Intent(LoginActivity.this,SignupActivity.class);
        startActivity(intent);
    }

    /*
    Provera da li su polja prazna
    Provera naloga na Firebase Auth
    Ako su podaci okej prosledjuje se na home page ukoliko ne prijavljuje gresku
     */
    private void loginClicked(View view)
    {
        if(emailTextEdit.getText().toString().trim().length() == 0)
        {
            Toast.makeText(this, "Please provide Email", Toast.LENGTH_SHORT).show();
            return;
        }
        if(passwordTextEdit.getText().toString().trim().length() == 0)
        {
            Toast.makeText(this, "Please provide Password", Toast.LENGTH_SHORT).show();
            return;
        }
        String email = emailTextEdit.getText().toString();
        String password = passwordTextEdit.getText().toString();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    FirebaseUser user = auth.getCurrentUser();
                    Toast.makeText(LoginActivity.this, "Welcome " + user.getDisplayName(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this,HomeActivity.class);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}