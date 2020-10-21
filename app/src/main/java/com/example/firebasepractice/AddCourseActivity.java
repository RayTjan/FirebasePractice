package com.example.firebasepractice;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ActivityOptions;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.firebasepractice.model.Course;
import com.example.firebasepractice.model.Lecturer;
import com.example.firebasepractice.model.Student;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.widget.Toast.LENGTH_SHORT;

public class AddCourseActivity extends AppCompatActivity {
    TextInputLayout mSubject;
    Button addCourse;
    Spinner dayS, timeS, lecturerS, timeF;
    FirebaseAuth mAuth;
    DatabaseReference mUserDatabase;
    ProgressDialog loadingBar;
    String subjectCheck, action, subjectName, day, timeStart, timeFinish, lecturer;
    Toolbar bar;
    DatabaseReference dbLecturer;
    ArrayList<String> listLecturer, timeFinArrNew;
    ArrayList<Lecturer> lecturerArrayList;
    String[] timeFinArr;
    Course course;
    Dialog dialog;
    AlphaAnimation klik = new AlphaAnimation(1F, 0.6F);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_course);
        listLecturer = new ArrayList<String>(); //should not store name but ID so edits can be updated

        bar = findViewById(R.id.toolbarAddCourse);
        setSupportActionBar(bar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        lecturerArrayList = new ArrayList<>();
        mAuth = FirebaseAuth.getInstance();
        dialog = Glovar.loadingDialog(this);
        mUserDatabase = FirebaseDatabase.getInstance().getReference("Course");
        dbLecturer = FirebaseDatabase.getInstance().getReference("Lecturer");
        mSubject = findViewById(R.id.text_inputL_subject);
        addCourse = findViewById(R.id.button_addCourse);
        dayS = findViewById(R.id.spinner_day);
        timeS = findViewById(R.id.spinner_timeS);
        timeF = findViewById(R.id.spinner_timeF);
        lecturerS = findViewById(R.id.spinner_lecturer);
        loadingBar = new ProgressDialog(this);
        mSubject.getEditText().addTextChangedListener(inputCheck);
        Intent intent = getIntent();
        action = intent.getStringExtra("action");
        fetchLectData();

        timeS.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                timeFinArr = getResources().getStringArray(R.array.timeFin);
                timeFinArrNew = new ArrayList<>();
                for (int i = 0; i < timeFinArr.length; i++) {
                    timeFinArrNew.add(timeFinArr[i]);
                }
                setTimeF(timeFinArrNew,position);
//                SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm:ss");
//                try {
//                    if (dateFormat.parse(timeF.getSelectedItem().toString()).compareTo(dateFormat.parse(timeS.getSelectedItem().toString())) <= 0) {
//                        timeFinArrNew = new ArrayList<String>();
//                        for (int i = position; i < timeFinArr.length; i++) {
//                            timeFinArrNew.add(timeFinArr[i]);
//                        }
//
//                    }
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        if (action.equals("add")) {
            getSupportActionBar().setTitle("ADD COURSE");
            addCourse.setText("Add Course");
            addCourse.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getFormValue();
                    AddCourse();
                }
            });
        } else {
            course = intent.getParcelableExtra("edit_data_course");
            timeStart = course.getStartTime();
            timeFinish = course.getFinishTime();
            day = course.getDay();
            lecturer = course.getLecturer();
            for (int i = 0; i < lecturerArrayList.size(); i++) {
                if (lecturerArrayList.equals(lecturerArrayList.get(i).getId())) {
                    lecturer = lecturerArrayList.get(i).getName();
                    break;
                }
            }
            Log.d("LECTuRER", lecturer);
            String[] timeStartArr = getResources().getStringArray(R.array.timeStart);
            String[] timeFinArr = getResources().getStringArray(R.array.timeFin);
            String[] dayArr = getResources().getStringArray(R.array.day);
            Log.d("ARRAYY", timeStartArr[0]);
            getSupportActionBar().setTitle("EDIT COURSE");
            mSubject.getEditText().setText(course.getSubjectName());
            // LECTURER NOT YET WORKING FOR EDIT!!!!!!!!!!!!!!
            // Prepares for if lecturer is empty
            // Lecturer can't have teaching time overllaping
            int posTimeS = 0;
            for (int i = 0; i < timeStartArr.length; i++) {
                if (timeStart.equals(timeStartArr[i])) {
                    posTimeS = i;
                    break;
                }
            }
            timeS.setSelection(posTimeS);
            int posTimeF = 0;
            for (int i = 0; i < timeFinArr.length; i++) {
                if (timeFinish.equals(timeFinArr[i])) {
                    posTimeF = i;
                    break;
                }
            }
            timeF.setSelection(posTimeF);
            int dayPos = 0;
            for (int i = 0; i < dayArr.length; i++) {
                if (day.equals(dayArr[i])) {
                    dayPos = i;
                    break;
                }
            }
            dayS.setSelection(dayPos);
//            int posLect = 0;
//            for (int i = 0; i < listLecturer.size(); i++) {
//                if (lecturer.equalsIgnoreCase(listLecturer.get(i))) {
//                    posLect = i;
//                    break;
//                }
//            }
//            lecturerS.setSelection(posLect);
            addCourse.setText("Edit Course");
            addCourse.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.show();
                    getFormValue();
                    try {
                        if (checkCourseTime()) {
                            new AlertDialog.Builder(AddCourseActivity.this)
                                    .setTitle("Warning")
                                    .setMessage("Overlapping Lecturer schedule!")
                                    .setCancelable(false)
                                    .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(final DialogInterface dialogInterface, int i) {
                                            dialog.show();
                                            new Handler().postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    dialog.cancel();
                                                    //                                                Intent in = new Intent(AddCourseActivity.this, AddLecturerActivity.class);
                                                    //                                                in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                    //                                                in.putExtra("action", "add");
                                                    //                                                Toast.makeText(AddCourseActivity.this, "Going to add Lecturer!", Toast.LENGTH_SHORT).show();
                                                    //                                                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(AddCourseActivity.this);
                                                    //                                                startActivity(in, options.toBundle());
                                                    finish();
                                                    dialogInterface.cancel();

                                                }
                                            }, 1000);
                                        }
                                    })
                                    .create()
                                    .show();
                        } else {
                            Map<String, Object> params = new HashMap<>();

                            params.put("subjectName", subjectName);
                            params.put("day", day);
                            params.put("startTime", timeStart);
                            params.put("finishTime", timeFinish);
                            params.put("lecturer", lecturer);
                            params.put("lecturerID", setLecturerID(lecturer));

                            mUserDatabase.child(course.getId()).updateChildren(params).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    dialog.cancel();
                                    Intent intent;
                                    intent = new Intent(AddCourseActivity.this, CourseDataActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(AddCourseActivity.this);
                                    startActivity(intent, options.toBundle());
                                    finish();
                                }
                            });
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                }
            });
        }
    }

    private void refreshLecturerName() {
        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (final DataSnapshot childSnapshot : snapshot.getChildren()) {
                    String refreshLectName = "";
                    Course course = childSnapshot.getValue(Course.class);
                    for (int i = 0; i < lecturerArrayList.size(); i++) {
                        if (course.getLecturerID().equals(lecturerArrayList.get(i).getId())) {
                            refreshLectName = lecturerArrayList.get(i).getName();
                        }

                    }
                    Map<String, Object> params = new HashMap<>();
                    params.put("lecturer", refreshLectName);
                    mUserDatabase.child(course.getId()).updateChildren(params);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private String setLecturerID(String selectedLecturer) {
        for (int i = 0; i < listLecturer.size(); i++) {
            if (selectedLecturer.equals(lecturerArrayList.get(i).getName())) {
                selectedLecturer = lecturerArrayList.get(i).getId();
                break;
            }
        }
        return selectedLecturer;
    }

    private Boolean checkCourseTime() throws ParseException {
        final Boolean[] overlap = {false};//change this with MutableLiveData
//        final SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
        final int startSecond = turnStringTimetoInt(timeStart);
        final int finishSecond = turnStringTimetoInt(timeFinish);
        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    Course course = childSnapshot.getValue(Course.class);
                    assert course != null;
                    if (lecturer.equals(course.getLecturer())) {
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

    private void setTimeF(ArrayList<String> timeFinArrNew, int position) {

        int posTimeF = 0;
        for (int i = 0; i < timeFinArrNew.size(); i++) {
            if (timeF.getSelectedItem().toString().equals(timeFinArrNew.get(i))){
                posTimeF = i;
                break;
            }
        }

        for (int i = 0; i < position; i++) {
            timeFinArrNew.remove(0);
        }

        ArrayAdapter<String> adapterTime = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, timeFinArrNew);
        adapterTime.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeF.setAdapter(adapterTime);
        if (position< posTimeF){
            posTimeF-= position;
            timeF.setSelection(posTimeF);
        }
    }


    public void fetchLectData() {
        dbLecturer.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    Lecturer lecturer = childSnapshot.getValue(Lecturer.class);
                    lecturerArrayList.add(lecturer);
                    listLecturer.add(lecturer.getName());
                }
                if (action.equals("add")) {
                    refreshLecturerName();
                }
                if (listLecturer.isEmpty()) {
                    new AlertDialog.Builder(AddCourseActivity.this)
                            .setTitle("Warning")
                            .setMessage("No Lecturer found")
                            .setCancelable(false)
                            .setPositiveButton("Add a Lecturer", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(final DialogInterface dialogInterface, int i) {
                                    dialog.show();
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            dialog.cancel();
                                            Intent in = new Intent(AddCourseActivity.this, AddLecturerActivity.class);
                                            in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            in.putExtra("action", "add");
                                            Toast.makeText(AddCourseActivity.this, "Going to add Lecturer!", LENGTH_SHORT).show();
                                            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(AddCourseActivity.this);
                                            startActivity(in, options.toBundle());
                                            finish();
                                            dialogInterface.cancel();

                                        }
                                    }, 1000);
                                }
                            })
                            .setNegativeButton("Back", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(final DialogInterface dialogInterface, int which) {
                                    Intent in = new Intent(AddCourseActivity.this, StarterActivity.class);
                                    in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    Toast.makeText(AddCourseActivity.this, "Going back to home!", LENGTH_SHORT).show();
                                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(AddCourseActivity.this);
                                    startActivity(in, options.toBundle());
                                    finish();
                                    dialogInterface.cancel();

                                }
                            })
                            .create()
                            .show();
                } else {
                    showLectSpinner(listLecturer);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void showLectSpinner(ArrayList<String> listLecturer) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listLecturer);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter.notifyDataSetChanged();
        lecturerS.setAdapter(adapter);
        if (action.equalsIgnoreCase("edit")) {
            int posLect = 0;
            for (int i = 0; i < listLecturer.size(); i++) {
                if (lecturer.equalsIgnoreCase(listLecturer.get(i))) {
                    posLect = i;
                    break;
                }
            }
            lecturerS.setSelection(posLect);
        }
