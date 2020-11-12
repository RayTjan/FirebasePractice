package com.example.firebasepractice.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Student implements Parcelable {
    private String id,name, nim, gender, age, address, email, password,token;

   public Student(){};

    public Student(String id, String name, String nim, String gender, String age, String address, String email, String password, String token) {
        this.id = id;
        this.name = name;
        this.nim = nim;
        this.gender = gender;
        this.age = age;
        this.address = address;
        this.email = email;
        this.password = password;
        this.token = token;
    }

    protected Student(Parcel in) {
        id = in.readString();
        name = in.readString();
        nim = in.readString();
        gender = in.readString();
        age = in.readString();
        address = in.readString();
        email = in.readString();
        password = in.readString();
        token = in.readString();
    }

    public static final Creator<Student> CREATOR = new Creator<Student>() {
        @Override
        public Student createFromParcel(Parcel in) {
            return new Student(in);
        }

        @Override
        public Student[] newArray(int size) {
            return new Student[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNim() {
        return nim;
    }

    public void setNim(String nim) {
        this.nim = nim;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(nim);
        dest.writeString(gender);
        dest.writeString(age);
        dest.writeString(address);
        dest.writeString(email);
        dest.writeString(password);
        dest.writeString(token);
    }
}
