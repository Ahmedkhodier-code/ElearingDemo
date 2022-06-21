package sci.khodier.andriod.elearningdemo;

public class student {
    String name, email, degree;

    public student(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public student(String name, String email, String degree) {
        this.name = name;
        this.email = email;
        this.degree = degree;
    }

    public String getName() {
        return name;
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
