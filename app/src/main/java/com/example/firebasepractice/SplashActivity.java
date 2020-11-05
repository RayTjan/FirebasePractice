package com.example.firebasepractice;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        if (FirebaseAuth.getInstance().getCurrentUser() !=null && !FirebaseAuth.getInstance().getCurrentUser().getUid().equals("KB3XU2qO8IVZwyjtmipK62Fxx3o1")) {
            Thread splashthread = new Thread(){
                @Override
                public void run(){
                    try {
                        sleep(2000);
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                    finally {

                        Intent intent = new Intent(SplashActivity.this,MainActivity.class);
                        intent.putExtra("state","relog");
                        startActivity(intent);
                        finish();
                        return;


                    }
                }
            };
            splashthread.start();


        }
        else{
            Thread splashthread = new Thread(){
            @Override
            public void run(){
                try {
                    sleep(2000);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                finally {

                    Intent welcomeIntent = new Intent(SplashActivity.this, StarterActivity.class);
                    startActivity(welcomeIntent);


                }
            }
        };
            splashthread.start();

        }

    }
    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}