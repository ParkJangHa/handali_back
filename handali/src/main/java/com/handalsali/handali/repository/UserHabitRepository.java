package com.handalsali.handali.repository;

import com.handalsali.handali.domain.Habit;
import com.handalsali.handali.domain.User;
import com.handalsali.handali.domain.UserHabit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserHabitRepository extends JpaRepository<UserHabit,Long> {
    boolean existsByUserAndHabit(User user, Habit habit);
    UserHabit findByUserAndHabit(User user, Habit habit);

    /** [달별 습관 조회]*/
    @Query("SELECT uh FROM UserHabit uh " +
            "WHERE uh.user = :user AND uh.month = :month")
    List<UserHabit> findByUserAndMonth(
            @Param("user") User user,
            @Param("month") int month);
}
