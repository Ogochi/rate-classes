package pl.ogochi.rate_classes_server.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document
@Data
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"isEnabled", "roles"})
public class User {
    @Id
    String email;
    String password;

    boolean isEnabled;
    List<String> roles;

    public User() {}
}
