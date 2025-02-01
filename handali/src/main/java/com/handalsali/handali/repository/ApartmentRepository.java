package com.handalsali.handali.repository;

import com.handalsali.handali.domain.Apartment;
import com.handalsali.handali.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApartmentRepository extends JpaRepository<Apartment, Long> {
    Optional<Apartment> findTopByOrderByApartIdDesc();

    List<Apartment> findByUser(User user);  // 사용자별 아파트 조회

}