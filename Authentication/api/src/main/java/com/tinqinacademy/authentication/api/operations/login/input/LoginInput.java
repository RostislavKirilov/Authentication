package com.tinqinacademy.authentication.api.operations.login.input;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class LoginInput {

    private String username;
    private String password;
}
