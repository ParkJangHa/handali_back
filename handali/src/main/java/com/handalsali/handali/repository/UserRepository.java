package com.handalsali.handali.repository;

import com.handalsali.handali.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    //이메일 중복 확인
    boolean existsByEmail(String email);
    User findByEmail(String email);
    Optional<User> findByUserId(long userId);
}
