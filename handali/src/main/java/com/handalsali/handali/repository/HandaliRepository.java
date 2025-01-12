package com.handalsali.handali.repository;

import com.handalsali.handali.domain.Handali;
import com.handalsali.handali.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface HandaliRepository extends JpaRepository<Handali,Long> {
    @Query("SELECT COUNT(p) FROM Handali p WHERE p.user = :userId AND FUNCTION('DATE_FORMAT', p.startDate, '%Y-%m') = FUNCTION('DATE_FORMAT', CURRENT_DATE, '%Y-%m')")
    long countPetsByUserIdAndCurrentMonth(@Param("userId") User user);
}
