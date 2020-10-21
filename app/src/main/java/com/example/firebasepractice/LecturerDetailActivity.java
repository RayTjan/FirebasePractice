package com.example.firebasepractice;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.firebasepractice.model.Lecturer;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class LecturerDetailActivity extends AppCompatActivity {
    Toolbar bar;
    DatabaseReference dbLecturer;
    ArrayList<Lecturer> listLecturer = new ArrayList<>();
    int pos=0;
    TextView dispName,dispGender,dispExp;
    FloatingActionButton deleteLect,editLect;
    Lecturer lecturer;
    Dialog dialog;
    AlphaAnimation klik = new AlphaAnimation(1F, 0.6F);
    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecturer_detail);
        bar = findViewById(R.id.toolbarLecDetail);
        setSupportActionBar(bar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        dbLecturer = FirebaseDatabase.getInstance().getReference("Lecturer");
        dispName = findViewById(R.id.textView_displayLectName);
        dispGender = findViewById(R.id.textView_displayLectGender);
        dispExp = findViewById(R.id.textView_displayLectExp);
        deleteLect = findViewById(R.id.button_lectDelete);
        editLect = findViewById(R.id.button_lectEdit);
        imageView = findViewById(R.id.imageView_LectDetail);
        dialog = Glovar.loadingDialog(LecturerDetailActivity.this);

        Intent intent = getIntent();
        pos = intent.getIntExtra("position",0);
        lecturer = intent.getParcelableExtra("data_lecturer");
        dispName.setText(lecturer.getName());
        dispGender.setText(lecturer.getGender());
        dispExp.setText(lecturer.getExpertise());

        if(lecturer.getGender().equals("Male")){
            imageView.setImageResource(R.drawable.teachguy);
        }
        else{
            imageView.setImageResource(R.drawable.teachfemale);
        }
        deleteLect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(klik);
                new AlertDialog.Builder(LecturerDetailActivity.this)
                        .setTitle("Confirmation")
                        .setMessage("Are you sure to delete "+lecturer.getName()+" data?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialogInterface, int i) {
                                dialog.show();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        dialog.cancel();
                                        dbLecturer.child(lecturer.getId()).removeValue(new DatabaseReference.CompletionListener() {
                                            @Override
                                            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                                Intent in = new Intent(LecturerDetailActivity.this, LecturerDataActivity.class);
                                                in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                Toast.makeText(LecturerDetailActivity.this, "Delete success!", Toast.LENGTH_SHORT).show();
                                                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(LecturerDetailActivity.this);
                                                startActivity(in, options.toBundle());
                                                finish();
                                                dialogInterface.cancel();
                                            }
                                        });

                                    }
                                }, 2000);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .create()
                        .show();
            }
        });



        editLect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(klik);
                Intent in = new Intent(LecturerDetailActivity.this, AddLecturerActivity.class);
                in.putExtra("action", "edit");
                in.putExtra("edit_data_lect", lecturer);
                in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(LecturerDetailActivity.this);
                startActivity(in, options.toBundle());
                finish();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id==android.R.id.home){
            Intent intent;
            intent = new Intent(LecturerDetailActivity.this,LecturerDataActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(LecturerDetailActivity.this);
            startActivity(intent, options.toBundle());
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent;
        intent = new Intent(LecturerDetailActivity.this, LecturerDetailActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(LecturerDetailActivity.this);
        startActivity(intent, options.toBundle());
        finish();
    }
}