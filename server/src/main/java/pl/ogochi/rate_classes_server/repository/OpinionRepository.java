package pl.ogochi.rate_classes_server.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import pl.ogochi.rate_classes_server.model.Opinion;

public interface OpinionRepository extends MongoRepository<Opinion, String> {
}
