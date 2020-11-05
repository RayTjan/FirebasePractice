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

import com.example.firebasepractice.adapter.CourseAdapter;
import com.example.firebasepractice.etc.ItemClickSupport;
import com.example.firebasepractice.model.Course;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CourseDataActivity extends AppCompatActivity {
    Toolbar bar;
    DatabaseReference dbCourse;
    ArrayList<Course> listCourse;
    RecyclerView rvCourse;
    AlphaAnimation klik = new AlphaAnimation(1F,0.6F);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listCourse = new ArrayList<Course>();
        setContentView(R.layout.activity_course_data);
        rvCourse = findViewById(R.id.recyclerView_CourseData);
        dbCourse = FirebaseDatabase.getInstance().getReference("Course");
        bar = findViewById(R.id.toolbarCourseData);
        setSupportActionBar(bar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        fetchCourseData();
    }

    public void fetchCourseData(){
        dbCourse.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listCourse.clear();
                rvCourse.setAdapter(null);
                for (final DataSnapshot childSnapshot : snapshot.getChildren()){
                    Course course = childSnapshot.getValue(Course.class);
                    listCourse.add(course);
                }
                showCourseData(listCourse);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void showCourseData(final ArrayList<Course> list){
        rvCourse.setLayoutManager(new LinearLayoutManager(CourseDataActivity.this));
        CourseAdapter courseAdapter = new CourseAdapter(CourseDataActivity.this);
        courseAdapter.setListCourse(list);
        rvCourse.setAdapter(courseAdapter);

        ItemClickSupport.addTo(rvCourse).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
//                v.startAnimation(klik);
//                Intent intent = new Intent(CourseDataActivity.this, CourseDetailActivity.class);
//                Course course = new Course(list.get(position).getId(), list.get(position).getSubjectName(), list.get(position).getStartTime(), list.get(position).getFinishTime(),list.get(position).getLecturer());
//                intent.putExtra("data_course", course);
//                intent.putExtra("position", position);
//                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(CourseDataActivity.this);
//                startActivity(intent, options.toBundle());
//                finish();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id==android.R.id.home){
            Intent intent;
            intent = new Intent(CourseDataActivity.this,AddCourseActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("action", "add");
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(CourseDataActivity.this);
            startActivity(intent, options.toBundle());
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent;
        intent = new Intent(CourseDataActivity.this, AddCourseActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("action", "add");
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(CourseDataActivity.this);
        startActivity(intent, options.toBundle());
        finish();
    }
}