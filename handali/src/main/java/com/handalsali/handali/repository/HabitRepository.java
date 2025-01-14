package com.handalsali.handali.repository;

import com.handalsali.handali.domain.Habit;
import com.handalsali.handali.domain.User;
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
    //카테고리명, 세부습관명으로 습관 객체 찾기
    Optional<Habit> findByCategoryNameAndDetailedHabitName(Categoryname categoryName, String detailedHabitName);

    //사용자, 생성자, 카테고리로 습관 찾기
    @Query("SELECT h FROM Habit h JOIN UserHabit uh ON h = uh.habit " +
            "WHERE uh.user = :user AND h.createdType = :created_type AND h.categoryName = :category")
    List<Habit> findByUserAndCreatedTypeAndCategory(
            @Param("user") User user,
            @Param("created_type") CreatedType createdType,
            @Param("category") Categoryname category);


    // 카테고리별 조회 추가
    @Query("SELECT h FROM Habit h JOIN UserHabit uh ON h.habitId = uh.habit.habitId " +
            "WHERE uh.user.userId = :userId AND h.createdType = :createdType AND h.categoryName = :category AND uh.month = :month")
    List<Habit> findByUserCategoryAndMonth(
            @Param("userId") Long userId,
            @Param("createdType") CreatedType createdType,
            @Param("category") Categoryname category,
            @Param("month") int month);
}
