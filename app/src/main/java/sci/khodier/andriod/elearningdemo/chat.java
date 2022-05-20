package sci.khodier.andriod.elearningdemo;

public class chat {
    String courseId, courseName;

    public chat(String courseId, String courseName) {
        this.courseId = courseId;
        this.courseName = courseName;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getCorseName() {
        return courseName;
    }

    public void setCorseName(String corseName) {
        this.courseName = corseName;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }
}
