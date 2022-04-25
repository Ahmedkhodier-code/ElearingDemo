package sci.khodier.andriod.elearningdemo;

public class comment {
    String commentText, time, username;

    public comment(String commentText, String time, String username) {
        this.commentText = commentText;
        this.time = time;
        this.username = username;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
