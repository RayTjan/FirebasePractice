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
        if (FirebaseAuth.getInstance().getCurrentUser() !=null) {
            // Activity was brought to front and not created,
            // Thus finishing this will get us to the last viewed activity
//                        FirebaseUser fUser = getIntent().getParcelableExtra("user");
//                        Intent intent = new Intent(SplashActivity.this,MainActivity.class);
//                        intent.putExtra("user",fUser);
//                        startActivity(intent);
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
                    Log.d("HALLELUYA","Fail");

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