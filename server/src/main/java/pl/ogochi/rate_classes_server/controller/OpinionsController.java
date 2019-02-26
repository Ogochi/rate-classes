package pl.ogochi.rate_classes_server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import pl.ogochi.rate_classes_server.dto.AddClassRequest;
import pl.ogochi.rate_classes_server.dto.AddLecturerRequest;
import pl.ogochi.rate_classes_server.dto.AddOpinionRequest;
import pl.ogochi.rate_classes_server.dto.OpinionListResponse;
import pl.ogochi.rate_classes_server.exception.ClassNotFoundException;
import pl.ogochi.rate_classes_server.exception.LecturerNotFoundException;
import pl.ogochi.rate_classes_server.exception.NotEnoughUserOpinionsException;
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
import java.util.Optional;

@RestController
@RequestMapping("/api/opinions")
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
                .author(userRepository.getUserByEmail(userPrincipal.getEmail()))
                .popularity(0)
                .build();
        opinionRepository.save(opinion);
    }

    @GetMapping
    @RolesAllowed("ROLE_USER")
    public OpinionListResponse getOpinions(@RequestParam String className) {
        Optional<Class> aClass = classRepository.findById(className);
        if (!aClass.isPresent()) {
            throw new ClassNotFoundException();
        }

        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.getUserByEmail(userPrincipal.getEmail());

        if (user.getLikedOpinions().size() < REQUIRED_OPINIONS_COUNT) {
            throw new NotEnoughUserOpinionsException();
        }

        return new OpinionListResponse(opinionRepository.findAllByAClass_Name(className), user.getEmail(), userRepository);
    }
}
