package sci.khodier.andriod.elearningdemo;

public class material {
    String id, name, ext, type, courseId, time;

    public material(String id, String name, String ext, String type, String courseId, String time) {
        this.id = id;
        this.name = name;
        this.ext = ext;
        this.type = type;
        this.courseId = courseId;
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
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

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public String getExt() {
        return ext;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }
}
