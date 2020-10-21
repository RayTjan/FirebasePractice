package com.example.firebasepractice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginStudentActivity extends AppCompatActivity {
    TextInputLayout mEmail,mPassword;
    Button loginStudent;
    ProgressDialog loadingBar;
    FirebaseAuth mAuth;
    String emailCheck, passwordCheck;
    Toolbar bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        bar = findViewById(R.id.toolbarLogin);
        setSupportActionBar(bar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mAuth = FirebaseAuth.getInstance();
        mEmail = findViewById(R.id.text_inputL_LogEmail);
        mPassword = findViewById(R.id.text_inputL_LogPass);
        loginStudent = findViewById(R.id.button_logStu);
        loadingBar = new ProgressDialog(this);

        mEmail.getEditText().addTextChangedListener(inpuCheck);
        mPassword.getEditText().addTextChangedListener(inpuCheck);

        loginStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmail.getEditText().getText().toString();
                String password = mPassword.getEditText().getText().toString();
                LoginUser(email,password);
            }
        });



    }

    private void LoginUser(String email, String password) {
        if (TextUtils.isEmpty(email)||TextUtils.isEmpty(password)){
            if (TextUtils.isEmpty(email)){
                Toast.makeText(this, "Please insert email", Toast.LENGTH_SHORT).show();
            }
            if (TextUtils.isEmpty(password)){
                Toast.makeText(this, "Please insert password", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            loadingBar.setTitle("Logging in..");
            loadingBar.setMessage("Please wait a moment");
            loadingBar.show();
            mAuth.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                Intent toMain = new Intent(LoginStudentActivity.this,MainActivity.class);
                                toMain.putExtra("state","log");
                                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(LoginStudentActivity.this);
                                startActivity(toMain, options.toBundle());
                                loadingBar.dismiss();
                            }
                            else{
                                Toast.makeText(LoginStudentActivity.this, "Log in Failure", Toast.LENGTH_SHORT).show();
                                loginStudent.setEnabled(false);
                                loadingBar.dismiss();
                            }
                        }
                    });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id==android.R.id.home){
            Intent intent;
            intent = new Intent(LoginStudentActivity.this,StarterActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(LoginStudentActivity.this);
            startActivity(intent, options.toBundle());
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent;
        intent = new Intent(LoginStudentActivity.this, StarterActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(LoginStudentActivity.this);
        startActivity(intent, options.toBundle());
        finish();
    }

    TextWatcher inpuCheck = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            emailCheck = mEmail.getEditText().getText().toString();
            passwordCheck = mPassword.getEditText().getText().toString();
            loginStudent.setEnabled(!emailCheck.isEmpty()&&!passwordCheck.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
}