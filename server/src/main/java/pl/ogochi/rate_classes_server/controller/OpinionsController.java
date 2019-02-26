package pl.ogochi.rate_classes_server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import pl.ogochi.rate_classes_server.dto.AddClassRequest;
import pl.ogochi.rate_classes_server.dto.AddLecturerRequest;
import pl.ogochi.rate_classes_server.dto.AddOpinionRequest;
import pl.ogochi.rate_classes_server.dto.OpinionListResponse;
import pl.ogochi.rate_classes_server.model.Lecturer;
import pl.ogochi.rate_classes_server.model.UniveristyClass;
import pl.ogochi.rate_classes_server.model.User;
import pl.ogochi.rate_classes_server.opinion.OpinionManagementService;
import pl.ogochi.rate_classes_server.repository.LecturerRepository;
import pl.ogochi.rate_classes_server.repository.UniveristyClassRepository;
import pl.ogochi.rate_classes_server.repository.UserRepository;
import pl.ogochi.rate_classes_server.security.UserPrincipal;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/opinions")
@CrossOrigin
public class OpinionsController {
    @Autowired
    UniveristyClassRepository univeristyClassRepository;
    @Autowired
    LecturerRepository lecturerRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    OpinionManagementService opinionManagementService;

    @PutMapping("/addClass")
    @RolesAllowed("ROLE_USER")
    public void addClass(@Valid @RequestBody AddClassRequest request) {
        univeristyClassRepository.save(new UniveristyClass(request.getName(), request.getDescription()));
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
        opinionManagementService.createOrUpdateOpinion(request, getCurrentUser());
    }

    @GetMapping
    @RolesAllowed("ROLE_USER")
    public OpinionListResponse getOpinions(@RequestParam String className) {
        return opinionManagementService.getOpinionsListWithHiddenDetails(className, getCurrentUser());
    }

    @PostMapping("/switchOpinionLike")
    @RolesAllowed("ROLE_USER")
    @Transactional
    public void switchOpinionLike(@RequestParam String opinionId) {
        opinionManagementService.switchLikeForOpinion(opinionId, getCurrentUser());
    }

    @GetMapping("/classes")
    public List<UniveristyClass> getClasses() {
        return univeristyClassRepository.findAll();
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
