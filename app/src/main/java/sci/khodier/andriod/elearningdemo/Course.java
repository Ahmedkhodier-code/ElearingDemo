package sci.khodier.andriod.elearningdemo;

import android.graphics.Bitmap;

public class Course {
   private String name,id;
    private Bitmap img;
public Course(String name, String id,Bitmap img){
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

    public void setImg(Bitmap img) {
        this.img = img;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public Bitmap getImg() {
        return img;
    }
}
