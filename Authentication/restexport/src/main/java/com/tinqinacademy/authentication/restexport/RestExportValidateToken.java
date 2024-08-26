package com.tinqinacademy.authentication.restexport;

import com.tinqinacademy.authentication.api.getrole.input.GetRoleInput;
import com.tinqinacademy.authentication.api.getrole.output.GetRoleOutput;
import com.tinqinacademy.authentication.api.getusername.input.GetUsernameInput;
import com.tinqinacademy.authentication.api.getusername.output.GetUsernameOutput;
import com.tinqinacademy.authentication.api.operations.validatetoken.input.ValidateTokenInput;
import feign.Headers;
import feign.RequestLine;
import org.springframework.cloud.openfeign.FeignClient;

@Headers({
        "Content-Type:application/json"
})
public interface RestExportValidateToken {

    @RequestLine("POST /auth/validate-token")
    boolean validateToken( ValidateTokenInput input);
    @RequestLine("POST /auth/get-username")
    GetUsernameOutput getUsernameFromToken( GetUsernameInput input);

    @RequestLine("POST /auth/get-role")
    GetRoleOutput getRoleFromToken( GetRoleInput input);
}
