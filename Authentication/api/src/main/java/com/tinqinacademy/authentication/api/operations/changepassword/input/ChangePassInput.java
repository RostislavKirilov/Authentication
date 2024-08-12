package com.tinqinacademy.authentication.api.operations.changepassword.input;

import com.tinqinacademy.authentication.api.base.OperationInput;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ChangePassInput implements OperationInput {

    @NotNull
    private String oldPassword;
    @NotNull
    private String newPassword;
    @Email
    private String email;
}
