package com.handalsali.handali.repository;

import com.handalsali.handali.domain.Habit;
import com.handalsali.handali.domain.User;
import com.handalsali.handali.domain.UserHabit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserHabitRepository extends JpaRepository<UserHabit,Long> {
    boolean existsByUserAndHabit(User user, Habit habit);
    UserHabit findByUserAndHabit(User user, Habit habit);
}
