package com.jaybhararia.playmate.auth;

import com.jaybhararia.playmate.auth.dto.LogInRequest;
import com.jaybhararia.playmate.auth.dto.LogInResponse;
import com.jaybhararia.playmate.auth.jwt.JwtUtils;
import io.jsonwebtoken.Jwt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    UserDetailsManager userDetailsManager;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtUtils jwtService;

    @Autowired
    AuthenticationManager authenticationManager;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody LogInRequest logInRequest){
        if (userDetailsManager.userExists(logInRequest.username())){
            return new ResponseEntity<>("Username is Already taken",HttpStatus.CONFLICT);
        }
        UserDetails userDetails = User.withUsername(logInRequest.username())
                .password(passwordEncoder.encode(logInRequest.password()))
                .roles("USER")
                .build();

        userDetailsManager.createUser(userDetails);

        return new ResponseEntity<>(userDetails, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LogInRequest logInRequest){
        try{
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(logInRequest.username(), logInRequest.password()));
        } catch (Exception e){
            return new ResponseEntity<>("Authentication Failed", HttpStatus.UNAUTHORIZED);
        }
        String authenticatedUsername = logInRequest.username();
        String authToken = jwtService.generateTokenFromUsername(authenticatedUsername);
        LogInResponse logInResponse = new LogInResponse(authToken);
        return new ResponseEntity<>(logInResponse, HttpStatus.OK);
    }
}
