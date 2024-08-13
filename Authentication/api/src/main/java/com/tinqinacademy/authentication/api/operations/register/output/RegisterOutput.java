package com.tinqinacademy.authentication.api.operations.register.output;

import com.tinqinacademy.authentication.api.base.OperationOutput;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class RegisterOutput implements OperationOutput {

    private String userId;
}
