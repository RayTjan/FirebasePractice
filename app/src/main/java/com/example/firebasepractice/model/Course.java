package com.example.firebasepractice.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Course implements Parcelable {
    private String id, subjectName,day, startTime,finishTime, lecturer,lecturerID;

    Course(){};

    public Course(String id, String subjectName, String day, String startTime, String finishTime, String lecturer, String lecturerID) {
        this.id = id;
        this.subjectName = subjectName;
        this.day = day;
        this.startTime = startTime;
        this.finishTime = finishTime;
        this.lecturer = lecturer;
        this.lecturerID = lecturerID;
    }

    protected Course(Parcel in) {
        id = in.readString();
        subjectName = in.readString();
        day = in.readString();
        startTime = in.readString();
        finishTime = in.readString();
        lecturer = in.readString();
        lecturerID = in.readString();
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

    public void setId(String id) {
        this.id = id;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(String finishTime) {
        this.finishTime = finishTime;
    }

    public String getLecturer() {
        return lecturer;
    }

    public void setLecturer(String lecturer) {
        this.lecturer = lecturer;
    }

    public String getLecturerID() {
        return lecturerID;
    }

    public void setLecturerID(String lecturerID) {
        this.lecturerID = lecturerID;
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
        dest.writeString(lecturerID);
    }
}
