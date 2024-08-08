package com.tinqinacademy.authentication.api.operations.login.output;

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
