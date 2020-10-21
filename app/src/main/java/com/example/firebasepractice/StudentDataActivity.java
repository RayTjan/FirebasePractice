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
import android.widget.Button;

import com.example.firebasepractice.adapter.StudentAdapter;
import com.example.firebasepractice.model.Lecturer;
import com.example.firebasepractice.model.Student;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class StudentDataActivity extends AppCompatActivity {
    Toolbar bar;
    DatabaseReference dbStudent;
    ArrayList<Student> listStudent;
    RecyclerView rvStudent;
    AlphaAnimation klik = new AlphaAnimation(1F,0.6F);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listStudent = new ArrayList<Student>();
        setContentView(R.layout.activity_student_data);
        rvStudent = findViewById(R.id.recyclerView_StuData);
        dbStudent = FirebaseDatabase.getInstance().getReference("Student");
        bar = findViewById(R.id.toolbarStuData);
        setSupportActionBar(bar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        fetchStudentData();
    }

    public void fetchStudentData(){
        dbStudent.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listStudent.clear();
                rvStudent.setAdapter(null);
                for (DataSnapshot childSnapshot : snapshot.getChildren()){
//                    Lecturer lecturer = childSnapshot.getValue(Lecturer.class);
                    Student student = childSnapshot.getValue(Student.class);
                    listStudent.add(student);
                }
                showStudentData(listStudent);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    public void showStudentData(final ArrayList<Student> list){
        rvStudent.setLayoutManager(new LinearLayoutManager(StudentDataActivity.this));
        StudentAdapter studentAdapter = new StudentAdapter(StudentDataActivity.this);
        studentAdapter.setListStudent(list);
        rvStudent.setAdapter(studentAdapter);

//        ItemClickSupport.addTo(rvStudent).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
//            @Override
//            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
//                v.startAnimation(klik);
//                Intent intent = new Intent(StudentDataActivity.this, StudentDetailActivity.class);
//                Student student = new Student(list.get(position).getId(), list.get(position).getName(), list.get(position).getNim(), list.get(position).getGender(),
//                        list.get(position).getAge(),list.get(position).getAddress(),list.get(position).getEmail(),list.get(position).getPassword());
//                intent.putExtra("data_student", student);
//                intent.putExtra("position", position);
//                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(StudentDataActivity.this);
//                startActivity(intent, options.toBundle());
//                finish();
//            }
//
//
//        });

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id==android.R.id.home){
            Intent intent;
            intent = new Intent(StudentDataActivity.this,RegisterStudentActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("action", "add");
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(StudentDataActivity.this);
            startActivity(intent, options.toBundle());
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent;
        intent = new Intent(StudentDataActivity.this, RegisterStudentActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("action", "add");
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(StudentDataActivity.this);
        startActivity(intent, options.toBundle());
        finish();
    }
}