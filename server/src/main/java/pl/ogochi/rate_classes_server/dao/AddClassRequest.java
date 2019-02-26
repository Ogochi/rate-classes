package pl.ogochi.rate_classes_server.dao;

import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
public class AddClassRequest {
    @NotBlank
    String name;
    @NotNull
    String description;
}
