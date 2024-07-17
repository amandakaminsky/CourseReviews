import java.sql.Timestamp;

public class Review {
    private int rating;
    private Timestamp timestamp;
    private String comment;
    private int userId;
    private User user;
    private Course course;

    public Review(){}

    public Review(int rating, Timestamp timestamp, String comment, User user, Course course){
        this.rating = rating;
        this.timestamp = timestamp;
        this.comment = comment;
        this.user = user;
        this.course = course;
    }
    public Review(int rating, Timestamp timestamp, User user, Course course){
        this.rating = rating;
        this.timestamp = timestamp;
        this.user = user;
        this.course = course;
    }

    public Review(int rating, Timestamp timestamp, User user, Course course, String comment){
        this.rating = rating;
        this.timestamp = timestamp;
        this.user = user;
        this.course = course;
        this.comment = comment;
    }

    public Review(int rating, Timestamp timestamp, String comment, Course course){
        this.rating = rating;
        this.timestamp = timestamp;
        this.comment = comment;
        this.course = course;
    }

    public Review(int rating, Timestamp timestamp, Course course){
        this.rating = rating;
        this.timestamp = timestamp;
        this.course = course;
    }

    //setters and getter methods

    //rating
    public int getRating() {
        return rating;
    }
    public void setRating(int rating) {
        this.rating = rating;
    }

    //Timestamp
    public Timestamp getTimestamp() {
        return timestamp;
    }
    public void setRating(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    //comment
    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }

    //user
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }

    //course
    public Course getCourse() {
        return course;
    }
    public void setCourse(Course course) {
        this.course = course;
    }
}