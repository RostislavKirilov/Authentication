package com.tinqinacademy.authentication.operations.login.input;

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
