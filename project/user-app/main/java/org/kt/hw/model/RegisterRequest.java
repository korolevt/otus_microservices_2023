package org.kt.hw.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RegisterRequest {
    @NotNull
    private String login;

    @NotNull
    private String password;

    private String firstName;
    private String lastName;
    private String email;
}
