package com.handalsali.handali.repository;

import com.handalsali.handali.DTO.HandaliDTO;
import com.handalsali.handali.DTO.StatDetailDTO;
import com.handalsali.handali.domain.Apart;
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

    @Query("select h from Handali h WHERE FUNCTION('DATE_FORMAT',h.startDate,'%Y-%m') =function('DATE_FORMAT',CURRENT_DATE,'%Y-%m')" +
            "and h.user=:user ORDER BY h.startDate DESC")
    List<Handali> findHandaliListByCurrentDateAndUser(@Param("user") User user);

    default Handali findLatestHandaliByCurrentDateAndUser(User user) {
        List<Handali> handalis = findHandaliListByCurrentDateAndUser(user);
        return handalis.isEmpty() ? null : handalis.get(0); // 가장 최근 생성된 한달이 반환
    }

    //handali_id를 이용한 스탯 정보 조회 / 02.06 수정

    @Query("SELECT new com.handalsali.handali.DTO.StatDetailDTO(s.typeName, s.value) " +
            "FROM Stat s join HandaliStat hs on hs.stat=s " +
            "WHERE hs.handali.handaliId = :handaliId")
    List<StatDetailDTO> findStatsByHandaliId(@Param("handaliId") Long handaliId);

    // 아파트 입주 한달이 조회
    @Query("SELECT h FROM Handali h JOIN FETCH h.job WHERE h.apart.apartId.apartId = :apartId ORDER BY h.apart.apartId.floor ASC")
    List<Handali> findByApartId(@Param("apartId") int apartId);

    //아파트에 입주한 모든 한달이 조회
    @Query("SELECT h FROM Handali h WHERE h.apart IS NOT NULL")
    List<Handali> findAllHandalisInApartments();

    //가장 최신 아파트 조회
    @Query("SELECT a FROM Apart a ORDER BY a.apartId.apartId DESC")
    List<Apart> findLatestApartmentList();

    default Apart findLatestApartment() {
        List<Apart> latestApartments = findLatestApartmentList();
        return latestApartments.isEmpty() ? null : latestApartments.get(0);
    }

    // 특정 아파트에 입주한 한달이 수 조회
    @Query("SELECT COUNT(h) FROM Handali h WHERE h.apart.apartId.apartId = :apartId")
    int countHandalisInApartment(@Param("apartId") int apartId);
}
