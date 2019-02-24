package pl.ogochi.rate_classes_server.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document
public class VerificationToken {
    @Id
    String token;
    Date expiryDate;
    @DBRef
    User user;
}
