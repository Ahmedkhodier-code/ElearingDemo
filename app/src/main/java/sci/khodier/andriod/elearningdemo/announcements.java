package sci.khodier.andriod.elearningdemo;

public class announcements {
    String message, time, courseName, type , id;

    public announcements(String message, String time, String courseName, String type, String id) {
        this.message = message;
        this.time = time;
        this.courseName = courseName;
        this.type = type;
        this.id=id;
    }

    public String getMessage() {
        return message;
    }

    public String getId() {
        return id;
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
