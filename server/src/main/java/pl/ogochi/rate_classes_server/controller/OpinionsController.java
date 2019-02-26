package pl.ogochi.rate_classes_server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import pl.ogochi.rate_classes_server.dto.AddClassRequest;
import pl.ogochi.rate_classes_server.dto.AddLecturerRequest;
import pl.ogochi.rate_classes_server.dto.AddOpinionRequest;
import pl.ogochi.rate_classes_server.dto.OpinionListResponse;
import pl.ogochi.rate_classes_server.exception.ClassNotFoundException;
import pl.ogochi.rate_classes_server.exception.LecturerNotFoundException;
import pl.ogochi.rate_classes_server.exception.NotEnoughUserOpinionsException;
import pl.ogochi.rate_classes_server.exception.OpinionNotFoundException;
import pl.ogochi.rate_classes_server.model.Class;
import pl.ogochi.rate_classes_server.model.Lecturer;
import pl.ogochi.rate_classes_server.model.Opinion;
import pl.ogochi.rate_classes_server.model.User;
import pl.ogochi.rate_classes_server.repository.ClassRepository;
import pl.ogochi.rate_classes_server.repository.LecturerRepository;
import pl.ogochi.rate_classes_server.repository.OpinionRepository;
import pl.ogochi.rate_classes_server.repository.UserRepository;
import pl.ogochi.rate_classes_server.security.UserPrincipal;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/opinions")
@CrossOrigin
public class OpinionsController {
    @Autowired
    ClassRepository classRepository;
    @Autowired
    LecturerRepository lecturerRepository;
    @Autowired
    OpinionRepository opinionRepository;
    @Autowired
    UserRepository userRepository;

    private final static int REQUIRED_OPINIONS_COUNT = 2;

    @PutMapping("/addClass")
    @RolesAllowed("ROLE_USER")
    public void addClass(@Valid @RequestBody AddClassRequest request) {
        classRepository.save(new Class(request.getName(), request.getDescription()));
    }

    @PutMapping("/addLecturer")
    @RolesAllowed("ROLE_USER")
    public void addLecturer(@Valid @RequestBody AddLecturerRequest request) {
        lecturerRepository.save(new Lecturer(request.getName(), request.getWebsiteUrl()));
    }

    @PostMapping("/addOpinion")
    @RolesAllowed("ROLE_USER")
    @Transactional
    public void addOrUpdateOpinion(@Valid @RequestBody AddOpinionRequest request) {
        Optional<Lecturer> lecturer = lecturerRepository.findById(request.getLecturerName());
        if (!lecturer.isPresent()) {
            throw new LecturerNotFoundException();
        }

        Optional<Class> aClass = classRepository.findById(request.getClassName());
        if (!aClass.isPresent()) {
            throw new ClassNotFoundException();
        }

        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Opinion opinion = Opinion.builder()
                .lecturer(lecturer.get())
                .aClass(aClass.get())
                .rating(request.getRating())
                .text(request.getText())
                .authorEmail(userPrincipal.getEmail())
                .popularity(0)
                .build();

        opinionRepository.deleteByAClass_NameAndAuthorEmail(aClass.get().getName(), userPrincipal.getEmail());
        opinionRepository.save(opinion);
    }

    @GetMapping
    @RolesAllowed("ROLE_USER")
    public OpinionListResponse getOpinions(@RequestParam String className) {
        Optional<Class> aClass = classRepository.findById(className);
        if (!aClass.isPresent()) {
            throw new ClassNotFoundException();
        }

        User user = getCurrentUser();
        if (opinionRepository.countByAuthorEmail(user.getEmail()) < REQUIRED_OPINIONS_COUNT) {
            throw new NotEnoughUserOpinionsException();
        }

        return new OpinionListResponse(opinionRepository.findAllByAClass_NameOrderByPopularityDesc(className),
                user.getEmail(), userRepository);
    }

    @PostMapping("/switchOpinionLike")
    @RolesAllowed("ROLE_USER")
    @Transactional
    public void switchOpinionLike(@RequestParam String opinionId) {
        Optional<Opinion> opinion = opinionRepository.findById(opinionId);
        if (!opinion.isPresent()) {
            throw new OpinionNotFoundException();
        }

        User user = getCurrentUser();
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

    @GetMapping("/classes")
    public List<Class> getClasses() {
        return classRepository.findAll();
    }

    @GetMapping("/lecturers")
    public List<Lecturer> getLecturers() {
        return lecturerRepository.findAll();
    }

    private User getCurrentUser() {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.getUserByEmail(userPrincipal.getEmail());
    }
}
