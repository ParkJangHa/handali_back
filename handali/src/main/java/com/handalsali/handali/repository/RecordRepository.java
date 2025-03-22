package com.handalsali.handali.repository;

import com.handalsali.handali.domain.Habit;
import com.handalsali.handali.domain.Record;
import com.handalsali.handali.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface RecordRepository extends JpaRepository<Record, Long> {
    boolean existsByHabitAndDateAndUser(Habit habit, LocalDate date, User user);

    @Query("select count(r) from Record r " +
            "where r.habit=:habit and " +
            "r.user=:user and " +
            "r.date >=:startDate and " +
            "r.date <=:endDate")
    int countByUserAndHabitAndDate(@Param("user")User user,@Param("habit")Habit habit,@Param("startDate")LocalDate startDate,@Param("endDate")LocalDate endDate);

    Record findTopByUserAndHabitOrderByDateDesc(User user, Habit habit);

    @Query("select count(r) from Record r " +
            "where r.user=:user and " +
            "r.date >=:startDate and " +
            "r.date <=:endDate")
    int countByUserAndDate(@Param("user")User user,@Param("startDate")LocalDate startDate,@Param("endDate")LocalDate endDate);
}
