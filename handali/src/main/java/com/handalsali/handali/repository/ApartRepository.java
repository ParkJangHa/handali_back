package com.handalsali.handali.repository;

import com.handalsali.handali.domain.Apart;
import com.handalsali.handali.enums_multyKey.ApartId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ApartRepository extends JpaRepository<Apart, ApartId> { // ✅ 복합 키 사용
    @Query("SELECT a FROM Apart a ORDER BY a.apartId.apartId DESC LIMIT 1")
    Apart findLatestApartment();
}
