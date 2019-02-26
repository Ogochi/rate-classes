package pl.ogochi.rate_classes_server.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import pl.ogochi.rate_classes_server.model.Opinion;
import pl.ogochi.rate_classes_server.model.User;
import pl.ogochi.rate_classes_server.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class OpinionListResponse {
    private List<OpinionDTO> opinions;
    @JsonIgnore
    private UserRepository userRepository;
    @JsonIgnore
    private String userEmail;

    public OpinionListResponse (List<Opinion> opinions, String userEmail, UserRepository userRepository) {
        this.userRepository = userRepository;
        this.userEmail = userEmail;

        this.opinions = opinions.stream().map(OpinionDTO::new).collect(Collectors.toList());
    }

    @Data
    private class OpinionDTO {
        private String lecturer;
        private String text;
        private int rating;
        private int popularity;
        private boolean isAuthoredByMe = false;
        private boolean isLikedByMe = false;

        OpinionDTO(Opinion opinion) {
            this.lecturer = opinion.getLecturer().getName();
            this.text = opinion.getText();
            this.rating = opinion.getRating();
            this.popularity = opinion.getPopularity();

            User user = userRepository.getUserByEmail(userEmail);
            if (opinion.getAuthor().getEmail().equals(userEmail)) {
                isAuthoredByMe = true;
            }
            if (user.getLikedOpinions().contains(opinion)) {
                isLikedByMe = true;
            }
        }
    }
}
