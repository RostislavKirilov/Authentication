package com.tinqinacademy.authentication.api.operations.changepassword.output;

import com.tinqinacademy.authentication.api.base.OperationOutput;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ChangePassOutput implements OperationOutput {

    private String message;
}
