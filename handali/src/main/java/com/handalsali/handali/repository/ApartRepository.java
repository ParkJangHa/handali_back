package com.handalsali.handali.repository;

import com.handalsali.handali.domain.Apart;
import com.handalsali.handali.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApartRepository extends JpaRepository<Apart, Long> {
    // 이미 있는 아파트 조회 (올바른 ID 타입 확인)
    Optional<Apart> findById(Long apartId);

    Optional<Apart> findByApartIdAndFloorAndUser(int apartId, int floor, User user);
}
