package com.handalsali.handali.repository;

import com.handalsali.handali.domain.Habit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HabitRepository extends JpaRepository<Habit,Long> {

}
