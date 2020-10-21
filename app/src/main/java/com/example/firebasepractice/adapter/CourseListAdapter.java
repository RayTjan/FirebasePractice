package com.example.firebasepractice.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.firebasepractice.AddCourseActivity;
import com.example.firebasepractice.Glovar;
import com.example.firebasepractice.LecturerDataActivity;
import com.example.firebasepractice.LecturerDetailActivity;
import com.example.firebasepractice.RegisterStudentActivity;
import com.example.firebasepractice.model.Course;
import com.example.firebasepractice.model.Lecturer;
import com.example.firebasepractice.R;
import com.example.firebasepractice.model.Student;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CourseListAdapter extends RecyclerView.Adapter<CourseListAdapter.CardViewViewHolder>{

    private Context context;
    private ArrayList<Course> listcourse;
    private ArrayList<Course> getListCourse() {
        return listcourse;
    }
    public void setListCourse(ArrayList<Course> listcourse) {
        this.listcourse = listcourse;
    }
    public CourseListAdapter(Context context) {
        this.context = context;
    }
    AlphaAnimation klik = new AlphaAnimation(1F, 0.6F);
    Dialog dialog;
    @NonNull
    @Override
    public CourseListAdapter.CardViewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.course_adapter, parent, false);
        return new CourseListAdapter.CardViewViewHolder(view);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull final CourseListAdapter.CardViewViewHolder holder, final int position) {
        final Course course = getListCourse().get(position);
        ArrayList<Course> listCourse = new ArrayList<Course>();
        final DatabaseReference dbCourse = FirebaseDatabase.getInstance().getReference(" List");
        final ArrayList<Course> finalListCourse = listCourse;
        dialog = Glovar.loadingDialog(context);
        dbCourse.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot childSnapshot : snapshot.getChildren()){
                    Course course = childSnapshot.getValue(Course.class);
                    finalListCourse.add(course);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        holder.cardName.setText(course.getSubjectName());
        holder.cardDay.setText(course.getDay());
        holder.cardTime.setText(course.getStartTime() + " - " + course.getFinishTime());
        holder.cardLect.setText(course.getLecturer());

    }

    @Override
    public int getItemCount() {
        return getListCourse().size();
    }

    class CardViewViewHolder extends RecyclerView.ViewHolder{
        TextView cardName,cardDay,cardTime,cardLect;

        CardViewViewHolder(View itemView) {
            super(itemView);
            cardName = itemView.findViewById(R.id.textView_SNameCourseData);
            cardDay = itemView.findViewById(R.id.textView_dayCourseData);
            cardTime = itemView.findViewById(R.id.textVIew_timeSCourseData);
            cardLect = itemView.findViewById(R.id.textView_lnameCourseData);

        }
    }
}





