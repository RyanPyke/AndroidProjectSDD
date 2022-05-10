package com.example.studybuddy;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

public class HomeActivity extends AppCompatActivity {
    //declare variables
    private EditText editTextInput;
    private TextView textViewCountdown;
    private Button buttonSetTime;
    private Button buttonStartPause;
    private Button buttonReset;

    private CountDownTimer countDownTimer;
    private boolean timerRunning;
    private  long startTimeInMs;
    private long timeLeftInMs = startTimeInMs;
    private long endTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        //assign variables to xml elements
        editTextInput = findViewById(R.id.edit_timer_input);
        buttonSetTime = findViewById(R.id.set_timer_button);
        textViewCountdown = findViewById(R.id.timer_textview);
        buttonStartPause = findViewById(R.id.start_btn);
        buttonReset = findViewById(R.id.reset_btn);

        //when buttons/editText pressed, what to do

        buttonSetTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String input = editTextInput.getText().toString();
                if (input.length()==0) {
                    Toast.makeText(HomeActivity.this, "Cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                long msInput = Long.parseLong(input) * 60000;
                if (msInput == 0) {
                    Toast.makeText(HomeActivity.this, "Enter a positive number", Toast.LENGTH_SHORT).show();
                    return;
                }

                setTime(msInput);
                editTextInput.setText("");
            }
        });
        buttonStartPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (timerRunning) {
                    pauseTimer();
                } else {
                    startTimer();
                }
            }
        });
        buttonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetTimer();
            }
        });
        updateCountDownText();
    }

    private void setTime(Long milliseconds) {
        startTimeInMs = milliseconds;
        resetTimer();
    }
        private void startTimer(){
            endTime = System.currentTimeMillis() + timeLeftInMs;
        countDownTimer = new CountDownTimer(timeLeftInMs, 1000) {
            @Override
            public void onTick(long msUntilFinished) {
                timeLeftInMs = msUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                timerRunning = false;
                buttonStartPause.setText("Start");
                buttonStartPause.setVisibility(View.INVISIBLE);
                buttonReset.setVisibility(View.VISIBLE);


            }
        }
        .start();
        timerRunning = true;
        updateInterface();


    }
    private void pauseTimer(){
        countDownTimer.cancel();
        timerRunning = false;
        updateInterface();
        }
        private void resetTimer(){
            timeLeftInMs = startTimeInMs;
            updateCountDownText();
            updateInterface();

        }
   private  void updateCountDownText(){
            int minutes = (int) timeLeftInMs/1000/60;
            int seconds = (int) timeLeftInMs/1000 % 60;

            String timeLeftFormatted = String.format("%02d:%02d", minutes,seconds);
            textViewCountdown.setText(timeLeftFormatted);
        }

        //set visibility
    private void updateInterface() {
        if (timerRunning) {
            editTextInput.setVisibility(View.INVISIBLE);
            buttonSetTime.setVisibility(View.INVISIBLE);
            buttonReset.setVisibility(View.INVISIBLE);
            buttonStartPause.setText("Pause");
        } else {
            editTextInput.setVisibility(View.VISIBLE);
            buttonSetTime.setVisibility(View.VISIBLE);
            buttonStartPause.setText("Start");
            if (timeLeftInMs < 1000) {
                buttonStartPause.setVisibility(View.INVISIBLE);
            } else {
                buttonStartPause.setVisibility(View.VISIBLE);
            }
            if (timeLeftInMs < startTimeInMs) {
                buttonReset.setVisibility(View.VISIBLE);
            } else {
                buttonReset.setVisibility(View.INVISIBLE);
            }
        }
    }
    //set up for recovering timer if app closes
    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong("startTimeInMillis", startTimeInMs);
        editor.putLong("millisLeft", timeLeftInMs);
        editor.putBoolean("timerRunning", timerRunning);
        editor.putLong("endTime", endTime);
        editor.apply();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
    //recover timer by comparing to system time
    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        startTimeInMs = prefs.getLong("startTimeInMillis", 600000);
        timeLeftInMs = prefs.getLong("millisLeft", startTimeInMs);
        timerRunning = prefs.getBoolean("timerRunning", false);
        updateCountDownText();
        updateInterface();
        if (timerRunning) {
            endTime = prefs.getLong("endTime", 0);
            timeLeftInMs = endTime - System.currentTimeMillis();
            if (timeLeftInMs < 0) {
                timeLeftInMs = 0;
                timerRunning = false;
                updateCountDownText();
                updateInterface();
            } else {
                startTimer();
            }
}
    }
}