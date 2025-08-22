package com.handalsali.handali.repository;

import com.handalsali.handali.domain.Handbook;
import com.handalsali.handali.domain.User;
import com.handalsali.handali.domain.UserHandbook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserHandbookRepository extends JpaRepository<UserHandbook, Long> {
    boolean existsByUserAndHandbook(User user, Handbook handbook);

    /**
     * [도감 조회]
     */
    List<UserHandbook> findAllByUser(User user);
}
