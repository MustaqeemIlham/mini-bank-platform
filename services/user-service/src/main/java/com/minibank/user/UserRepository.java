package com.minibank.user;

import org.springframework.data.jpa.repository.JpaRepository;

// Spring writes the SQL for us: save(), findById(), findAll()...
// existsByEmail is generated from the method name alone.
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);
}
