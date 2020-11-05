package com.example.firebasepractice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ActivityOptions;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.firebasepractice.etc.Glovar;
import com.example.firebasepractice.model.Lecturer;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class AddLecturerActivity extends AppCompatActivity {
    TextInputLayout nameLec,mexpertise;
    Button addLec;
    RadioGroup radioGroup;
    RadioButton radioButton;
    FirebaseAuth mAuth;
    DatabaseReference mUserDatabase;
    ProgressDialog loadingBar;
    String nameLecCheck, expertiseCheck,genderCheck,action="";
    Lecturer lecturer;
    Toolbar bar;
    Dialog dialog;
    ImageView lecturerPP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_lecturer);
        mAuth = FirebaseAuth.getInstance();
        dialog = Glovar.loadingDialog(AddLecturerActivity.this);
        mUserDatabase = FirebaseDatabase.getInstance().getReference();
        bar = findViewById(R.id.toolbarAddLect);
        setSupportActionBar(bar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        nameLec = findViewById(R.id.text_inputL_lecName);
        mexpertise = findViewById(R.id.text_inputL_expertise);
        addLec = findViewById(R.id.button_addLecturer);
        radioGroup = findViewById(R.id.radioGroup_LecGender);
        loadingBar = new ProgressDialog(this);
        lecturerPP = findViewById(R.id.imageView_addLectProfilePic);
        nameLec.getEditText().addTextChangedListener(inputCheck);
        mexpertise.getEditText().addTextChangedListener(inputCheck);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                radioButton = findViewById(checkedId);
                String radioResult = radioButton.getText().toString();
                if (radioResult.equals("Male")){
                    lecturerPP.setImageResource(R.drawable.monster_jack_o_lantern);
                }
                else{
                    lecturerPP.setImageResource(R.drawable.monster_witch);
                }
            }
        });

        addLec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioButton = findViewById(radioGroup.getCheckedRadioButtonId());
                genderCheck = radioButton.getText().toString();
                nameLecCheck = nameLec.getEditText().getText().toString();
                expertiseCheck = mexpertise.getEditText().getText().toString();
                AddLecturer(nameLecCheck,genderCheck,expertiseCheck);
            }
        });

        Intent intent = getIntent();
        action = intent.getStringExtra("action");
        if(action.equals("add")){
            getSupportActionBar().setTitle("ADD LECTURER");
            addLec.setText("Add Lecturer");
            addLec.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    radioButton = findViewById(radioGroup.getCheckedRadioButtonId());
                    genderCheck = radioButton.getText().toString();
                    nameLecCheck = nameLec.getEditText().getText().toString().trim();
                    expertiseCheck = mexpertise.getEditText().getText().toString().trim();
                    AddLecturer(nameLecCheck, genderCheck, expertiseCheck);
                }
            });
        }else{ //saat activity dari lecturer detail & mau mengupdate data
            getSupportActionBar().setTitle("EDIT LECTURER");
            lecturer = intent.getParcelableExtra("edit_data_lect");
            nameLec.getEditText().setText(lecturer.getName());
            mexpertise.getEditText().setText(lecturer.getExpertise());
            if(lecturer.getGender().equalsIgnoreCase("male")){
                radioGroup.check(R.id.radioButton_LecMale);
            }else{
                radioGroup.check(R.id.radioButton_LecFemale);
            }
            addLec.setText("Edit Lecturer");
            addLec.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.show();
                    nameLecCheck = nameLec.getEditText().getText().toString().trim();
                    expertiseCheck = mexpertise.getEditText().getText().toString().trim();
                    radioButton = findViewById(radioGroup.getCheckedRadioButtonId());
                    genderCheck = radioButton.getText().toString();
                    Map<String,Object> params = new HashMap<>();
                    params.put("name", nameLecCheck);
                    params.put("expertise", expertiseCheck);
                    params.put("gender", genderCheck);
                    mUserDatabase.child("Lecturer").child(lecturer.getId()).updateChildren(params).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            dialog.cancel();
                            Toast.makeText(AddLecturerActivity.this, "Updated Successfully", Toast.LENGTH_SHORT).show();
                            Intent intent;
                            intent = new Intent(AddLecturerActivity.this, LecturerDataActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(AddLecturerActivity.this);
                            startActivity(intent, options.toBundle());
                            finish();
                        }
                    });
                }
            });
        }
    }

    private void AddLecturer(final String name, final String gender, final String expertise) {
        if (TextUtils.isEmpty(name)||TextUtils.isEmpty(gender)||TextUtils.isEmpty(expertise)){
            if (TextUtils.isEmpty(name)){
                Toast.makeText(this, "Please insert name", Toast.LENGTH_SHORT).show();
            }
            if (TextUtils.isEmpty(gender)){
                Toast.makeText(this, "Please insert gender", Toast.LENGTH_SHORT).show();
            }
            if (TextUtils.isEmpty(expertise)){
                Toast.makeText(this, "Please insert expertise", Toast.LENGTH_SHORT).show();
            }

        }
        else {
            loadingBar.setTitle("Adding Lecturer.");
            loadingBar.setMessage("Please wait a moment");
            loadingBar.show();
            String mid =mUserDatabase.child("Lecturer").push().getKey();
            Lecturer lecturer = new Lecturer(mid,name,gender,expertise);
            mUserDatabase.child("Lecturer").child(mid).setValue(lecturer).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(AddLecturerActivity.this, "Lecturer Added Successfully", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AddLecturerActivity.this, "Lecturer Added Failed", Toast.LENGTH_SHORT).show();
                }
            });
            Intent toMain = new Intent(AddLecturerActivity.this, AddLecturerActivity.class);
            toMain.putExtra("action", "add");
            startActivity(toMain);
            loadingBar.dismiss();
        }
    }

    TextWatcher inputCheck = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            nameLecCheck = nameLec.getEditText().getText().toString();
            expertiseCheck = mexpertise.getEditText().getText().toString();
            addLec.setEnabled(!nameLecCheck.isEmpty() && !expertiseCheck.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (action.equals("add")){
            getMenuInflater().inflate(R.menu.lecturer_menu,menu);
        }
        return true;


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id==android.R.id.home){
            if (action.equals("add")){
                Intent intent;
                intent = new Intent(AddLecturerActivity.this, StarterActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(AddLecturerActivity.this);
                startActivity(intent, options.toBundle());
                finish();
            }
            else{
                Intent intent;
                intent = new Intent(AddLecturerActivity.this, LecturerDetailActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("data_lecturer", lecturer);
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(AddLecturerActivity.this);
                startActivity(intent, options.toBundle());
                finish();
            }
            return true;
        }
        else if(id == R.id.lecturer_list){
            Intent intent;
            intent = new Intent(AddLecturerActivity.this,LecturerDataActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(AddLecturerActivity.this);
            startActivity(intent, options.toBundle());
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (action.equals("add")){
            Intent intent;
            intent = new Intent(AddLecturerActivity.this, StarterActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(AddLecturerActivity.this);
            startActivity(intent, options.toBundle());
            finish();
        }
        else{
            Intent intent;
            intent = new Intent(AddLecturerActivity.this, LecturerDetailActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("data_lecturer", lecturer);
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(AddLecturerActivity.this);
            startActivity(intent, options.toBundle());
            finish();
        }

    }


}