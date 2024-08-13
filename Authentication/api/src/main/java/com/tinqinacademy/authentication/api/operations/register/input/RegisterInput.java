package com.tinqinacademy.authentication.api.operations.register.input;

import com.tinqinacademy.authentication.api.base.OperationInput;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class RegisterInput implements OperationInput {

    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
}
