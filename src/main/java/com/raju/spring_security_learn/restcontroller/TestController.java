package com.raju.spring_security_learn.restcontroller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/")
    public ResponseEntity<String> activeUser(){
        return ResponseEntity.ok("Response Successfully");
    }
}
