package pl.ogochi.rate_classes_server.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import pl.ogochi.rate_classes_server.model.Opinion;

import java.util.List;

public interface OpinionRepository extends MongoRepository<Opinion, String> {
    List<Opinion> findAllByUniveristyClassNameOrderByPopularityDesc(String className);
    void deleteByUniveristyClassNameAndAuthorEmail(String className, String authorEmail);
    int countByAuthorEmail(String authorEmail);
}
