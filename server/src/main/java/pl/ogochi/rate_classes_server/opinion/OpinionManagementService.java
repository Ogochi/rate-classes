package pl.ogochi.rate_classes_server.opinion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import pl.ogochi.rate_classes_server.dto.AddOpinionRequest;
import pl.ogochi.rate_classes_server.dto.OpinionListResponse;
import pl.ogochi.rate_classes_server.exception.ClassNotFoundException;
import pl.ogochi.rate_classes_server.exception.LecturerNotFoundException;
import pl.ogochi.rate_classes_server.exception.NotEnoughUserOpinionsException;
import pl.ogochi.rate_classes_server.exception.OpinionNotFoundException;
import pl.ogochi.rate_classes_server.model.Lecturer;
import pl.ogochi.rate_classes_server.model.Opinion;
import pl.ogochi.rate_classes_server.model.UniveristyClass;
import pl.ogochi.rate_classes_server.model.User;
import pl.ogochi.rate_classes_server.repository.LecturerRepository;
import pl.ogochi.rate_classes_server.repository.OpinionRepository;
import pl.ogochi.rate_classes_server.repository.UniveristyClassRepository;
import pl.ogochi.rate_classes_server.repository.UserRepository;
import pl.ogochi.rate_classes_server.security.UserPrincipal;

import java.util.Optional;

@Service
public class OpinionManagementService {
    private final static int REQUIRED_OPINIONS_COUNT_FOR_OPINIONS_LOOKUP = 2;

    @Autowired
    UniveristyClassRepository univeristyClassRepository;
    @Autowired
    LecturerRepository lecturerRepository;
    @Autowired
    OpinionRepository opinionRepository;
    @Autowired
    UserRepository userRepository;

    public void createOrUpdateOpinion(AddOpinionRequest request, User user) {
        Optional<Lecturer> lecturer = lecturerRepository.findById(request.getLecturerName());
        if (!lecturer.isPresent()) {
            throw new LecturerNotFoundException();
        }

        Optional<UniveristyClass> universityClass = univeristyClassRepository.findById(request.getClassName());
        if (!universityClass.isPresent()) {
            throw new ClassNotFoundException();
        }

        Opinion opinion = Opinion.builder()
                .lecturer(lecturer.get())
                .univeristyClass(universityClass.get())
                .rating(request.getRating())
                .text(request.getText())
                .authorEmail(user.getEmail())
                .popularity(0)
                .build();

        opinionRepository.deleteByUniveristyClassNameAndAuthorEmail(universityClass.get().getName(), user.getEmail());
        opinionRepository.save(opinion);
    }

    public OpinionListResponse getOpinionsListWithHiddenDetails(String univeristyClassName, User user) {
        Optional<UniveristyClass> universityClass = univeristyClassRepository.findById(univeristyClassName);
        if (!universityClass.isPresent()) {
            throw new ClassNotFoundException();
        }

        if (opinionRepository.countByAuthorEmail(user.getEmail()) < REQUIRED_OPINIONS_COUNT_FOR_OPINIONS_LOOKUP) {
            throw new NotEnoughUserOpinionsException();
        }

        return new OpinionListResponse(opinionRepository.findAllByUniveristyClassNameOrderByPopularityDesc(univeristyClassName),
                user.getEmail(), userRepository);
    }

    public void switchLikeForOpinion(String opinionId, User user) {
        Optional<Opinion> opinion = opinionRepository.findById(opinionId);
        if (!opinion.isPresent()) {
            throw new OpinionNotFoundException();
        }

        if (user.getLikedOpinions().contains(opinion.get())) {
            user.getLikedOpinions().remove(opinion.get());
            opinion.get().decrPopularity();
        } else {
            user.getLikedOpinions().add(opinion.get());
            opinion.get().incrPopularity();
        }

        userRepository.save(user);
        opinionRepository.save(opinion.get());
    }
}
