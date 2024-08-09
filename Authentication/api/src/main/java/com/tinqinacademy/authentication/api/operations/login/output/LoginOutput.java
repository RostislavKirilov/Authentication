package com.tinqinacademy.authentication.api.operations.login.output;

import com.tinqinacademy.authentication.api.base.OperationOutput;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class LoginOutput implements OperationOutput {

    private String jwtToken;
}
