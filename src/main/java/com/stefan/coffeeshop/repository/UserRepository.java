package com.stefan.coffeeshop.repository;

import com.stefan.coffeeshop.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByPhone(String phone);

    Page<User> findByActiveTrue(Pageable pageable);

    Page<User> findByFullNameContainingIgnoreCaseAndActiveTrue(String name, Pageable pageable);
}
