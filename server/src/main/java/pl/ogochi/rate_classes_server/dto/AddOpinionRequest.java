package pl.ogochi.rate_classes_server.dto;

import lombok.Getter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
public class AddOpinionRequest {
    @NotBlank
    String className;
    @NotBlank
    String lecturerName;

    @NotNull
    @Min(1)
    @Max(5)
    int rating;

    @NotBlank
    String text;
}
