package com.example.firebasepractice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.MutableLiveData;

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

import com.example.firebasepractice.etc.Glovar;
import com.example.firebasepractice.model.Course;
import com.example.firebasepractice.model.Lecturer;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.widget.Toast.LENGTH_SHORT;

public class AddCourseActivity extends AppCompatActivity {
    TextInputLayout mSubject;
    Button addCourse;
    Spinner dayS, timeS, lecturerS, timeF;
    FirebaseAuth mAuth;
    DatabaseReference mCourseDatabase;
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
        mSubject = findViewById(R.id.text_inputL_subject);
        addCourse = findViewById(R.id.button_addCourse);
        dayS = findViewById(R.id.spinner_day);
        timeS = findViewById(R.id.spinner_timeS);
        timeF = findViewById(R.id.spinner_timeF);
        lecturerS = findViewById(R.id.spinner_lecturer);
        loadingBar = new ProgressDialog(this);
        bar = findViewById(R.id.toolbarAddCourse);

        lecturerArrayList = new ArrayList<>();
        mAuth = FirebaseAuth.getInstance();
        dialog = Glovar.loadingDialog(this);
        mCourseDatabase = FirebaseDatabase.getInstance().getReference("Course");
        dbLecturer = FirebaseDatabase.getInstance().getReference("Lecturer");

        //SETUPS
        listLecturer = new ArrayList<String>();
        setSupportActionBar(bar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
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
                setTimeF(timeFinArrNew, position);
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
                    initializeCourse();
                }
            });
        } else {
            course = intent.getParcelableExtra("edit_data_course");
            timeStart = course.getStartTime();
            timeFinish = course.getFinishTime();
            day = course.getDay();
            lecturer = course.getLecturerID();

            String[] timeStartArr = getResources().getStringArray(R.array.timeStart);
            String[] timeFinArr = getResources().getStringArray(R.array.timeFin);
            String[] dayArr = getResources().getStringArray(R.array.day);
            getSupportActionBar().setTitle("EDIT COURSE");
            mSubject.getEditText().setText(course.getSubjectName());

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
            addCourse.setText("Edit Course");
            addCourse.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        checkCourseTime(course.getId());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                }
            });
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
        if (action.equals("add")){
            getMenuInflater().inflate(R.menu.course_menu, menu);
        }
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

    private void setTimeF(ArrayList<String> timeFinArrNew, int position) {
        int posTimeF = 0;
        for (int i = 0; i < timeFinArrNew.size(); i++) {
            if (timeF.getSelectedItem().toString().equals(timeFinArrNew.get(i))) {
                posTimeF = i;
                break;
            }
        }
        for (int i = 0; i < position; i++) {
            timeFinArrNew.remove(0);
        }
        ArrayAdapter<String> adapterTime = new ArrayAdapter<String>(this, R.layout.personal_spinner_item, timeFinArrNew);
        adapterTime.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeF.setAdapter(adapterTime);
        if (position < posTimeF) {
            posTimeF -= position;
            timeF.setSelection(posTimeF);
        }
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

    private void checkCourseTime(final String courseID) throws ParseException {
        getFormValue();
        final MutableLiveData<Boolean> checkOverlap = new MutableLiveData<>();
        final int startSecond = turnStringTimetoInt(timeStart);
        final int finishSecond = turnStringTimetoInt(timeFinish);
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
                        Log.d("COURSECOMPARISON", course.getId() + " COMPARED TO" + courseID);
                        Log.d("LECTURER", course.getId() + " LECTURER TO" + course.getLecturerID());
                        if (!course.getId().equals(courseID)) {
                            assert course != null;
                            if ( day.equals(course.getDay())&&setLecturerID(lecturer).equals(course.getLecturerID())) {
                                Log.d("comparing ig going on", "COMPARING");
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
                        if (action.equals("add")) {
                            finalizeAddCourse();
                        } else {
                            finalizeEditCourse();
                        }
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    private int turnStringTimetoInt(String time) {
        String[] timeSplit = time.split(":");
        return Integer.parseInt(timeSplit[0]) * 3600 + Integer.parseInt(timeSplit[1]) * 60 + Integer.parseInt(timeSplit[2]);
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
                                            Intent in = new Intent(AddCourseActivity.this, AddLecturerActivity.class);
                                            in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            in.putExtra("action", "add");
                                            Toast.makeText(AddCourseActivity.this, "Going to add Lecturer!", LENGTH_SHORT).show();
                                            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(AddCourseActivity.this);
                                            dialog.cancel();
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
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.personal_spinner_item, listLecturer);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter.notifyDataSetChanged();
        lecturerS.setAdapter(adapter);
        if (action.equalsIgnoreCase("edit")) {
            Log.d("INITIALIZING", "LECTUREREDITPOSITION :" + lecturer);
            int posLect = 0;
            for (int i = 0; i < lecturerArrayList.size(); i++) {
                if (lecturer.equalsIgnoreCase(lecturerArrayList.get(i).getId())) {
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


    private void initializeCourse() {
        if (TextUtils.isEmpty(subjectName) || TextUtils.isEmpty(day) || TextUtils.isEmpty(timeStart) || TextUtils.isEmpty(lecturer)) {
            if (TextUtils.isEmpty(subjectName)) {
                Toast.makeText(this, "Please insert subject", LENGTH_SHORT).show();
            }
            if (TextUtils.isEmpty(day)) {
                Toast.makeText(this, "Please select day", LENGTH_SHORT).show();
            }
            if (TextUtils.isEmpty(timeStart)) {
                Toast.makeText(this, "Please select Start time", LENGTH_SHORT).show();
            }
            if (TextUtils.isEmpty(timeFinish)) {
                Toast.makeText(this, "Please select Finish time", LENGTH_SHORT).show();
            }
            if (TextUtils.isEmpty(lecturer)) {
                Toast.makeText(this, "Please select lecturer", LENGTH_SHORT).show();
            }

        } else {


            try {
                checkCourseTime("none");
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }


    public void finalizeEditCourse() {
        getFormValue();
        Map<String, Object> params = new HashMap<>();
        params.put("subjectName", subjectName);
        params.put("day", day);
        params.put("startTime", timeStart);
        params.put("finishTime", timeFinish);
        params.put("lecturerID", setLecturerID(lecturer));
        mCourseDatabase.child(course.getId()).updateChildren(params).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Intent intent;
                intent = new Intent(AddCourseActivity.this, CourseDataActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(AddCourseActivity.this);
                startActivity(intent, options.toBundle());
                finish();
            }
        });


    }

    public void overlapNotification() {
        Log.d("OVERLAP", "OVERLAP WARNING APPEARS");
//        Toast.makeText(this, "Overlapping", LENGTH_SHORT).show();
        new AlertDialog.Builder(AddCourseActivity.this)
                .setTitle("Warning")
                .setMessage("Overlapping Lecturer schedule!")
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

    public void finalizeAddCourse() {
        getFormValue();
        loadingBar.setTitle("Adding course..");
        loadingBar.setMessage("Please wait a moment");
        loadingBar.show();
        String mid = mCourseDatabase.push().getKey();
        Course course = new Course(mid, subjectName, day, timeStart, timeFinish, setLecturerID(lecturer));
        mCourseDatabase.child(mid).setValue(course).addOnSuccessListener(new OnSuccessListener<Void>() {
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


}