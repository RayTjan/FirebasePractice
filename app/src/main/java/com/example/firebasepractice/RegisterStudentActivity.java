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
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.firebasepractice.model.Student;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class RegisterStudentActivity extends AppCompatActivity {
    TextInputLayout mName, mEmail,mPassword,mNIM,mAge,mAddress;
    TextView titleActivity;
    ImageView colorBlock;
    //RADIOBUTTON?
    Button buttonLR;
    FirebaseAuth mAuth;
    DatabaseReference mUserDatabase;
    ProgressDialog loadingBar;
    RadioGroup radioGroup;
    RadioButton radioButton;
    String name,nim,age,address,gender,pass,email,action="",uid;
    Toolbar bar;
    Dialog dialog;
    Student student;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        mUserDatabase = FirebaseDatabase.getInstance().getReference("Student");
        dialog = Glovar.loadingDialog(this);
        bar = findViewById(R.id.toolbarAddStu);
        setSupportActionBar(bar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mName = findViewById(R.id.text_inputL_name);
        mEmail = findViewById(R.id.text_inputL_email);
        mPassword = findViewById(R.id.text_inputL_password);
        mNIM = findViewById(R.id.text_inputLnim);
        mAge = findViewById(R.id.text_inputL_age);
        mAddress = findViewById(R.id.text_inputL_address);
        buttonLR = findViewById(R.id.button_registerStu);
        loadingBar = new ProgressDialog(this);
        radioGroup = findViewById(R.id.radioGroup_student);

        mName.getEditText().addTextChangedListener(inputCheck);
        mNIM.getEditText().addTextChangedListener(inputCheck);
        mAge.getEditText().addTextChangedListener(inputCheck);
        mAddress.getEditText().addTextChangedListener(inputCheck);
        mEmail.getEditText().addTextChangedListener(inputCheck);
        mPassword.getEditText().addTextChangedListener(inputCheck);
        Intent intent = getIntent();
        action = intent.getStringExtra("action");

//        Intent intent = getIntent();
//        action = intent.getStringExtra("action");
//        if (action.equalsIgnoreCase("add")){
//            getSupportActionBar().setTitle("Register Student");
//            buttonLR.setText("Register Student");
//            buttonLR.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    addStudent();
//                }
//            });
//        }
//        else if (action.equalsIgnoreCase("edit")){
//            getFormValue();
//        }


        if(action.equals("add")){
            getSupportActionBar().setTitle("ADD STUDENT");
            mEmail.setEnabled(true);
            mEmail.setClickable(true);
            mEmail.setFocusable(true);
            mPassword.setEnabled(true);
            mPassword.setClickable(true);
            mPassword.setFocusable(true);
            buttonLR.setText("Register Student");
            buttonLR.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getFormValue();
                    addStudent();
                }
            });
        }else{ //saat activity dari lecturer detail & mau mengupdate data
            getSupportActionBar().setTitle("EDIT STUDENT");
            student = intent.getParcelableExtra("edit_data_student");
            mName.getEditText().setText(student.getName());
            mNIM.getEditText().setText(student.getNim());
            mAge.getEditText().setText(student.getAge());
            mAddress.getEditText().setText(student.getAddress());
            mEmail.getEditText().setText(student.getEmail());
            mPassword.getEditText().setText(student.getPassword());
            mEmail.setEnabled(false);
            mEmail.setClickable(false);
            mEmail.setFocusable(false);
            mPassword.setEnabled(false);
            mPassword.setClickable(false);
            mPassword.setFocusable(false);
            if(student.getGender().equalsIgnoreCase("male")){
                radioGroup.check(R.id.radioButton_Male);
            }else{
                radioGroup.check(R.id.radioButton_Female);
            }
            buttonLR.setText("Edit Student");
            buttonLR.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.show();
                    getFormValue();
                    Map<String,Object> params = new HashMap<>();
                    params.put("name", name);
                    params.put("nim", nim);
                    params.put("gender", gender);
                    params.put("age", age);
                    params.put("address", address);
                    params.put("email", email);
                    params.put("password", pass);

                    mUserDatabase.child(student.getId()).updateChildren(params).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            dialog.cancel();
                            Toast.makeText(RegisterStudentActivity.this, "Edit Successfully", Toast.LENGTH_SHORT).show();
                            Intent intent;
                            intent = new Intent(RegisterStudentActivity.this, StudentDataActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(RegisterStudentActivity.this);
                            startActivity(intent, options.toBundle());
                            finish();
                        }
                    });
                }
            });
        }
    }



    public void getFormValue(){
        name = mName.getEditText().getText().toString().trim();
        nim = mNIM.getEditText().getText().toString().trim();
        age = mAge.getEditText().getText().toString().trim();
        address = mAddress.getEditText().getText().toString().trim();
        email = mEmail.getEditText().getText().toString().trim();
        pass = mPassword.getEditText().getText().toString().trim();
        radioButton = findViewById(radioGroup.getCheckedRadioButtonId());
        gender = radioButton.getText().toString();    }

    public void addStudent(){
        getFormValue();
        dialog.show();
        mAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(RegisterStudentActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    dialog.cancel();
                    uid = mAuth.getCurrentUser().getUid();
                    Student student = new Student(mAuth.getCurrentUser().getUid(),name,nim, gender,age,address,email,pass);
                    mUserDatabase.child(mAuth.getCurrentUser().getUid()).setValue(student).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(RegisterStudentActivity.this, "Student Registered Successful", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(RegisterStudentActivity.this, "Student Registered Failed", Toast.LENGTH_SHORT).show();

                        }
                    });
                    mAuth.signOut();
                    Intent intent = new Intent(RegisterStudentActivity.this,RegisterStudentActivity.class);
                    intent.putExtra("action", "add");
                    startActivity(intent);
                }
                else{
                    try {
                        throw task.getException();
                    }catch (FirebaseAuthInvalidCredentialsException malFormed){
                        Toast.makeText(RegisterStudentActivity.this, "Invalid Email or Password", Toast.LENGTH_SHORT).show();
                    } catch (FirebaseAuthUserCollisionException existEmail){
                        Toast.makeText(RegisterStudentActivity.this, "Email already Registered", Toast.LENGTH_SHORT).show();
                    }
                    catch (Exception e) {
                        Toast.makeText(RegisterStudentActivity.this, "Registered Failed", Toast.LENGTH_SHORT).show();
                    }
                    dialog.cancel();
                }
            }
        });
    }




    TextWatcher inputCheck = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            getFormValue();
            buttonLR.setEnabled(!name.isEmpty()&&!nim.isEmpty()&&!age.isEmpty()&&!address.isEmpty()&&!email.isEmpty()&&!pass.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.student_menu,menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id==android.R.id.home){
            if (action.equals("add")){
                Intent intent;
                intent = new Intent(RegisterStudentActivity.this, StarterActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(RegisterStudentActivity.this);
                startActivity(intent, options.toBundle());
            }
            else{
                Intent intent;
                intent = new Intent(RegisterStudentActivity.this, StudentDataActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(RegisterStudentActivity.this);
                startActivity(intent, options.toBundle());
            }
            finish();
            return true;
        }
        else if(id == R.id.student_list){
            Intent intent;
            intent = new Intent(RegisterStudentActivity.this,StudentDataActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(RegisterStudentActivity.this);
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
            intent = new Intent(RegisterStudentActivity.this, StarterActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(RegisterStudentActivity.this);
            startActivity(intent, options.toBundle());
        }
        else{
            Intent intent;
            intent = new Intent(RegisterStudentActivity.this, StudentDataActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(RegisterStudentActivity.this);
            startActivity(intent, options.toBundle());
        }
        finish();

    }

}