package com.handalsali.handali.repository;

import com.handalsali.handali.domain.Handali;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HandaliRepository extends JpaRepository<Handali,Long> {
}
