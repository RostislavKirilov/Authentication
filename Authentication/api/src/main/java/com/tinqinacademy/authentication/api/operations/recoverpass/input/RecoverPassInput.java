package com.tinqinacademy.authentication.api.operations.recoverpass.input;

import com.tinqinacademy.authentication.api.base.OperationInput;
import jakarta.validation.constraints.Email;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class RecoverPassInput implements OperationInput {

    @Email
    private String email;
}
