package com.minibank.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

// The "front counter": turns HTTP requests into method calls, and results into JSON
@RestController
@RequestMapping("/api/users")
public class UserController {

    // The JSON body of POST /api/users, e.g. {"name": "Ali", "email": "ali@mail.com"}
    public record CreateUserRequest(@NotBlank String name, @NotBlank @Email String email) {}

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED) // 201 instead of the default 200
    public User create(@Valid @RequestBody CreateUserRequest request) { // @Valid -> 400 if the checks fail
        return service.create(request.name(), request.email());
    }

    @GetMapping("/{id}")
    public User get(@PathVariable Long id) {
        return service.get(id);
    }
}
