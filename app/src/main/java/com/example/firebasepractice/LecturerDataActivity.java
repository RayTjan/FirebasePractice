package com.example.firebasepractice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;

import com.example.firebasepractice.adapter.LecturerAdapter;
import com.example.firebasepractice.etc.ItemClickSupport;
import com.example.firebasepractice.model.Lecturer;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class LecturerDataActivity extends AppCompatActivity {
    Toolbar bar;
    DatabaseReference dbLecturer;
    ArrayList<Lecturer> listLecturer;
    RecyclerView rvLecturer;
    AlphaAnimation klik = new AlphaAnimation(1F,0.6F);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listLecturer = new ArrayList<Lecturer>();
        setContentView(R.layout.activity_lecturer_data);
        rvLecturer = findViewById(R.id.recyclerView_LecData);
        dbLecturer = FirebaseDatabase.getInstance().getReference("Lecturer");
        bar = findViewById(R.id.toolbarLecData);
        setSupportActionBar(bar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        fetchLecturerData();
    }

    public void fetchLecturerData(){
        dbLecturer.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
            listLecturer.clear();
            rvLecturer.setAdapter(null);
            for (DataSnapshot childSnapshot : snapshot.getChildren()){
                Lecturer lecturer = childSnapshot.getValue(Lecturer.class);
                listLecturer.add(lecturer);
            }
                showLecturerData(listLecturer);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void showLecturerData(final ArrayList<Lecturer> list){
        rvLecturer.setLayoutManager(new LinearLayoutManager(LecturerDataActivity.this));
        LecturerAdapter lecturerAdapter = new LecturerAdapter(LecturerDataActivity.this);
        lecturerAdapter.setListLecturer(list);
        rvLecturer.setAdapter(lecturerAdapter);

        ItemClickSupport.addTo(rvLecturer).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                v.startAnimation(klik);
                Intent intent = new Intent(LecturerDataActivity.this, LecturerDetailActivity.class);
                Lecturer lecturer = new Lecturer(list.get(position).getId(), list.get(position).getName(), list.get(position).getGender(), list.get(position).getExpertise());
                intent.putExtra("data_lecturer", lecturer);
                intent.putExtra("position", position);
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(LecturerDataActivity.this);
                startActivity(intent, options.toBundle());
                finish();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id==android.R.id.home){
            Intent intent;
            intent = new Intent(LecturerDataActivity.this,AddLecturerActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("action", "add");
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(LecturerDataActivity.this);
            startActivity(intent, options.toBundle());
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent;
        intent = new Intent(LecturerDataActivity.this, AddLecturerActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("action", "add");
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(LecturerDataActivity.this);
        startActivity(intent, options.toBundle());
        finish();
    }
}