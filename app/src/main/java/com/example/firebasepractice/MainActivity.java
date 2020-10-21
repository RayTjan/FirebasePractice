package com.example.firebasepractice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.firebasepractice.fragments.CourseFragment;
import com.example.firebasepractice.fragments.ProfileFragment;
import com.example.firebasepractice.fragments.ScheduleFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottomNav;
    Toolbar bar;
    boolean press = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNav = findViewById(R.id.bottomNavigation);
        bar = findViewById(R.id.toolbar_main);
        if(getIntent().getStringExtra("state").equals("relog")){
            Toast.makeText(this, "Welcome back!", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this, "Login Successfully", Toast.LENGTH_SHORT).show();
        }
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = null;
                switch (item.getItemId()){

                    //firstPage is courses taken
                    // second page is courses AVAILABLE to be taken
                    //cannot take overlapping schedules
                    //slide left and right

                    case R.id.nav_FirstPage:
                        fragment = new ScheduleFragment();
                        bar.setTitle("Schedule");
                        loadFragment(fragment);
                        return true;

                    case R.id.nav_SecondPage:
                        fragment = new CourseFragment();
                        bar.setTitle("Courses");
                        loadFragment(fragment);
                        return true;

                    case R.id.nav_ThirdPage:
                        fragment = new ProfileFragment();
                        bar.setTitle("Profile");
                        loadFragment(fragment);
                        return true;
                }
                return false;
            }
        });
    }
    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_main, fragment);
        transaction.commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        bottomNav.setSelectedItemId(R.id.nav_FirstPage); /*mulai dimana saat run aplikasi */
//        Fragment fragment = new HomeFragment();
//        loadFragment(fragment);
    }


    @Override
    public void onBackPressed() {
        if (press){
            this.press = false;
//            Intent splash = new Intent(MainActivity.this,SplashActivity.class);
//            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this);
//            splash.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
//            Log.d("HALLELUYA","IDKKK");
//            startActivity(splash, options.toBundle());
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
//            intent.putExtra("user",fUser);
//            finish();
//            moveTaskToBack(true);
            startActivity(intent);
        }
        this.press = true;
        Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();
    }


}