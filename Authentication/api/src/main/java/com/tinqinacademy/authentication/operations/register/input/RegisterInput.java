package com.tinqinacademy.authentication.operations.register.input;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class RegisterInput {

    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
}
