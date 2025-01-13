package com.handalsali.handali.repository;

import com.handalsali.handali.domain.Habit;
import com.handalsali.handali.enums_multyKey.Categoryname;
import com.handalsali.handali.enums_multyKey.CreatedType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HabitRepository extends JpaRepository<Habit,Long> {
    //추가
    Optional<Habit> findByCategoryNameAndDetailedHabitName(Categoryname categoryName, String detailedHabitName);

    //추가
    @Query("SELECT h FROM Habit h JOIN UserHabit uh ON h.habitId = uh.habit.habitId " +
            "WHERE uh.user.userId = :userId AND h.createdType = :category_Type AND h.categoryName = :category")
    List<Habit> findByUserIdAndCategoryTypeAndCategory(
            @Param("userId") Long userId,
            @Param("category_Type") CreatedType category_Type,
            @Param("category") Categoryname category);


    // 카테고리별 조회 추가
    @Query("SELECT h FROM Habit h JOIN UserHabit uh ON h.habitId = uh.habit.habitId " +
            "WHERE uh.user.userId = :userId AND h.createdType = :category_Type AND h.categoryName = :category AND uh.month = :month")
    List<Habit> findByUserCategoryAndMonth(
            @Param("userId") Long userId,
            @Param("category_Type") CreatedType category_Type,
            @Param("category") Categoryname category,
            @Param("month") int month);

}
