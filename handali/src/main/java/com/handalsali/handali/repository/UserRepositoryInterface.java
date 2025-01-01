package com.handalsali.handali.repository;

import com.handalsali.handali.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepositoryInterface extends JpaRepository<User,Long> {
    //이메일 중복 확인
    boolean existsByEmail(String email);
}
