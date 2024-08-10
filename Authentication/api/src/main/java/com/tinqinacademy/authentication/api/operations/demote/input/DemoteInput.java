package com.tinqinacademy.authentication.api.operations.demote.input;

import com.tinqinacademy.authentication.api.base.OperationInput;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class DemoteInput implements OperationInput {


    private String userId;
}
