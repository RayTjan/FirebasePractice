package com.example.firebasepractice.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;

import com.example.firebasepractice.ItemClickSupport;
import com.example.firebasepractice.R;
import com.example.firebasepractice.adapter.CourseListAdapter;
import com.example.firebasepractice.adapter.ScheduleAdapter;
import com.example.firebasepractice.model.Course;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ScheduleFragment extends Fragment {
    Toolbar bar;
    DatabaseReference dbCourse;
    ArrayList<Course> listCourse;
    RecyclerView rvCourse;
    AlphaAnimation klik = new AlphaAnimation(1F,0.6F);
    public ScheduleFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.schedule_fragment, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listCourse = new ArrayList<Course>();
        rvCourse = view.findViewById(R.id.recyclerView_CourseTaken);
        dbCourse =  FirebaseDatabase.getInstance().getReference("Student").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Course Taken");
        fetchCourseData();
    }

    public void fetchCourseData(){
        dbCourse.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listCourse.clear();
                rvCourse.setAdapter(null);
                for (DataSnapshot childSnapshot : snapshot.getChildren()){
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
        rvCourse.setLayoutManager(new LinearLayoutManager(getActivity()));
        ScheduleAdapter courseTakenAdapter = new ScheduleAdapter(getActivity());
        courseTakenAdapter.setListCourse(list);
        rvCourse.setAdapter(courseTakenAdapter);

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


}

