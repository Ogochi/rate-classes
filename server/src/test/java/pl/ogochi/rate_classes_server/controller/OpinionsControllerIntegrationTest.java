package pl.ogochi.rate_classes_server.controller;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import pl.ogochi.rate_classes_server.dto.AddOpinionRequest;
import pl.ogochi.rate_classes_server.exception.UniveristyClassNotFoundException;
import pl.ogochi.rate_classes_server.model.Lecturer;
import pl.ogochi.rate_classes_server.model.UniveristyClass;
import pl.ogochi.rate_classes_server.model.User;
import pl.ogochi.rate_classes_server.opinion.OpinionManagementService;
import pl.ogochi.rate_classes_server.repository.LecturerRepository;
import pl.ogochi.rate_classes_server.repository.OpinionRepository;
import pl.ogochi.rate_classes_server.repository.UniveristyClassRepository;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class OpinionsControllerIntegrationTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    LecturerRepository lecturerRepository;
    @Autowired
    UniveristyClassRepository univeristyClassRepository;
    @Autowired
    OpinionRepository opinionRepository;
    @Autowired
    OpinionManagementService opinionManagementService;

    @Test
    @WithMockUser
    public void getsLecturersList() throws Exception {
        // given
        Lecturer lecturer = new Lecturer("Adam", "http://adam.pl");
        lecturerRepository.save(lecturer);


        // when
        ResultActions resultActions = mvc.perform(get("/api/opinions/lecturers"));
        JSONArray jsonResponse = new JSONArray(resultActions.andReturn().getResponse().getContentAsString());

        // then
        resultActions.andExpect(status().isOk());
        assertEquals(1, jsonResponse.length());
        assertEquals(lecturer.getName(), new JSONObject(jsonResponse.get(0).toString()).get("name"));
    }

    @Test
    public void addCorrectOpinion() {
        // given
        UniveristyClass univeristyClass = new UniveristyClass("class_name", "class_desc");
        Lecturer lecturer = new Lecturer("Adam", "http://adam.pl");
        AddOpinionRequest addOpinionRequest = new AddOpinionRequest(univeristyClass.getName(), lecturer.getName(), 5, "empty_text");
        User user = new User("user@user.pl", "aaa", Arrays.asList("ROLE_USER"));
        univeristyClassRepository.save(univeristyClass);
        lecturerRepository.save(lecturer);

        // when
        opinionManagementService.createOrUpdateOpinion(addOpinionRequest, user);

        // then
        assertEquals(1, opinionRepository.countByAuthorEmail(user.getEmail()));
        assertEquals(1, opinionRepository.count());
        assertEquals(lecturer, opinionRepository.findAll().get(0).getLecturer());
        assertEquals(univeristyClass, opinionRepository.findAll().get(0).getUniveristyClass());
    }

    @Test(expected = UniveristyClassNotFoundException.class)
    public void addOpinionWithIncorrectClass() {
        // given
        UniveristyClass univeristyClass = new UniveristyClass("class_name", "class_desc");
        Lecturer lecturer = new Lecturer("Adam", "http://adam.pl");
        AddOpinionRequest addOpinionRequest = new AddOpinionRequest(univeristyClass.getName(), lecturer.getName(), 5, "empty_text");
        User user = new User("user@user.pl", "aaa", Arrays.asList("ROLE_USER"));
        lecturerRepository.save(lecturer);

        // when
        opinionManagementService.createOrUpdateOpinion(addOpinionRequest, user);

        // then
        // Exception thrown
    }
}