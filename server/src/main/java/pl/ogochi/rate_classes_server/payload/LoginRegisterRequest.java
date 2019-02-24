package pl.ogochi.rate_classes_server.payload;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class LoginRegisterRequest {
    @NotBlank
    String email;
    @NotBlank
    String password;
}
