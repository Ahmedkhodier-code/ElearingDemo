package sci.khodier.andriod.elearningdemo;

public class material {
    String id, name , ext, type;

    public material(String id, String name, String ext ,String type) {
        this.id = id;
        this.name = name;
        this.ext = ext;
        this.type=type;
    }

    public String getId() {
        return id;
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

    public void setExt(String ext) {
        this.ext = ext;
    }
}
