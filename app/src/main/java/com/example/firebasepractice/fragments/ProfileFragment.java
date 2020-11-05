package com.example.firebasepractice.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.firebasepractice.AddCourseActivity;
import com.example.firebasepractice.AddLecturerActivity;
import com.example.firebasepractice.Glovar;
import com.example.firebasepractice.LecturerDataActivity;
import com.example.firebasepractice.LecturerDetailActivity;
import com.example.firebasepractice.MainActivity;
import com.example.firebasepractice.R;
import com.example.firebasepractice.RegisterStudentActivity;
import com.example.firebasepractice.StarterActivity;
import com.example.firebasepractice.model.Student;
import com.example.firebasepractice.model.Upload;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ProfileFragment extends Fragment {
    public ProfileFragment() {
        // Required empty public constructor
    }
    DatabaseReference dbStudent, mDatabaseRef;
    TextView name,nim,genderAge,address,email;
    Button logoutbtn,editProfile;
    Dialog dialog;
    ImageView profilePic;
    AlphaAnimation klik = new AlphaAnimation(1F, 0.6F);
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.profile_fragment, container, false);

    }

    private void setProfile(final Student student) {
        name.setText(student.getName());
        nim.setText(student.getNim());
        genderAge.setText(student.getGender() + " | " + student.getAge() + " years old");
        address.setText(student.getAddress());
        email.setText(student.getEmail());
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot childSnapshot : snapshot.getChildren()){
                    Upload upload = childSnapshot.getValue(Upload.class);
                    if (upload.getName().equals(student.getId())){
                        Picasso.get()
                                .load(upload.getImageUrl())
                                .placeholder(R.drawable.monster_skeleton)
                                .fit()
                                .centerCrop()
                                .into(profilePic);
                        break;
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        name = view.findViewById(R.id.textView_profileName);
        genderAge = view.findViewById(R.id.textView_profileGenderAge);
        nim = view.findViewById(R.id.textView_profileNim);
        address = view.findViewById(R.id.textView_profileAddress);
        email = view.findViewById(R.id.textView_profileEmail);
        logoutbtn = view.findViewById(R.id.button_logout);
        editProfile = view.findViewById(R.id.button_editProfile);
        profilePic = view.findViewById(R.id.imageView_profileAvatar);
        dialog = Glovar.loadingDialog(getActivity());
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");
        dbStudent = FirebaseDatabase.getInstance().getReference("Student").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        dbStudent.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    final Student student = snapshot.getValue(Student.class);
                    setProfile(student);
                    editProfile.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent in = new Intent(getActivity(), RegisterStudentActivity.class);
                            in.putExtra("action", "edit");
                            in.putExtra("fromProfile","yes");
                            in.putExtra("edit_data_student", student);
                            in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation( getActivity());
                            getActivity().startActivity(in, options.toBundle());
                            (getActivity()).finish();
                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        logoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(klik);
                new AlertDialog.Builder(getActivity())
                        .setTitle("Confirmation")
                        .setMessage("Do you really want to logout?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialogInterface, int i) {
                                dialog.show();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        dialog.cancel();
                                        Intent intent;
                                        FirebaseAuth.getInstance().signOut();
                                        Toast.makeText(getActivity(), "Logout Successfully", Toast.LENGTH_SHORT).show();
                                        intent = new Intent(getActivity(), StarterActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(getActivity());
                                        startActivity(intent, options.toBundle());
                                        getActivity().finish();
                                        dialogInterface.cancel();
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
    }
}