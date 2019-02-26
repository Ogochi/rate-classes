package pl.ogochi.rate_classes_server.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Lecturer {
    @Id
    String name;
    String websiteUrl;
}