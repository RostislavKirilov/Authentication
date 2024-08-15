package com.tinqinacademy.authentication.api.operations.logout.input;

import com.tinqinacademy.authentication.api.base.OperationInput;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LogoutInput implements OperationInput {
    private String token;
}