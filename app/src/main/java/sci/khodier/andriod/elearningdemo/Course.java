package sci.khodier.andriod.elearningdemo;

import android.graphics.Bitmap;

public class Course {
    private String name, id, creator, img;

    public Course(String name, String id, String img, String creator) {
        this.id = id;
        this.name = name;
        this.creator = creator;
        this.img = img;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getCreator() {
        return creator;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getImg() {
        return img;
    }
}
