package com.example.firebasepractice.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.firebasepractice.etc.Glovar;
import com.example.firebasepractice.model.Course;
import com.example.firebasepractice.R;
import com.example.firebasepractice.model.Lecturer;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.CardViewViewHolder>{

    private Context context;
    private ArrayList<Course> listcourse;
    private ArrayList<Course> getListCourse() {
        return listcourse;
    }
    public void setListCourse(ArrayList<Course> listcourse) {
        this.listcourse = listcourse;
    }
    public ScheduleAdapter(Context context) {
        this.context = context;
    }
    AlphaAnimation klik = new AlphaAnimation(1F, 0.6F);
    Dialog dialog;

    @NonNull
    @Override
    public ScheduleAdapter.CardViewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.coursetaken_adapter, parent, false);
        return new ScheduleAdapter.CardViewViewHolder(view);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull final ScheduleAdapter.CardViewViewHolder holder, final int position) {
        final Course course = getListCourse().get(position);
        final DatabaseReference dbCourse = FirebaseDatabase.getInstance().getReference("Course");
        final MutableLiveData<String> getLectName = new MutableLiveData<>();
        final DatabaseReference dbLect = FirebaseDatabase.getInstance().getReference("Lecturer");
        final DatabaseReference dbStudent = FirebaseDatabase.getInstance().getReference("Student").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Course Taken");
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
                    fillHolder(getLectName.getValue(), holder, course, dbStudent);

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        dialog.cancel();


    }

    private void fillHolder(String lecturerName, ScheduleAdapter.CardViewViewHolder holder, final Course course, final DatabaseReference dbStudent) {
        final AlphaAnimation klik = new AlphaAnimation(1F, 0.6F);
        holder.cardName.setText(course.getSubjectName());
        holder.cardDay.setText(course.getDay());
        holder.cardTime.setText(course.getStartTime() + " - " + course.getFinishTime());
        holder.cardLect.setText(lecturerName);
        holder.deleteCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                v.startAnimation(klik);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        v.startAnimation(klik);
                        new AlertDialog.Builder(context)
                                .setTitle("Confirmation")
                                .setMessage("Do you really want to remove " + course.getSubjectName() + " ?")
                                .setCancelable(false)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(final DialogInterface dialogInterface, int i) {
                                        dialog.show();
                                        dbStudent.child(course.getId()).removeValue(new DatabaseReference.CompletionListener() {
                                            @Override
                                            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                                Toast.makeText(context, "Delete success!", Toast.LENGTH_SHORT).show();
                                                dialog.cancel();
                                            }
                                        });
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
                }, 2000);
            }



        });

    }

    @Override
    public int getItemCount() {
        return getListCourse().size();
    }

    class CardViewViewHolder extends RecyclerView.ViewHolder{
        TextView cardName,cardDay,cardTime,cardLect;
        Button deleteCourse;

        CardViewViewHolder(View itemView) {
            super(itemView);
            cardName = itemView.findViewById(R.id.textView_SNameCourseTakenData);
            cardDay = itemView.findViewById(R.id.textView_dayCourseTakenData);
            cardTime = itemView.findViewById(R.id.textVIew_timeSCourseTakenData);
            cardLect = itemView.findViewById(R.id.textView_lnameCourseTakenData);
            deleteCourse = itemView.findViewById(R.id.button_delCourseTaken);
        }
    }
}





