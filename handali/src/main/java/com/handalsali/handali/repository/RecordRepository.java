package com.handalsali.handali.repository;

import com.handalsali.handali.DTO.Record.MonthlyRecordCountResponse;
import com.handalsali.handali.DTO.Record.SatisfactionAvgByCategoryResponse;
import com.handalsali.handali.DTO.Record.TotalRecordsByCategoryResponse;
import com.handalsali.handali.DTO.Record.TotalTimeByCategoryResponse;
import com.handalsali.handali.DTO.RecordDTO;
import com.handalsali.handali.domain.Habit;
import com.handalsali.handali.domain.Record;
import com.handalsali.handali.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

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

    @Query("select new com.handalsali.handali.DTO.Record.SatisfactionAvgByCategoryResponse(" +
            "r.habit.categoryName, avg(r.satisfaction)) " +
            "from Record r " +
            "where r.user=:user " +
            "and month(r.date)=month(current_date) " +
            "and year(r.date)=year(current_date) " +
            "group by r.habit.categoryName")
    List<SatisfactionAvgByCategoryResponse> findAvgSatisfactionByCategoryThisMonth(@Param("user") User user);

    @Query("select new com.handalsali.handali.DTO.Record.TotalTimeByCategoryResponse(" +
            "r.habit.categoryName, sum(r.time)) " +
            "from Record r " +
            "where r.user=:user " +
            "and month(r.date)=month(current_date) " +
            "and year(r.date)=year(current_date) " +
            "group by r.habit.categoryName")
    List<TotalTimeByCategoryResponse> findTotalTimeByCategoryThisMonth(@Param("user") User user);

    @Query("select count(r) " +
            "from Record r " +
            "where r.user=:user " +
            "and month(r.date)=month(current_date ) " +
            "and year(r.date)=year(current_date )")
    int countByDateThisMonth(@Param("user")User user);

    @Query("select new com.handalsali.handali.DTO.Record.TotalRecordsByCategoryResponse(" +
            "r.habit.categoryName, count(r)) " +
            "from Record r " +
            "where r.user=:user " +
            "and month(r.date)=month(current_date) " +
            "and year(r.date)=year(current_date) " +
            "group by r.habit.categoryName")
    List<TotalRecordsByCategoryResponse> findTotalRecordsByCategoryThisMonth(@Param("user") User user);

    @Query("select new com.handalsali.handali.DTO.Record.MonthlyRecordCountResponse(" +
            "year(r.date), month(r.date), count(r)) " +
            "from Record r " +
            "where r.user=:user " +
            "and r.date between :start and :end " +
            "group by year(r.date), month(r.date) " +
            "order by year(r.date), month(r.date)")
    List<MonthlyRecordCountResponse> findMonthlyRecordCounts(
            @Param("user") User user,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end);
}
