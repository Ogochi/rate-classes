package pl.ogochi.rate_classes_server.dao;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ChangePasswordRequest {
    @NotBlank
    String currentPassword;
    @NotBlank
    String newPassword;
}
