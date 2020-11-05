package com.example.firebasepractice.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;

import com.example.firebasepractice.etc.Glovar;
import com.example.firebasepractice.R;
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
    DatabaseReference dbCourseTaken,dbCourse;
    ArrayList<Course> listCourse;
    RecyclerView rvCourse;
    Dialog dialog;
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
        dialog = Glovar.loadingDialog(getActivity());
        dialog.show();
        listCourse = new ArrayList<Course>();
        rvCourse = view.findViewById(R.id.recyclerView_CourseTaken);
        dbCourseTaken =  FirebaseDatabase.getInstance().getReference("Student").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Course Taken");
        dbCourse = FirebaseDatabase.getInstance().getReference("Course");
        setCourseData();
    }

    private void setCourseData() {
        final MutableLiveData<Boolean> checkLoad = new MutableLiveData<>();

        final ArrayList<Course> allCourseList = new ArrayList<>();
        dbCourse.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int counter = 0;
                for (DataSnapshot childSnapshot : snapshot.getChildren()){
                    Course course =  childSnapshot.getValue(Course.class);
                    allCourseList.add(course);
                    counter++;
                }
                if (snapshot.getChildrenCount() == counter){
                    fetchCourseTakenData(allCourseList);
                    dialog.cancel();
                }
            }



            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
    });
    }

    public void fetchCourseTakenData(final ArrayList<Course> allCourseList){
        dbCourseTaken.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listCourse.clear();
                rvCourse.setAdapter(null);
                for (DataSnapshot childSnapshot : snapshot.getChildren()){
                    String courseId = (String) childSnapshot.child("courseID").getValue();
                    for (int i =0; i<allCourseList.size();i++){
                        if (courseId.equals(allCourseList.get(i).getId())){
                            listCourse.add(allCourseList.get(i));
                        }
                    }
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

    }


}

