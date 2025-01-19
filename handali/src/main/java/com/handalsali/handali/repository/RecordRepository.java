package com.handalsali.handali.repository;

import com.handalsali.handali.domain.Habit;
import com.handalsali.handali.domain.Record;
import com.handalsali.handali.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface RecordRepository extends JpaRepository<Record, Long> {
    boolean existsByHabitAndDateAndUser(Habit habit, LocalDate date, User user);
}
