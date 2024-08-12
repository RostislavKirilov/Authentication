package com.tinqinacademy.authentication.api.operations.recoverpass.output;

import com.tinqinacademy.authentication.api.base.OperationOutput;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class RecoverPassOutput implements OperationOutput {

    private String message;
}
