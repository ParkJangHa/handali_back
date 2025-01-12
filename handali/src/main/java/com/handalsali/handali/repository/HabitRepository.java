package com.handalsali.handali.repository;

import com.handalsali.handali.domain.Habit;
import com.handalsali.handali.enums_multyKey.Categoryname;
import com.handalsali.handali.enums_multyKey.CreatedType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HabitRepository extends JpaRepository<Habit,Long> {
    Optional<Habit> findByCategoryNameAndDetailedHabitName(Categoryname categoryname, String detail);
    List<Habit> findByUserIdAndCategoryTypeAndCategory(Long userId, CreatedType categoryType, Categoryname category);
    List<Habit> findByUserCategoryAndMonth(Long userId, CreatedType createdType, Categoryname category, int month);
}
