package com.neo.tech.whatsappPoc.user.controller;


import com.neo.tech.whatsappPoc.user.dto.CreateUserRequest;
import com.neo.tech.whatsappPoc.user.dto.UserResponse;
import com.neo.tech.whatsappPoc.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;




    @PostMapping("/manager")
    public ResponseEntity<String> createManager(
            @RequestParam String mobile,
            @RequestParam String name
    ) {
        userService.createManager(mobile, name);
        return ResponseEntity.ok("Manager created successfully");
    }

    // ---------------- EXECUTOR ----------------

    @PostMapping("/executor")
    public ResponseEntity<String> createExecutor(
            @RequestParam String mobile,
            @RequestParam String name,
            @RequestParam String branchCode,
            @RequestParam String branchName
    ) {
        userService.createExecutor(mobile, name, branchCode, branchName);
        return ResponseEntity.ok("Executor created successfully");
    }
}

