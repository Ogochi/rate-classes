package pl.ogochi.rate_classes_server.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document
@Data
@EqualsAndHashCode(exclude = {"isEnabled", "roles"})
public class User {
    @Id
    String email;
    String password;

    boolean isEnabled = false;
    List<String> roles;

    @DBRef
    List<Opinion> opinions = new ArrayList<>();
    @DBRef
    List<Opinion> likedOpinions = new ArrayList<>();

    public User() {}
    public User(String email, String password, List<String> roles) {
        this.email = email;
        this.password = password;
        this.roles = roles;
    }
}
