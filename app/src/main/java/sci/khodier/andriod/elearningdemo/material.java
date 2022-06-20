package sci.khodier.andriod.elearningdemo;

public class material {
    String id, name, ext, type, courseId, time, username,materialId;

    public material(String id, String name, String ext, String type, String courseId, String time, String username ,String materialId) {
        this.id = id;
        this.name = name;
        this.ext = ext;
        this.type = type;
        this.courseId = courseId;
        this.time = time;
        this.username = username;
        this.materialId=materialId;
    }

    public String getId() {
        return id;
    }

    public String getTime() {
        return time;
    }

    public String getMaterialId() {
        return materialId;
    }

    public void setMaterialId(String materialId) {
        this.materialId = materialId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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
