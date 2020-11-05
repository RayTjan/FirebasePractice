package com.example.firebasepractice.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.MutableLiveData;
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
import com.example.firebasepractice.etc.Glovar;
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

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CardViewViewHolder>{

    private Context context;
    private ArrayList<Course> listcourse;
    private ArrayList<Course> getListCourse() {
        return listcourse;
    }
    public void setListCourse(ArrayList<Course> listcourse) {
        this.listcourse = listcourse;
    }
    public CourseAdapter(Context context) {
        this.context = context;
    }
    AlphaAnimation klik = new AlphaAnimation(1F, 0.6F);
    Dialog dialog ;
    @NonNull
    @Override
    public CourseAdapter.CardViewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.course_adapter, parent, false);
        return new CourseAdapter.CardViewViewHolder(view);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull final CourseAdapter.CardViewViewHolder holder, final int position) {
        final Course course = getListCourse().get(position);
        ArrayList<Course> listCourse = new ArrayList<Course>();
        final DatabaseReference dbCourse = FirebaseDatabase.getInstance().getReference("Course");
        final MutableLiveData<String> getLectName = new MutableLiveData<>();
        dialog =  Glovar.loadingDialog(context);
        final DatabaseReference dbLect = FirebaseDatabase.getInstance().getReference("Lecturer");
        final DatabaseReference dbStudent = FirebaseDatabase.getInstance().getReference("Student");
        dialog.show();
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

    private void fillHolder(String lecturerName, CardViewViewHolder holder, final Course course, final DatabaseReference dbCourse,final DatabaseReference dbStudent) {
        holder.cardName.setText(course.getSubjectName());
        holder.cardDay.setText(course.getDay());
        holder.cardTime.setText(course.getStartTime() + " - " + course.getFinishTime());
        holder.cardLect.setText(lecturerName);
        holder.deleteCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(klik);
                new AlertDialog.Builder(context)
                        .setTitle("Confirmation")
                        .setMessage("Are you sure to delete "+course.getSubjectName()+" data?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialogInterface, int i) {
                                CourseAdapter.this.dialog.show();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        CourseAdapter.this.dialog.cancel();
                                        dbStudent.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                for (DataSnapshot childSnapshot : snapshot.getChildren()){
                                                    final Student student = childSnapshot.getValue(Student.class);
                                                    dbStudent.child(student.getId()).child("Course Taken").addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                                                                String courseId = (String) childSnapshot.child("courseID").getValue();
                                                                if (courseId.equals(course.getId())) {
                                                                    dbStudent.child(student.getId()).child("Course Taken").child(courseId).removeValue();
                                                                }
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                        }
                                                    });
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                        dbCourse.child(course.getId()).removeValue(new DatabaseReference.CompletionListener() {
                                            @Override
                                            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                                Toast.makeText(context, "Delete success!", Toast.LENGTH_SHORT).show();
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
        holder.editCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(context, AddCourseActivity.class);
                in.putExtra("action", "edit");
                in.putExtra("edit_data_course", course);
                in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) context);
                context.startActivity(in, options.toBundle());
                ((Activity)context).finish();
            }
        });
    }


    @Override
    public int getItemCount() {
        return getListCourse().size();
    }

    class CardViewViewHolder extends RecyclerView.ViewHolder{
        TextView cardName,cardDay,cardTime,cardLect;
        Button deleteCourse,editCourse;

        CardViewViewHolder(View itemView) {
            super(itemView);
            cardName = itemView.findViewById(R.id.textView_SNameCourseData);
            cardDay = itemView.findViewById(R.id.textView_dayCourseData);
            cardTime = itemView.findViewById(R.id.textVIew_timeSCourseData);
            cardLect = itemView.findViewById(R.id.textView_lnameCourseData);
            deleteCourse = itemView.findViewById(R.id.button_delCourse);
            editCourse = itemView.findViewById(R.id.button_editeCourse);
        }
    }
}





