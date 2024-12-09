package com.jwt.service;
import com.jwt.Dto.UserDto;
import com.jwt.Dto.UserRequest;
import com.jwt.Dto.UserResponse;
import com.jwt.Entity.User;
import com.jwt.Enum.Role;
import com.jwt.Repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;

    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    private final PasswordEncoder passwordEncoder;

    public UserResponse save(UserDto userDto) {
        if (userRepository.findByUsername(userDto.getUsername()).isPresent() || userRepository.findByUsername(userDto.getEmail()).isPresent() ) {
            throw new IllegalArgumentException("This username or email is already taken.");
        }
        User user = User.builder()
                .username(userDto.getUsername())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .nameSurname(userDto.getNameSurname())
                .email(userDto.getEmail())
                .role(Role.USER)
                .build();
        userRepository.save(user);

        var token = jwtService.generateToken(user);
        return UserResponse.builder().token(token).build();
    }
    public UserResponse auth(UserRequest userRequest) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userRequest.getUsername(), userRequest.getPassword()));
        User user = userRepository.findByUsername(userRequest.getUsername()).orElseThrow();
        String token = jwtService.generateToken(user);
        return UserResponse.builder().token(token).build();
    }
    public String getTokenFromRequest(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            return token.substring(7);
        }
        return null;
    }
}