package pl.ogochi.rate_classes_server.dao;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;

public class AddOpinionRequest {
    @NotBlank
    String className;
    @NotBlank
    String lecturerName;

    @NotBlank
    @DecimalMin("1")
    @DecimalMax("5")
    String rating;

    @NotBlank
    String text;
}
