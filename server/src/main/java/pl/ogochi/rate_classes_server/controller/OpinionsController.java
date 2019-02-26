package pl.ogochi.rate_classes_server.controller;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.ogochi.rate_classes_server.dao.AddClassRequest;
import pl.ogochi.rate_classes_server.dao.AddLecturerRequest;
import pl.ogochi.rate_classes_server.dao.AddOpinionRequest;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/opinions")
public class OpinionsController {

    @PutMapping("/addClass")
    public void addClass(@Valid @RequestBody AddClassRequest request) {

    }

    @PutMapping("/addLecturer")
    public void addLecturer(@Valid @RequestBody AddLecturerRequest request) {

    }

    @PutMapping("/addOpinion")
    public void addOrUpdateOpinion(@Valid @RequestBody AddOpinionRequest request) {

    }
}
