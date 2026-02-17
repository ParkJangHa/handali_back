package com.handalsali.handali.repository;

import com.handalsali.handali.DTO.StatDetailDTO;
import com.handalsali.handali.domain.Handali;
import com.handalsali.handali.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface HandaliRepository extends JpaRepository<Handali,Long> {
    @Query("SELECT COUNT(p) FROM Handali p WHERE p.user = :userId AND FUNCTION('DATE_FORMAT', p.startDate, '%Y-%m') = FUNCTION('DATE_FORMAT', CURRENT_DATE, '%Y-%m')")
    long countPetsByUserIdAndCurrentMonth(@Param("userId") User user);

    /** 시작일 기준으로 내림차순 정렬하여 가장 최신 한달이 1개 조회 **/
    @Query("SELECT h FROM Handali h WHERE h.user.userId = :userId ORDER BY h.startDate DESC LIMIT 1")
    Optional<Handali> findLatestHandaliByUser(@Param("userId") Long userId);

    /**직업 및 아파트에 입주하지 않고, 해당 달에 해당하는 한달이들 조회*/
    @Query("SELECT h FROM Handali h WHERE h.startDate >= :startOfMonth AND h.startDate < :endOfMonth " +
            "AND h.job IS NULL AND h.apart IS NULL")
    List<Handali> findUnemployedHandalisForMonth(
            @Param("startOfMonth") LocalDate startOfMonth,
            @Param("endOfMonth") LocalDate endOfMonth
    );

    /** 최신 한달이 1개 반환 (findHandaliByCurrentMonth를 기반으로) **/
    @Query("SELECT h FROM Handali h WHERE h.user.userId = :userId " +
            "AND YEAR(h.startDate) = YEAR(CURRENT_DATE) " +
            "AND MONTH(h.startDate) = MONTH(CURRENT_DATE) " +
            "ORDER BY h.startDate DESC")
    Handali findLatestHandaliByCurrentMonth(@Param("userId") Long userId);

    //아파트에 입주한 모든 한달이 조회
    @Query("SELECT h FROM Handali h " +
            "JOIN FETCH h.apart " +
            "JOIN FETCH h.job " +
            "WHERE h.user = :user " +
            "AND h.apart IS NOT NULL")
    List<Handali> findAllByUserWithApartAndJob(@Param("user") User user);

    /**지난달 한달이 찾기*/
    @Query("select h from Handali h " +
            "where h.startDate >=:startDate " +
            "and h.startDate <=:endDate " +
            "and h.user=:user")
    Handali findLastMonthHandali(@Param("user") User user,@Param("startDate")LocalDate startDate, @Param("endDate")LocalDate endDate);

    /**직업을 가진 한달이 조회*/
    @Query("select h from Handali h " +
            "where h.job is not null")
    List<Handali> findAllByJobIsNotNull();

    /**사용자별 직업을 가진 한달이 조회*/
    @Query("SELECT h FROM Handali h " +
            "JOIN FETCH h.job " +
            "WHERE h.user = :user " +
            "AND h.job IS NOT NULL")
    List<Handali> findByUserAndJobIsNotNullWithJob(@Param("user") User user);
}
