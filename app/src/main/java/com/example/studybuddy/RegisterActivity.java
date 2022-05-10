package com.example.studybuddy;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText editTextName, editTextEmailAddress, editTextPassword;
    private TextView banner, registerUser;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mAuth = FirebaseAuth.getInstance();

        banner = findViewById(R.id.bannerview);
        banner.setOnClickListener((View.OnClickListener) this);

        registerUser = findViewById(R.id.registerButton);
        registerUser.setOnClickListener((View.OnClickListener) this);

        editTextName = findViewById(R.id.editTextTextName);
        editTextEmailAddress = findViewById(R.id.editTextTextEmailAddress);
        editTextPassword = findViewById(R.id.editTextTextPassword);

        mAuth = FirebaseAuth.getInstance();
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bannerview:
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.registerButton:
                registerUser();
        }
    }

    private void registerUser() {
        String name = editTextName.getText().toString();
        String email = editTextEmailAddress.getText().toString();
        String password = editTextPassword.getText().toString();
//empty fields message
        if (name.isEmpty()) {
            editTextName.setError("Please enter first name");
            editTextName.requestFocus();
            return;
        }
        if (email.isEmpty()) {
            editTextEmailAddress.setError("Please enter email address");
            editTextEmailAddress.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            editTextPassword.setError("Please enter password");
            editTextPassword.requestFocus();
            return;
        }
        if (password.length() < 6) {
            editTextPassword.setError("Password must have at least 6 characters");
            editTextPassword.requestFocus();
            return;
        }
        //invalid email address message
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmailAddress.setError("Please provide valid email");
            editTextEmailAddress.requestFocus();
            return;
        }
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {


                        if (task.isSuccessful()) {
                            User user = new User(name, email);

                            FirebaseDatabase.getInstance().getReference("Users").child
                                    (Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue(user)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(RegisterActivity.this, "user successfully registered", Toast.LENGTH_LONG).show();
                                                //direct to login
                                                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                            } else {
                                                Toast.makeText(RegisterActivity.this, "registration failed", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                        }
                    }
                });
    }
}