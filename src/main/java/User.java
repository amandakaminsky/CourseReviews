import java.util.ArrayList;
import java.util.List;

public class User {
    private String username;
    private String password;
    private int id;
    private List<edu.virginia.sde.reviews.Review> reviews;

    public User(String username, String password, List<edu.virginia.sde.reviews.Review> reviews) {
        this.username = username;
        this.password = password;
        this.reviews = reviews;
    }

    public User(String username, String password, int id) {
        this.username = username;
        this.password = password;
        this.id = id;
        this.reviews = new ArrayList<>();
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.reviews = new ArrayList<>();
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void addReview(edu.virginia.sde.reviews.Review review) {
        reviews.add(review);
    }

    public void setReviews(List<edu.virginia.sde.reviews.Review> reviews) {
        this.reviews = reviews;
    }

    public List<edu.virginia.sde.reviews.Review> getReviews() {
        return reviews;
    }

}