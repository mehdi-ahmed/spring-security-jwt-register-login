package com.mytutorials.spring.springdockerjwt.auth;

import com.mytutorials.spring.springdockerjwt.config.JwtService;
import com.mytutorials.spring.springdockerjwt.user.User;
import com.mytutorials.spring.springdockerjwt.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) {

        // save user to DB
        // Return a token wrapped in AuthenticationResponse
        User user = User
                .builder()
                .firstName(request.getFirstname())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .pwd(passwordEncoder.encode(request.getPwd()))
                .role(User.Role.USER)
                .build();

        userRepository.save(user);

         return AuthenticationResponse.builder()
                .token(jwtService.generateToken(user))
                .build();
    }

    public AuthenticationResponse login(AuthenticationRequest request) {

        // if User is not authenticated
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPwd()
                )
        );

        // if User is authenticated, i,e email + pwd are correct
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(null);

        return AuthenticationResponse.builder()
                .token(jwtService.generateToken(user))
                .build();
    }
}
