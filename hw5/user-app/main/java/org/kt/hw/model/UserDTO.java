package org.kt.hw.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserDTO {
    @NotNull
    private String name;

    private String password;

    private String email;
}
