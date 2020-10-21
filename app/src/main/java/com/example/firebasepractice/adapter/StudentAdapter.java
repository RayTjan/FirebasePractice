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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.firebasepractice.AddCourseActivity;
import com.example.firebasepractice.AddLecturerActivity;
import com.example.firebasepractice.Glovar;
import com.example.firebasepractice.ItemClickSupport;
import com.example.firebasepractice.LecturerDataActivity;
import com.example.firebasepractice.LecturerDetailActivity;
import com.example.firebasepractice.RegisterStudentActivity;
import com.example.firebasepractice.StudentDataActivity;
import com.example.firebasepractice.model.Lecturer;
import com.example.firebasepractice.R;
import com.example.firebasepractice.model.Student;
import com.example.firebasepractice.model.Upload;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.CardViewViewHolder>{

    private List<Upload> mUploads;
    private Context context;
    private ArrayList<Student> listStudent;
    private ArrayList<Student> getListStudent() {
        return listStudent;
    }
    public void setListStudent(ArrayList<Student> listStudent) {
        this.listStudent = listStudent;
    }
    public StudentAdapter(Context context,List<Upload> uploads) {
        this.context = context;
        mUploads = uploads;

    }
    Dialog dialog;
    AlphaAnimation klik = new AlphaAnimation(1F, 0.6F);
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    @NonNull
    @Override
    public StudentAdapter.CardViewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.student_adapter, parent, false);
        return new StudentAdapter.CardViewViewHolder(view);
    }
    @SuppressLint("ResourceAsColor")

    @Override
    public void onBindViewHolder(@NonNull final CardViewViewHolder holder, final int position) {



        final Student student = getListStudent().get(position);
        ArrayList<Student> listStudent = new ArrayList<Student>();
        final DatabaseReference dbStudent = FirebaseDatabase.getInstance().getReference("Student");
        final DatabaseReference dbImage = FirebaseDatabase.getInstance().getReference("uploads");

        final ArrayList<Student> finalListStudent = listStudent;
        dialog = Glovar.loadingDialog(context);
        mAuth = FirebaseAuth.getInstance();
        final String[] imageUri = {"none"};
        dbImage.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot childSnapshot : snapshot.getChildren()){
                    Upload upload = childSnapshot.getValue(Upload.class);
                    if (upload.getName().equals(student.getId())){
                        Picasso.get()
                                .load(upload.getImageUrl())
                                .placeholder(R.drawable.profile)
                                .fit()
                                .centerCrop()
                                .into(holder.imageVIew);
                        imageUri[0] = upload.getImageUrl();
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        dbStudent.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot childSnapshot : snapshot.getChildren()){
                    Student student = childSnapshot.getValue(Student.class);
                    finalListStudent.add(student);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        holder.cardName.setText(student.getName());
        holder.cardGender.setText(student.getGender());
        holder.cardNIM.setText(student.getNim());
        holder.cardAge.setText(student.getAge() + " years old");
        holder.cardAddress.setText(student.getAddress());
        holder.cardEmail.setText(student.getEmail());
        holder.deleteStu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(klik);
                new AlertDialog.Builder(context)
                        .setTitle("Confirmation")
                        .setMessage("Are you sure to delete "+student.getName()+" data?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialogInterface, int i) {
                                dialog.show();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        dialog.cancel();
                                        String uid = student.getId();
                                        mAuth.signInWithEmailAndPassword(student.getEmail(),student.getPassword()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                firebaseUser = mAuth.getCurrentUser();
                                                firebaseUser.delete();
                                                if ( !imageUri[0].equals("none")){
                                                    StorageReference imageRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageUri[0]);
                                                    imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            dbImage.child(student.getId()).removeValue();
                                                        }
                                                    });
                                                }
                                                dbStudent.child(student.getId()).removeValue(new DatabaseReference.CompletionListener() {
                                                    @Override
                                                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
//                                                        Intent in = new Intent(context, StudentDataActivity.class);
//                                                        in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                        Toast.makeText(context, "Delete success!", Toast.LENGTH_SHORT).show();
//                                                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) context);
//                                                        context.startActivity(in, options.toBundle());
//                                                        ((Activity)context).finish();
                                                        dialogInterface.cancel();
                                                    }
                                                });
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
        holder.editStu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(context, RegisterStudentActivity.class);
                in.putExtra("action", "edit");
                in.putExtra("edit_data_student", student);
                in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) context);
                context.startActivity(in, options.toBundle());
                ((Activity)context).finish();
            }
        });
    }


    @Override
    public int getItemCount() {
        return getListStudent().size();
    }

    class CardViewViewHolder extends RecyclerView.ViewHolder{
        TextView cardName, cardGender,cardNIM, cardAge,cardAddress,cardEmail;
        ImageView imageVIew;
        Button deleteStu,editStu;
        CardViewViewHolder(View itemView) {
            super(itemView);
            cardName = itemView.findViewById(R.id.textView_NameStuData);
            cardNIM = itemView.findViewById(R.id.textView_NIMStuData);
            cardGender = itemView.findViewById(R.id.textVIew_GenderStuData);
            cardAge = itemView.findViewById(R.id.textView_AgeStuData);
            cardAddress = itemView.findViewById(R.id.textView_AddressStuData);
            cardEmail = itemView.findViewById(R.id.textView_EmailStuData);
            deleteStu = itemView.findViewById(R.id.button_stuDeleteData);
            editStu = itemView.findViewById(R.id.button_stuEditData);
            imageVIew = itemView.findViewById(R.id.imageView_dataProfilePic);
        }
    }
}





