package pl.ogochi.rate_classes_server.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
@AllArgsConstructor
public class Lecturer {
    @Id
    String name;
    String websiteUrl;
}