//        getFormValue();
    }

    public void getFormValue() {
        subjectName = mSubject.getEditText().getText().toString();
        day = dayS.getSelectedItem().toString();
        timeStart = timeS.getSelectedItem().toString();
        timeFinish = timeF.getSelectedItem().toString();
        lecturer = lecturerS.getSelectedItem().toString();
    }


    private void AddCourse() {
        if (TextUtils.isEmpty(subjectName) || TextUtils.isEmpty(day) || TextUtils.isEmpty(timeStart)) {
            if (TextUtils.isEmpty(subjectName)) {
                Toast.makeText(this, "Please insert subject", LENGTH_SHORT).show();
            }
            if (TextUtils.isEmpty(day)) {
                Toast.makeText(this, "Please insert day", LENGTH_SHORT).show();
            }
            if (TextUtils.isEmpty(timeStart)) {
                Toast.makeText(this, "Please insert Start time", LENGTH_SHORT).show();
            }
            if (TextUtils.isEmpty(timeFinish)) {
                Toast.makeText(this, "Please insert Finish time", LENGTH_SHORT).show();
            }
            if (TextUtils.isEmpty(lecturer)) {
                Toast.makeText(this, "Please insert lecturer", LENGTH_SHORT).show();
            }

        } else {
            loadingBar.setTitle("Adding course..");
            loadingBar.setMessage("Please wait a moment");
            try {
                boolean courseT = checkCourseTime();
                Log.d("WAIIII", Boolean.toString(courseT));// DAHELLL WHY SUDDENLY FALSE
                if (courseT) {
                    new AlertDialog.Builder(AddCourseActivity.this)
                            .setTitle("Warning")
                            .setMessage("Overlapping Lecturer schedule!")
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
                    loadingBar.show();

                    String mid = mUserDatabase.push().getKey();
                    Course course = new Course(mid, subjectName, day, timeStart, timeFinish, lecturer, setLecturerID(lecturer));
                    mUserDatabase.child(mid).setValue(course).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(AddCourseActivity.this, "Course Added Successfully", LENGTH_SHORT).show();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AddCourseActivity.this, "Course Added Failed", LENGTH_SHORT).show();

                        }
                    });

                    Intent toMain = new Intent(AddCourseActivity.this, AddCourseActivity.class);
                    toMain.putExtra("action", "add");
                    startActivity(toMain);
                    loadingBar.dismiss();
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    TextWatcher inputCheck = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            subjectCheck = mSubject.getEditText().getText().toString();
            addCourse.setEnabled(!subjectCheck.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.course_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            if (action.equals("add")) {
                Intent intent;
                intent = new Intent(AddCourseActivity.this, StarterActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(AddCourseActivity.this);
                startActivity(intent, options.toBundle());
                finish();
            } else {
                Intent intent;
                intent = new Intent(AddCourseActivity.this, CourseDataActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(AddCourseActivity.this);
                startActivity(intent, options.toBundle());
                finish();
            }
            return true;
        } else if (id == R.id.course_list) {
            Intent intent;
            intent = new Intent(AddCourseActivity.this, CourseDataActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(AddCourseActivity.this);
            startActivity(intent, options.toBundle());
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (action.equals("add")) {
            Intent intent;
            intent = new Intent(AddCourseActivity.this, StarterActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(AddCourseActivity.this);
            startActivity(intent, options.toBundle());
            finish();
        } else {
            Intent intent;
            intent = new Intent(AddCourseActivity.this, CourseDataActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(AddCourseActivity.this);
            startActivity(intent, options.toBundle());
            finish();
        }

    }

}