package com.example.firebasepractice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.firebasepractice.model.Lecturer;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class StarterActivity extends AppCompatActivity {
    Button toLec,toCour,toStu,LogStudent;
    boolean press = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starter);
        toLec = findViewById(R.id.button_toLecturer);
        toCour = findViewById(R.id.button_toCourse);
        toStu = findViewById(R.id.button_studentR);
        LogStudent = findViewById(R.id.button_studentL);


        LogStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StarterActivity.this,LoginStudentActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(StarterActivity.this);
                startActivity(intent, options.toBundle());
                finish();
            }
        });
        toLec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StarterActivity.this,AddLecturerActivity.class);
                intent.putExtra("action", "add");
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(StarterActivity.this);
                startActivity(intent, options.toBundle());
                finish();
            }
        });
        toCour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StarterActivity.this,AddCourseActivity.class);
                intent.putExtra("action", "add");
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(StarterActivity.this);
                startActivity(intent, options.toBundle());
                finish();
            }
        });
        toStu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StarterActivity.this,RegisterStudentActivity.class);
                intent.putExtra("action", "add");
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(StarterActivity.this);
                startActivity(intent, options.toBundle());
                finish();
            }
        });
    }
    @Override
    public void onBackPressed() {
        if (press) {
            this.press = false;

            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            startActivity(intent);
        }
        this.press = true;
        Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();
    }
}