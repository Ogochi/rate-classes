package pl.ogochi.rate_classes_server.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Builder
@Data
public class Opinion {
    @Id
    public String id;

    String authorEmail;
    @DBRef
    Lecturer lecturer;
    @DBRef
    @Indexed
    UniveristyClass univeristyClass;

    String text;
    Integer rating;
    Integer popularity;

    public void incrPopularity() {
        popularity++;
    }

    public void decrPopularity() {
        popularity--;
    }
}
