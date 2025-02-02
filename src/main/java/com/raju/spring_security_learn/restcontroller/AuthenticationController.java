package com.raju.spring_security_learn.restcontroller;


import com.raju.spring_security_learn.entity.AuthenticationResponse;
import com.raju.spring_security_learn.entity.User;
import com.raju.spring_security_learn.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
public class AuthenticationController {
    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody User user){
        return ResponseEntity.ok(authService.register(user));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody User request){
        return ResponseEntity.ok(authService.authentication(request));
    }

    @GetMapping("/active/{id}")
    public ResponseEntity<String> activeUser(@PathVariable("id") long id){
        String response = authService.activeUser(id);
        return ResponseEntity.ok(response);
    }
}
