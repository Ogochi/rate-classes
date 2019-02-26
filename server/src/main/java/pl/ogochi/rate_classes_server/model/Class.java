package pl.ogochi.rate_classes_server.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Class {
    @Id
    String name;
    String description;
}
