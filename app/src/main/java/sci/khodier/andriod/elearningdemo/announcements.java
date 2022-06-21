package sci.khodier.andriod.elearningdemo;

import java.io.Serializable;

public class announcements implements Serializable {
    String message, time, courseName, type, id, degree , courseId;

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public announcements(String message, String time, String courseName, String type, String id , String courseId) {
        this.message = message;
        this.time = time;
        this.courseName = courseName;
        this.type = type;
        this.id = id;
        this.courseId=courseId;
    }

    public announcements(String message, String time, String courseName, String type, String id, String degree,  String courseId) {
        this.message = message;
        this.time = time;
        this.courseName = courseName;
        this.type = type;
        this.id = id;
        this.degree = degree;
        this.courseId=courseId;
    }

    public String getMessage() {
        return message;
    }

    public String getId() {
        return id;
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getCourseName() {
        return courseName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }
}
