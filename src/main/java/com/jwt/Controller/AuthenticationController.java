package com.jwt.Controller;

import com.jwt.Dto.UserDto;
import com.jwt.Dto.UserRequest;
import com.jwt.Dto.UserResponse;
import com.jwt.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/login/save")
    public ResponseEntity<UserResponse> save(@RequestBody UserDto userDto) {
        return ResponseEntity.ok(authenticationService.save(userDto));
    }

    @PostMapping("/login/auth")
    public ResponseEntity<UserResponse> auth(@RequestBody UserRequest userRequest) {
        return ResponseEntity.ok(authenticationService.auth(userRequest));
    }

//    @PostMapping("/logout")
//    public ResponseEntity<?> logout(HttpServletRequest request) {
//        String token = authenticationService.getTokenFromRequest(request);
//        return ResponseEntity.ok("Logged out successfully");
//    }
}