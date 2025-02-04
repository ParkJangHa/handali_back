package com.handalsali.handali.repository;

import com.handalsali.handali.DTO.HandaliDTO;
import com.handalsali.handali.DTO.StatDetailDTO;
import com.handalsali.handali.domain.Handali;
import com.handalsali.handali.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface HandaliRepository extends JpaRepository<Handali,Long> {
    @Query("SELECT COUNT(p) FROM Handali p WHERE p.user = :userId AND FUNCTION('DATE_FORMAT', p.startDate, '%Y-%m') = FUNCTION('DATE_FORMAT', CURRENT_DATE, '%Y-%m')")
    long countPetsByUserIdAndCurrentMonth(@Param("userId") User user);

    @Query("select h from Handali h where function('DATE_FORMAT',h.startDate,'%Y-%m')=function('DATE_FORMAT',CURRENT_DATE,'%Y-%m')" +
            "and h.user=:user")
    Handali findHandaliByCurrentDateAndUser(@Param("user") User user);

    /**handali_id에 대한 스탯 정보 조회 / 01.30*/
    @Query("SELECT new com.handalsali.handali.DTO.StatDetailDTO(s.typeName, s.value) " +
            "FROM Stat s join HandaliStat hs on hs.stat=s " +
            "WHERE hs.handali.handaliId = :handaliId")
    List<StatDetailDTO> findStatsByHandaliId(@Param("handaliId") Long handaliId);
}
