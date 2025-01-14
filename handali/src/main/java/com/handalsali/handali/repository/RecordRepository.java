package com.handalsali.handali.repository;

import com.handalsali.handali.domain.Habit;
import com.handalsali.handali.domain.Record;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface RecordRepository extends JpaRepository<Record, Long> {
    boolean existsByHabitAndDate(Habit habit, LocalDate date);
}
