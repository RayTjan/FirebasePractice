package com.example.firebasepractice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.Toast;

import com.example.firebasepractice.adapter.StudentAdapter;
import com.example.firebasepractice.model.Lecturer;
import com.example.firebasepractice.model.Student;
import com.example.firebasepractice.model.Upload;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

public class StudentDataActivity extends AppCompatActivity {
    Toolbar bar;
    DatabaseReference dbStudent;
    ArrayList<Student> listStudent;
    RecyclerView rvStudent;
    AlphaAnimation klik = new AlphaAnimation(1F,0.6F);
    private List<Upload> mUploads;
    StudentAdapter studentAdapter;
    private FirebaseStorage mStorage;
    private DatabaseReference mDatabaseRef;
    private ValueEventListener mDBListener;
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
        mUploads = new ArrayList<>();
        mStorage = FirebaseStorage.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");
        mDBListener = mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                mUploads.clear();

                for (DataSnapshot postSnapShot : snapshot.getChildren()){
                    Upload upload = postSnapShot.getValue(Upload.class);

                    upload.setKey(postSnapShot.getKey());

                    mUploads.add(upload);
                }
                Log.d("HELLO", Integer.toString(mUploads.size()));
//                studentAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(StudentDataActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
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
        studentAdapter = new StudentAdapter(StudentDataActivity.this, mUploads);

        studentAdapter.setListStudent(list);
        rvStudent.setAdapter(studentAdapter);


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