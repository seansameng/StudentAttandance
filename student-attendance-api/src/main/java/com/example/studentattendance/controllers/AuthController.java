package com.example.studentattendance.controllers;

import com.example.studentattendance.dto.AuthDto;
import com.example.studentattendance.models.User;
import com.example.studentattendance.security.JwtUtil;
import com.example.studentattendance.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private UserService userService;
    
    @PostMapping("/login")
    public ResponseEntity<AuthDto.AuthResponse> login(@Valid @RequestBody AuthDto.LoginRequest request) {
        try {
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
            
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = userService.findByUsername(userDetails.getUsername());
            
            // Generate tokens
            String accessToken = jwtUtil.generateToken(userDetails);
            String refreshToken = jwtUtil.generateRefreshToken(userDetails);
            
            // Update last login
            userService.updateLastLogin(user.getId());
            
            // Create response
            AuthDto.AuthResponse response = new AuthDto.AuthResponse();
            response.setAccessToken(accessToken);
            response.setRefreshToken(refreshToken);
            response.setTokenType("Bearer");
            response.setExpiresIn(86400000L); // 24 hours
            response.setUser(user);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/register")
    public ResponseEntity<AuthDto.AuthResponse> register(@Valid @RequestBody AuthDto.RegisterRequest request) {
        try {
            // Check if user already exists
            if (userService.existsByUsername(request.getUsername())) {
                return ResponseEntity.badRequest().build();
            }
            
            if (userService.existsByEmail(request.getEmail())) {
                return ResponseEntity.badRequest().build();
            }
            
            // Create new user
            User user = userService.createUser(request);
            
            // Generate tokens
            UserDetails userDetails = userService.loadUserByUsername(user.getUsername());
            String accessToken = jwtUtil.generateToken(userDetails);
            String refreshToken = jwtUtil.generateRefreshToken(userDetails);
            
            // Create response
            AuthDto.AuthResponse response = new AuthDto.AuthResponse();
            response.setAccessToken(accessToken);
            response.setRefreshToken(refreshToken);
            response.setTokenType("Bearer");
            response.setExpiresIn(86400000L); // 24 hours
            response.setUser(user);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/refresh")
    public ResponseEntity<AuthDto.AuthResponse> refreshToken(@RequestHeader("Authorization") String refreshToken) {
        try {
            String token = jwtUtil.extractTokenFromHeader(refreshToken);
            if (token == null || !jwtUtil.validateToken(token)) {
                return ResponseEntity.badRequest().build();
            }
            
            String username = jwtUtil.extractUsername(token);
            UserDetails userDetails = userService.loadUserByUsername(username);
            
            // Generate new tokens
            String newAccessToken = jwtUtil.generateToken(userDetails);
            String newRefreshToken = jwtUtil.generateRefreshToken(userDetails);
            
            // Create response
            AuthDto.AuthResponse response = new AuthDto.AuthResponse();
            response.setAccessToken(newAccessToken);
            response.setRefreshToken(newRefreshToken);
            response.setTokenType("Bearer");
            response.setExpiresIn(86400000L); // 24 hours
            response.setUser(userService.findByUsername(username));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String token) {
        // In a real application, you might want to blacklist the token
        // For now, we'll just return success
        return ResponseEntity.ok().build();
    }
}
