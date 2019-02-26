package pl.ogochi.rate_classes_server.dto;

import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
public class LoginRegisterRequest {
    @NotBlank
    String email;
    @NotBlank
    String password;
}
