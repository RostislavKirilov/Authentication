package com.tinqinacademy.authentication.operations.login.output;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class LoginOutput {

    private String jwtToken;
}
