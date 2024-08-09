package com.tinqinacademy.authentication.api.operations.promote.input;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class PromoteInput {

    private UUID userId;
}
