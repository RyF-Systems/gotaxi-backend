package com.ryfsystems.ryftaxi.controller;

import com.ryfsystems.ryftaxi.model.UserType;
import com.ryfsystems.ryftaxi.service.UserService;
import com.ryfsystems.ryftaxi.service.UserTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserTypeService userTypeService;
    private final UserService userService;

    @GetMapping("/register-types")
    public ResponseEntity<List<UserType>> getRegisterTypes() {
        List<UserType> userTypes = userTypeService.getAvailableUserTypes();
        return ResponseEntity.ok(userTypes);
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile() {
        return ResponseEntity.ok(userService.getUserProfile());
    }
}
