package com.handalsali.handali.repository;

import com.handalsali.handali.DTO.HandaliDTO;
import com.handalsali.handali.DTO.StatDetail;
import com.handalsali.handali.domain.Handali;
import com.handalsali.handali.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HandaliRepository extends JpaRepository<Handali,Long> {
    @Query("SELECT COUNT(p) FROM Handali p WHERE p.user = :userId AND FUNCTION('DATE_FORMAT', p.startDate, '%Y-%m') = FUNCTION('DATE_FORMAT', CURRENT_DATE, '%Y-%m')")
    long countPetsByUserIdAndCurrentMonth(@Param("userId") User user);

    @Query("SELECT h FROM Handali h WHERE h.user.userId = :userId AND MONTH(h.startDate) = :month")
    Optional<Handali> findByIdAndMonth(@Param("userId") Long userId, @Param("month") int month);

    @Query("SELECT new com.handalsali.handali.DTO.StatDetail(s.typeName, s.value) " +
            "FROM Stat s WHERE s.handali.handaliId = :handaliId")
    List<StatDetail> findStatsByHandaliId(@Param("handaliId") Long handaliId);

}

