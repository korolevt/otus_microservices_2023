package org.kt.hw.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class User {
    int id;
    String firstName;
    String lastName;
    String email;
}
