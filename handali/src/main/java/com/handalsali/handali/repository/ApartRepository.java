package com.handalsali.handali.repository;

import com.handalsali.handali.domain.Apart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApartRepository extends JpaRepository<Apart, ApartId> {
    // 이미 있는 아파트 조회 (올바른 ID 타입 확인)
    Optional<Apart> findById(ApartId apartId);

    // 특정 연도의 최신 아파트 조회
    @Query("SELECT a FROM Apart a WHERE a.apartId= :year ORDER BY a.floor DESC LIMIT 1")
    Optional<Apart> findLatestApartmentByYear(@Param("year") Long year);

    Optional<Apart> findByApartIdAndFloor(int year, int month);
}
