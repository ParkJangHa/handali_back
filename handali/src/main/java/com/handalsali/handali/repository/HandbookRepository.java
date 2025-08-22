package com.handalsali.handali.repository;

import com.handalsali.handali.domain.Handbook;
import com.handalsali.handali.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HandbookRepository extends JpaRepository<Handbook, Long> {
    Handbook findByCode(String code);
}
