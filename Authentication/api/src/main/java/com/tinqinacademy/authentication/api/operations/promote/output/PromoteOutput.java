package com.tinqinacademy.authentication.api.operations.promote.output;

import com.tinqinacademy.authentication.api.base.OperationOutput;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PromoteOutput implements OperationOutput {

    private String message;
}
