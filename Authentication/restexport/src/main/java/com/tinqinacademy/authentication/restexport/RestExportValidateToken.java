package com.tinqinacademy.authentication.restexport;

import feign.Headers;
import feign.RequestLine;
import org.springframework.cloud.openfeign.FeignClient;

@Headers({
        "Content-Type:application/json"
})
@FeignClient(name = "authentication-service", url = "${authentication.service.url}")
public interface RestExportValidateToken {

    @RequestLine("POST /auth/validate-token")
    boolean validateToken(String token);
    @RequestLine("POST /auth/get-username")
    String getUsernameFromToken(String token);

    @RequestLine("POST /auth/get-role")
    String getRoleFromToken(String token);
}
