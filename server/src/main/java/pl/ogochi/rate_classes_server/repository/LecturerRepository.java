package pl.ogochi.rate_classes_server.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import pl.ogochi.rate_classes_server.model.Lecturer;

public interface LecturerRepository extends MongoRepository<Lecturer, String> {
}
