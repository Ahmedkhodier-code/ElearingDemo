package sci.khodier.andriod.elearningdemo;

import android.graphics.Bitmap;

public class Course {
   private String name,id;
    private String img;
public Course(String name, String id,String img){
    this.id = id;
    this.name = name;
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
