package com.nc.Xmlxsdvalidate.controller;

import com.nc.Xmlxsdvalidate.dto.UserDto;
import com.nc.Xmlxsdvalidate.service.UserService;
import com.nc.Xmlxsdvalidate.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findById(id));
    }


    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(userService.findAll());
    }

    @PostMapping
    public ResponseEntity<UserDto> createUser(@RequestBody UserDto userDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.save(userDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/process")
    public ResponseEntity<String> processUser(@RequestBody UserDto userDto, @RequestParam String xsdContent) {
        try {
            ((UserServiceImpl) userService).processUserData(userDto, xsdContent);
            return ResponseEntity.ok("Traitement réussi");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur lors du traitement : " + e.getMessage());
        }
    }
}
