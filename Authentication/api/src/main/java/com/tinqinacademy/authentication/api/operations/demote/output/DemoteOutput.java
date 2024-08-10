package com.tinqinacademy.authentication.api.operations.demote.output;

import com.tinqinacademy.authentication.api.base.OperationOutput;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class DemoteOutput implements OperationOutput {

    private String message;
}
