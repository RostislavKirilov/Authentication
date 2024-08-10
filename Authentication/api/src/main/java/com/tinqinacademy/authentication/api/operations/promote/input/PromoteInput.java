package com.tinqinacademy.authentication.api.operations.promote.input;

import com.tinqinacademy.authentication.api.base.OperationInput;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class PromoteInput implements OperationInput {

    private UUID userId;
}
