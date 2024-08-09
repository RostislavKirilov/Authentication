package com.tinqinacademy.authentication.api.operations.login.input;

import com.tinqinacademy.authentication.api.base.OperationInput;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class LoginInput implements OperationInput {

    private String username;
    private String password;
}
