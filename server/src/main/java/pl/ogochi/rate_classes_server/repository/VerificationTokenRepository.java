package pl.ogochi.rate_classes_server.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import pl.ogochi.rate_classes_server.model.User;
import pl.ogochi.rate_classes_server.model.VerificationToken;

import java.util.UUID;

public interface VerificationTokenRepository extends MongoRepository<VerificationToken, String> {

    default VerificationToken createNewToken(User user) {
        String token = UUID.randomUUID().toString();

        while (findById(token).isPresent()) {
            token = UUID.randomUUID().toString();
        }

        VerificationToken verificationToken =  new VerificationToken(token, user);
        save(verificationToken);

        return verificationToken;
    }
}
