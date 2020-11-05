package com.example.firebasepractice.adapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.firebasepractice.etc.Glovar;
import com.example.firebasepractice.model.Course;
import com.example.firebasepractice.model.Lecturer;
import com.example.firebasepractice.R;
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
        final DatabaseReference dbLect = FirebaseDatabase.getInstance().getReference("Lecturer");
        final DatabaseReference dbCourse = FirebaseDatabase.getInstance().getReference("Course");
        final DatabaseReference dbStudent = FirebaseDatabase.getInstance().getReference("Student").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Course Taken");
        final MutableLiveData<String> getLectName = new MutableLiveData<>();

        dialog = Glovar.loadingDialog(context);
        dbLect.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot childSnapshot : snapshot.getChildren()){
                    Lecturer teacher = childSnapshot.getValue(Lecturer.class);
                    if (course.getLecturerID().equals(teacher.getId()) ){
                        getLectName.setValue(teacher.getName());
                        break;
                    }

                }
                if (!getLectName.getValue().isEmpty()){
                    fillHolder(getLectName.getValue(), holder, course,dbCourse, dbStudent);

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        dialog.cancel();


    }

    private void fillHolder(String lecturerName, CourseListAdapter.CardViewViewHolder holder, final Course course, final DatabaseReference dbCourse, final DatabaseReference dbStudent) {
        holder.cardName.setText(course.getSubjectName());
        holder.cardDay.setText(course.getDay());
        holder.cardTime.setText(course.getStartTime() + " - " + course.getFinishTime());
        holder.cardLect.setText(lecturerName);
        holder.takeCourseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(klik);
                getCourseTakenList(dbStudent,course,dbCourse);

            }
        });
    }



    @Override
    public int getItemCount() {
        return getListCourse().size();
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


    private void addToCourseTaken(final DatabaseReference dbStudent, final Course course){
        dialog.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.cancel();
                dbStudent.child(course.getId()).child("courseID").setValue(course.getId()).addOnSuccessListener(new OnSuccessListener<Void>() {
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

            }
        }, 2000);
    }

    private  void getCourseTakenList(final DatabaseReference dbStudent, final Course course, final DatabaseReference mCourseDatabase) {
        final ArrayList<String> coursesTaken = new ArrayList<>();
        dbStudent.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    coursesTaken.add((String) childSnapshot.child("courseID").getValue());
                }
                try {
                    checkCourseTime(course,mCourseDatabase,coursesTaken, dbStudent);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkCourseTime(final Course selectedCourse, DatabaseReference mCourseDatabase, final ArrayList<String> coursesTaken, final DatabaseReference dbStudent) throws ParseException {
        final MutableLiveData<Boolean> checkOverlap = new MutableLiveData<>();
        final int startSecond = turnStringTimetoInt(selectedCourse.getStartTime());
        final int finishSecond = turnStringTimetoInt(selectedCourse.getFinishTime());
        mCourseDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    checkOverlap.setValue(false);
                    Log.d("ANNOUNCE", "NO OVERLAP FOUND");
                } else {
                    int i = 0;
                    for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                        i++;
                        Course course = childSnapshot.getValue(Course.class);
                        assert course != null;
                        Log.d("size", snapshot.getChildrenCount() + " compared to " + i);
                        Log.d("COURSECOMPARISON", course.getId() + " COMPARED TO" + selectedCourse.getId());
                        Log.d("LECTURER", course.getId() + " LECTURER TO" + course.getLecturerID());
                        for (int j =0; j< coursesTaken.size();j++){
                            if (coursesTaken.get(j).equals(course.getId())) {
                                assert course != null;
                                if ( selectedCourse.getDay().equals(course.getDay())) {
                                    Log.d("comparing is going on", "COMPARING");
                                    int startDataSec = turnStringTimetoInt(course.getStartTime());
                                    int finishDataSec = turnStringTimetoInt(course.getFinishTime());
                                    Log.d("TIMES", Integer.toString(startSecond));
                                    Log.d("TIMEF", Integer.toString(finishSecond));
                                    Log.d("TIMESD", Integer.toString(startDataSec));
                                    Log.d("TIMESF", Integer.toString(finishDataSec));

                                    if ((startDataSec > startSecond && finishSecond > finishDataSec)||(startDataSec <= startSecond && startSecond < finishDataSec) || (startDataSec < finishSecond && finishSecond <= finishDataSec)) {
                                        checkOverlap.setValue(true);
                                        Log.d("ANNOUNCE", "OVERLAP FOUND");
                                        break;
                                    }

                                }
                            }
                        }

                        if (snapshot.getChildrenCount() == i && checkOverlap.getValue() == null) {
                            checkOverlap.setValue(false);
                            Log.d("ANNOUNCE", "NO OVERLAP FOUND");
                        }

                    }
                }

                if (checkOverlap.getValue() != null) {
                    if (checkOverlap.getValue()) {
                        overlapNotification();
                    } else {
                        addToCourseTaken(dbStudent,selectedCourse);
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void overlapNotification() {
        Log.d("OVERLAP", "OVERLAP WARNING APPEARS");
//        Toast.makeText(this, "Overlapping", LENGTH_SHORT).show();
        new AlertDialog.Builder(context)
                .setTitle("Warning")
                .setMessage("Overlapping Student schedule!")
                .setCancelable(false)
                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialogInterface, int i) {

                        dialogInterface.cancel();
                    }
                })
                .create()
                .show();
    }
}





