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
import android.util.Log;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.util.ArrayList;

import static android.widget.Toast.LENGTH_SHORT;

public class CourseListAdapter extends RecyclerView.Adapter<CourseListAdapter.CardViewViewHolder> {

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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.courselist_adapter, parent, false);
        return new CourseListAdapter.CardViewViewHolder(view);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull final CourseListAdapter.CardViewViewHolder holder, final int position) {
        final Course course = getListCourse().get(position);
        ArrayList<Course> listCourse = new ArrayList<Course>();
        final DatabaseReference dbCourse = FirebaseDatabase.getInstance().getReference("Course");
        final DatabaseReference dbStudent = FirebaseDatabase.getInstance().getReference("Student").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Course Taken");

        final ArrayList<Course> finalListCourse = listCourse;
        dialog = Glovar.loadingDialog(context);
        dbCourse.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
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
        holder.takeCourseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(klik);
                new AlertDialog.Builder(context)
                        .setTitle("Confirmation")
                        .setMessage("Do you really want to take " + course.getSubjectName() + " ?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialogInterface, int i) {
                                try {
                                    if (checkCourseTime(dbStudent, course.getStartTime(), course.getFinishTime())) {
                                        new AlertDialog.Builder(context)
                                                .setTitle("Warning")
                                                .setMessage("Overlapping schedule!")
                                                .setCancelable(false)
                                                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(final DialogInterface dialogInterface, int i) {
//                                            Intent toMain = new Intent(AddCourseActivity.this, AddCourseActivity.class);
//                                            toMain.putExtra("action", "add");
//                                            startActivity(toMain);
//                                            finish();
                                                        dialogInterface.cancel();

                                                    }
                                                })
                                                .create()
                                                .show();
                                    } else {
                                        dialog.show();
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                dialog.cancel();
                                                dbStudent.child(course.getId()).setValue(course).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Toast.makeText(context, "Course added successfully!", Toast.LENGTH_SHORT).show();

                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(context, "Course added successfully!", Toast.LENGTH_SHORT).show();

                                                    }
                                                });
                                                dialogInterface.cancel();

                                            }
                                        }, 2000);
                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

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
    }

    @Override
    public int getItemCount() {
        return getListCourse().size();
    }

    private Boolean checkCourseTime(DatabaseReference courseDB, String timeStart, String timeFinish) throws ParseException {
        final Boolean[] overlap = {false};
//        final SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
        final int startSecond = turnStringTimetoInt(timeStart);
        final int finishSecond = turnStringTimetoInt(timeFinish);
        courseDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    Course course = childSnapshot.getValue(Course.class);
                    assert course != null;
                    int startDataSec = turnStringTimetoInt(course.getStartTime());
                    int finishDataSec = turnStringTimetoInt(course.getFinishTime());
                    Log.d("TIMES", Integer.toString(startSecond));
                    Log.d("TIMEF", Integer.toString(finishSecond));
                    Log.d("TIMESD", Integer.toString(startDataSec));
                    Log.d("TIMESF", Integer.toString(finishDataSec));
                    if ((startDataSec >= startSecond && startSecond < finishDataSec) || (startDataSec > finishSecond && finishSecond <= finishDataSec) || (startSecond < startDataSec && finishDataSec < finishSecond)) {
                        overlap[0] = true;
                        Log.d("OVERLAPPED", Boolean.toString(overlap[0]));
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        Log.d("RESULT", Boolean.toString(overlap[0]));
        Boolean momentary = overlap[0];
        return momentary;
        //the return does not wait for the onDataChange to do it's stuff
    }

    private int turnStringTimetoInt(String time) {
        String[] timeSplit = time.split(":");
        return Integer.parseInt(timeSplit[0]) * 3600 + Integer.parseInt(timeSplit[1]) * 60 + Integer.parseInt(timeSplit[2]);
    }

    class CardViewViewHolder extends RecyclerView.ViewHolder {
        TextView cardName, cardDay, cardTime, cardLect;
        Button takeCourseButton;

        CardViewViewHolder(View itemView) {
            super(itemView);
            cardName = itemView.findViewById(R.id.textView_SNameCourseListData);
            cardDay = itemView.findViewById(R.id.textView_dayCourseListData);
            cardTime = itemView.findViewById(R.id.textVIew_timeSCourseListData);
            cardLect = itemView.findViewById(R.id.textView_lnameCourseListData);
            takeCourseButton = itemView.findViewById(R.id.button_takeCourse);
        }
    }
}





