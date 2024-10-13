package org.example.gateway.feign;

import org.example.gateway.dto.UserValidationRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "Userservice", url = "http://localhost:8081")
public interface UserServiceClient {

    @PostMapping("/user/reg")
    void regUser(@RequestBody UserValidationRequest userValidationRequest);

    @PostMapping("/user/validate")
    Boolean validateUser(@RequestBody UserValidationRequest request);

    @GetMapping("/user/find/{username}")
    UserValidationRequest getUserByUsername(@PathVariable("username") String username);

}
