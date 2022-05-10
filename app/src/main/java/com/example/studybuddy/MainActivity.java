package com.example.studybuddy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button login;
    private TextView register, forgotPassword;
    private EditText editTextEmailAddress, editTextPassword;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        register = findViewById(R.id.textView3);
        register.setOnClickListener(this);

        login = findViewById(R.id.loginButton);
        login.setOnClickListener(this);

        forgotPassword = findViewById(R.id.forgotPasswordbtn);
        forgotPassword.setOnClickListener(this);

        editTextEmailAddress = findViewById(R.id.editTextTextEmailAddress);
        editTextPassword = findViewById(R.id.editTextTextPassword);
    }


    @Override
    public void onClick(View view) { //register here
        switch (view.getId()) {
            case R.id.textView3:
                startActivity(new Intent(MainActivity.this, RegisterActivity.class));
                break;
            case R.id.loginButton:
                loginUser();
                break;
            case R.id.forgotPasswordbtn:
                startActivity(new Intent(this, forgotPassword.class));
            }
        }
//LOGIN PROCESS
    private void loginUser() {
        String email = editTextEmailAddress.getText().toString();
        String password = editTextPassword.getText().toString();
        //error messages for empty fields
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
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                editTextEmailAddress.setError("Please provide valid email");
                editTextEmailAddress.requestFocus();
                return;
            }
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (user.isEmailVerified()){
                            startActivity(new Intent(MainActivity.this, HomeActivity.class));
                        }
                        else {
                            user.sendEmailVerification();
                            Toast.makeText(MainActivity.this, "Check your email to verify your account(check spam!)", Toast.LENGTH_LONG).show();
                        }
                        //intent direct to home/user profile
                    }
                    else {
                        Toast.makeText(MainActivity.this, "Failed to login", Toast.LENGTH_LONG).show();
                    }
                }
            });
    }
}

