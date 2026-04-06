//package com.checkmate.backend.controller;
//
//import com.checkmate.backend.entity.AppUser;
//import com.checkmate.backend.entity.User;
//import com.checkmate.backend.repository.AppUserRepository;
//import com.checkmate.backend.repository.UserRepository;
//import com.checkmate.backend.security.JwtUtil;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/api/auth")
//@CrossOrigin("*") // Angular se request aane dega
//public class AuthController {
//
//    @Autowired
//    private AuthenticationManager authenticationManager;
//
//    @Autowired
//    private JwtUtil jwtUtil;
//
//    @Autowired
//    private AppUserRepository userRepository;
//
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//
//    // 1. REGISTER API (Database mein pehla user banane ke liye)
//    @PostMapping("/register")
//    public ResponseEntity<?> registerUser(@RequestBody AppUser user) {
//        // Check karo ki email pehle se toh nahi hai
//        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
//            return ResponseEntity.badRequest().body("Error: Email pehle se registered hai!");
//        }
//
//        // Password ko hash (encrypt) karo aur database mein save karo
//        user.setPassword(passwordEncoder.encode(user.getPassword()));
//        
//        // Agar role nahi diya, toh default 'USER' bana do
//        if(user.getRole() == null || user.getRole().isEmpty()){
//            user.setRole("USER");
//        }
//        
//        userRepository.save(user);
//        return ResponseEntity.ok("User successfully register ho gaya!");
//    }
//
//    // 2. LOGIN API (Token lene ke liye)
//    @PostMapping("/login")
//    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> loginData) {
//        String email = loginData.get("email");
//        String password = loginData.get("password");
//
//        try {
//            // Spring Security ko bolo password check karne ko
//            authenticationManager.authenticate(
//                    new UsernamePasswordAuthenticationToken(email, password)
//            );
//        } catch (Exception e) {
//            return ResponseEntity.status(401).body("Error: Email ya Password galat hai!");
//        }
//
//        // Agar password sahi hai, toh database se user nikalo role pata karne ke liye
//        AppUser user = userRepository.findByEmail(email).get();
//
//        // Machine se naya Token (ID Card) banwao
//        String token = jwtUtil.generateToken(email, user.getRole());
//
//        // Token aur Role frontend ko bhej do
//        Map<String, String> response = new HashMap<>();
//        response.put("token", token);
//        response.put("role", user.getRole());
//        response.put("message", "Login Successful!");
//
//        return ResponseEntity.ok(response);
//    }
//}




package com.checkmate.backend.controller;

import com.checkmate.backend.entity.AppUser;
import com.checkmate.backend.repository.AppUserRepository;
import com.checkmate.backend.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin("*") 
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AppUserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody AppUser user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            // 🛑 English update
            return ResponseEntity.badRequest().body("Error: Email is already registered!");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        if(user.getRole() == null || user.getRole().isEmpty()){
            user.setRole("USER");
        }
        
        userRepository.save(user);
        // 🛑 English update
        return ResponseEntity.ok("User registered successfully!");
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> loginData) {
        String email = loginData.get("email");
        String password = loginData.get("password");

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );
        } catch (Exception e) {
            // 🛑 English update
            return ResponseEntity.status(401).body("Error: Invalid Email or Password!");
        }

        AppUser user = userRepository.findByEmail(email).get();
        String token = jwtUtil.generateToken(email, user.getRole());

        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        response.put("role", user.getRole());
        // 🛑 English update
        response.put("message", "Login Successful!");

        return ResponseEntity.ok(response);
    }
}
