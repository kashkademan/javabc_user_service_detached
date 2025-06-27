package school.faang.user_service.dmitrysprint4.facebookcomments;

import java.time.LocalDateTime;

public class Comment {

    private String text;

    private String author;

    private LocalDateTime timestamp;

    public Comment(String text, String author, LocalDateTime timestamp) {
        this.text = text;
        this.author = author;
        this.timestamp = timestamp;
    }
}
