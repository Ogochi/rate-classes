package pl.ogochi.rate_classes_server.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import pl.ogochi.rate_classes_server.model.Opinion;

import java.util.List;

public interface OpinionRepository extends MongoRepository<Opinion, String> {
    List<Opinion> findAllByAClass_NameOrderByPopularityDesc(String className);
    void deleteByAClass_NameAndAuthorEmail(String className, String authorEmail);
    int countByAuthorEmail(String authorEmail);
}
