package pl.ogochi.rate_classes_server.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import pl.ogochi.rate_classes_server.model.UniveristyClass;

public interface UniveristyClassRepository extends MongoRepository<UniveristyClass, String> {
}
