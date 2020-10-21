package com.example.firebasepractice.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Course implements Parcelable {
    private String id, subjectName,day, startTime,finishTime, lecturer;

    Course(){};

    public Course(String id, String subjectName,String day, String startTime, String finishTime, String lecturer) {
        this.id = id;
        this.subjectName = subjectName;
        this.day = day;
        this.startTime = startTime;
        this.finishTime = finishTime;
        this.lecturer = lecturer;
    }

    protected Course(Parcel in) {
        id = in.readString();
        subjectName = in.readString();
        day = in.readString();
        startTime = in.readString();
        finishTime = in.readString();
        lecturer = in.readString();
    }

    public static final Creator<Course> CREATOR = new Creator<Course>() {
        @Override
        public Course createFromParcel(Parcel in) {
            return new Course(in);
        }

        @Override
        public Course[] newArray(int size) {
            return new Course[size];
        }
    };

    public String getId() {
        return id;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public String getDay() {
        return day;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getFinishTime() {
        return finishTime;
    }

    public String getLecturer() {
        return lecturer;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(subjectName);
        dest.writeString(day);
        dest.writeString(startTime);
        dest.writeString(finishTime);
        dest.writeString(lecturer);
    }
}
