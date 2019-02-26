package pl.ogochi.rate_classes_server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import pl.ogochi.rate_classes_server.dao.AddClassRequest;
import pl.ogochi.rate_classes_server.dao.AddLecturerRequest;
import pl.ogochi.rate_classes_server.dao.AddOpinionRequest;
import pl.ogochi.rate_classes_server.exception.ClassNotFoundException;
import pl.ogochi.rate_classes_server.exception.LecturerNotFoundException;
import pl.ogochi.rate_classes_server.model.Class;
import pl.ogochi.rate_classes_server.model.Lecturer;
import pl.ogochi.rate_classes_server.model.Opinion;
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
@RolesAllowed("ROLE_USER")
public class OpinionsController {
    @Autowired
    ClassRepository classRepository;
    @Autowired
    LecturerRepository lecturerRepository;
    @Autowired
    OpinionRepository opinionRepository;
    @Autowired
    UserRepository userRepository;

    @PutMapping("/addClass")
    public void addClass(@Valid @RequestBody AddClassRequest request) {
        classRepository.save(new Class(request.getName(), request.getDescription()));
    }

    @PutMapping("/addLecturer")
    public void addLecturer(@Valid @RequestBody AddLecturerRequest request) {
        lecturerRepository.save(new Lecturer(request.getName(), request.getWebsiteUrl()));
    }

    @PostMapping("/addOpinion")
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
                .build();
        opinionRepository.save(opinion);
    }
}
