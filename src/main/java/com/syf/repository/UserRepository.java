package com.syf.repository;

import com.syf.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users,String> {
    Optional<Users> findByUserId(Long userId);
    Optional<Users> findByUsername(String username);
    void deleteByUserId(Long userId);
}

