
public class Course {

    private int courseId;
    private String subject;
    private int number;
    private String title;
    private double averageRating;

    private int numRatings;
    private int sumRatings;

    public Course() {}

    public Course(int courseId, String subject, int number, String title, double averageRating, int numRatings, int sumRatings) {
        this.courseId = courseId;
        this.subject = subject;
        this.number = number;
        this.title = title;
        this.averageRating = averageRating;
        this.numRatings = numRatings;
        this.sumRatings = sumRatings;
    }

    public Course(String subject, int number, String title, double averageRating, int numRatings, int sumRatings) {
        this.subject = subject;
        this.number = number;
        this.title = title;
        this.averageRating = averageRating;
        this.numRatings = numRatings;
        this.sumRatings = sumRatings;
    }

    public Course(String subject, int number, String title) {
        this.subject=subject;
        this.number=number;
        this.title=title;
        this.averageRating = 0;
        this.numRatings = 0;
        this.sumRatings = 0;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    @Override
    public String toString() {
        if (averageRating > 0) {
            return subject + " " + number + ": " + title + " (Avg Rating: " + String.format("%.2f", averageRating) + ")";
        }
        else {
            return subject + " " + number + ": " + title;
        }
    }

    public boolean equals(Course course) {
        if (courseId == course.getCourseId()) {
            return true;
        }
        return false;
    }
}