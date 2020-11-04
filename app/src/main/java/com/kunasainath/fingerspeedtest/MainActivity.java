package com.kunasainath.fingerspeedtest;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TextView txtTimer, txtTaps;
    private Button btnTap;

    private final int TIMER_START_TIME = 60;
    private final int TIMER_INTERVAL = 1000;
    private final int TAPS_START = 1000;
    private int tapsStart = TAPS_START;

    private boolean landscape = true;

    private CountDownTimer mainCountDownTimer;

    private final String WON = "Congratulations! You have won...";
    private final String LOST = "Oops! You have lost by running out of time...";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtTimer = findViewById(R.id.txt_timer);
        txtTaps = findViewById(R.id.txt_taps);
        btnTap = findViewById(R.id.btn_tap);


        txtTimer.setText(Integer.toString(TIMER_START_TIME));
        txtTaps.setText(Integer.toString(tapsStart));

        ViewGroup.LayoutParams btnLayoutParams = btnTap.getLayoutParams();


        if(savedInstanceState != null){

            landscape = savedInstanceState.getBoolean("LANDSCAPE");
            int currentTime = savedInstanceState.getInt("TIME");
            String currentTaps = Integer.toString(savedInstanceState.getInt("TAPS"));
            txtTaps.setText(currentTaps);
            tapsStart = Integer.parseInt(currentTaps);

            if(landscape){
                landscape = false;
                txtTimer.setTextSize(50);
                txtTaps.setTextSize(30);
                btnLayoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                btnTap.setLayoutParams(btnLayoutParams);
                btnTap.setPadding(30,30,30,30);
            }else{
                landscape = true;
                txtTimer.setTextSize(200);
                txtTaps.setTextSize(100);
                btnLayoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                btnTap.setLayoutParams(btnLayoutParams);
            }


            mainCountDownTimer = new CountDownTimer(currentTime*1000, TIMER_INTERVAL) {
                @Override
                public void onTick(long currentTime) {
                    txtTimer.setText(Integer.toString((int) currentTime/1000));
                }

                @Override
                public void onFinish() {
                    showAlert(LOST);
                }
            };
            mainCountDownTimer.start();

            if(tapsStart <= 0){
                mainCountDownTimer.cancel();
                showAlert(WON);
            }
        }

        if(savedInstanceState == null) {
            mainCountDownTimer = new CountDownTimer(TIMER_START_TIME * 1000, TIMER_INTERVAL) {
                @Override
                public void onTick(long currentTime) {
                    txtTimer.setText(Integer.toString((int) currentTime / 1000));
                }

                @Override
                public void onFinish() {
                    showAlert(LOST);
                }
            };

            mainCountDownTimer.start();
        }

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(view.getId() == R.id.btn_tap){
                    tapsStart--;
                    txtTaps.setText(Integer.toString(tapsStart));
                    if(tapsStart <= 0){
                        showAlert(WON);
                        mainCountDownTimer.cancel();
                    }
                }
            }
        };

        btnTap.setOnClickListener(listener);

    }

    private void showAlert(String result){

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        String alertTitle = "";
        if(result.equals(WON)){
            alertTitle = new String(WON);
            alertDialog.setIcon(R.drawable.won);
            
        }else if(result.equals(LOST)){
            alertTitle = new String(LOST);
            alertDialog.setIcon(R.drawable.ic_baseline_timer_24);
        }

        alertDialog.setTitle(alertTitle);
        alertDialog.setMessage("Would you like to play again?");
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mainCountDownTimer.cancel();
                restoreTheGame();
            }
        });
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                showToast("Thanks for playing!", Toast.LENGTH_LONG);
                finish();
            }
        });
        alertDialog.show();
    }

    private void showToast(String message, int duration){
        Toast.makeText(this,message,duration).show();
    }

    private void restoreTheGame(){
        tapsStart = TAPS_START;
        txtTaps.setText(Integer.toString(tapsStart));
        mainCountDownTimer = new CountDownTimer(TIMER_START_TIME*1000,TIMER_INTERVAL) {
            @Override
            public void onTick(long currentTime) {
                txtTimer.setText(Long.toString(currentTime/1000));
            }

            @Override
            public void onFinish() {
                showAlert(LOST);
            }
        };
        mainCountDownTimer.start();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt("TAPS",tapsStart);
        outState.putInt("TIME",Integer.parseInt(txtTimer.getText().toString()));
        outState.putBoolean("LANDSCAPE",landscape);

        mainCountDownTimer.cancel();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.score){
            showToast("Your current score: " + tapsStart, Toast.LENGTH_LONG);
        }else if(item.getItemId() == R.id.time){
            showToast("Time left: " + txtTimer.getText().toString() + " seconds", Toast.LENGTH_LONG);
        }else if(item.getItemId() == R.id.version){
            showToast("App version: " + BuildConfig.VERSION_NAME, Toast.LENGTH_LONG);
        }
        return true;
    }
}