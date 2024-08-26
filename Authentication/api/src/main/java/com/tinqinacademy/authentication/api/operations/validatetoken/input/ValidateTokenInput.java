package com.tinqinacademy.authentication.api.operations.validatetoken.input;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class ValidateTokenInput {
    private String token;
}
