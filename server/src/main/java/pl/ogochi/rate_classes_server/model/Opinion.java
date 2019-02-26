package pl.ogochi.rate_classes_server.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Opinion {
    @Id
    public String id;

    @DBRef
    User author;
    @DBRef
    Lecturer lecturer;
    @DBRef
    @Indexed
    Class aClass;

    String text;
    Integer rating;
    Integer popularity;
}
