package pl.ogochi.rate_classes_server.dao;

import javax.validation.constraints.NotBlank;

public class AddClassRequest {
    @NotBlank
    String name;
}
