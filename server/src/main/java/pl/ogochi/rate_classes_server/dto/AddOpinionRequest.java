package pl.ogochi.rate_classes_server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
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

    public AddOpinionRequest() {}
}
