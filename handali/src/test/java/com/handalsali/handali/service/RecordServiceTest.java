package com.handalsali.handali.service;

import com.handalsali.handali.DTO.Record.MonthlyRecordCountResponse;
import com.handalsali.handali.DTO.Record.SatisfactionAvgByCategoryResponse;
import com.handalsali.handali.DTO.Record.TotalRecordsByCategoryResponse;
import com.handalsali.handali.DTO.Record.TotalTimeByCategoryResponse;
import com.handalsali.handali.DTO.RecordDTO;
import com.handalsali.handali.domain.Habit;
import com.handalsali.handali.domain.User;
import com.handalsali.handali.domain.Record;
import com.handalsali.handali.enums.Categoryname;
import com.handalsali.handali.enums.CreatedType;
import com.handalsali.handali.exception.HabitNotExistsException;
import com.handalsali.handali.exception.TodayHabitAlreadyRecordException;
import com.handalsali.handali.repository.RecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RecordServiceTest {

    @Mock
    private RecordRepository recordRepository;
    @Mock
    private UserService userService;
    @Mock
    private HabitService habitService;
    @Mock
    private StatService statService;

    @InjectMocks
    private RecordService recordService;

    private User user;
    private String token;
    private Habit habit;
    private Record record;
    private RecordDTO.RecordTodayHabitRequest request;
    private final LocalDate testDate = LocalDate.of(2025, 4, 13);

    @BeforeEach
    public void setUp() {
        token = "test-token";
        user = new User("aaa@gmail.com", "name", "1234", "010-1234-5678", testDate);
        habit = new Habit(Categoryname.ACTIVITY, "테니스", CreatedType.USER);
        record = new Record(user, habit, 3.0f, 50, testDate);
        request = new RecordDTO.RecordTodayHabitRequest(Categoryname.ACTIVITY, "테니스", 3.0f, 50, testDate);
    }

    // ========================================
    // recordTodayHabit 테스트
    // ========================================

    @Nested
    @DisplayName("습관 기록 테스트")
    class RecordTodayHabitTest {

        @Test
        @DisplayName("정상적으로 습관 기록 및 스탯 업데이트 성공")
        public void testRecordTodayHabit_Success() {
            // given
            setTokenAndHabit(Optional.of(habit));
            when(recordRepository.existsByHabitAndDateAndUser(habit, testDate, user))
                    .thenReturn(false);
            when(recordRepository.findTopByUserAndHabitOrderByDateDesc(user, habit))
                    .thenReturn(record);
            when(recordRepository.countByUserAndHabitAndDate(eq(user), eq(habit), any(LocalDate.class), any(LocalDate.class)))
                    .thenReturn(3);
            when(statService.statUpdateAndCheckHandaliStat(eq(user), anyInt(), anyFloat(), eq(request)))
                    .thenReturn(true);

            // when
            RecordDTO.RecordTodayHabitResponse response = recordService.recordTodayHabit(token, request);

            // then
            ArgumentCaptor<Record> captor = ArgumentCaptor.forClass(Record.class);
            verify(recordRepository).save(captor.capture());

            Record savedRecord = captor.getValue();
            assertEquals(user, savedRecord.getUser());
            assertEquals(habit, savedRecord.getHabit());
            assertEquals(request.getDate(), savedRecord.getDate());
            assertEquals(3.0f, savedRecord.getTime());
            assertEquals(50, savedRecord.getSatisfaction());

            // 응답 검증
            assertEquals("습관이 성공적으로 기록되었습니다.", response.getMessage());
            assertTrue(response.isAppearance_change());
        }

        @Test
        @DisplayName("스탯 변화가 없는 경우")
        public void testRecordTodayHabit_NoStatChange() {
            // given
            setTokenAndHabit(Optional.of(habit));
            when(recordRepository.existsByHabitAndDateAndUser(habit, testDate, user))
                    .thenReturn(false);
            when(recordRepository.findTopByUserAndHabitOrderByDateDesc(user, habit))
                    .thenReturn(record);
            when(recordRepository.countByUserAndHabitAndDate(eq(user), eq(habit), any(LocalDate.class), any(LocalDate.class)))
                    .thenReturn(3);
            when(statService.statUpdateAndCheckHandaliStat(eq(user), anyInt(), anyFloat(), eq(request)))
                    .thenReturn(false);

            // when
            RecordDTO.RecordTodayHabitResponse response = recordService.recordTodayHabit(token, request);

            // then
            assertFalse(response.isAppearance_change());
        }

        @Test
        @DisplayName("이전 기록이 없는 경우 (첫 기록)")
        public void testRecordTodayHabit_FirstRecord() {
            // given
            setTokenAndHabit(Optional.of(habit));
            when(recordRepository.existsByHabitAndDateAndUser(habit, testDate, user))
                    .thenReturn(false);
            when(recordRepository.findTopByUserAndHabitOrderByDateDesc(user, habit))
                    .thenReturn(null);
            when(recordRepository.countByUserAndHabitAndDate(eq(user), eq(habit), any(LocalDate.class), any(LocalDate.class)))
                    .thenReturn(0);
            when(statService.statUpdateAndCheckHandaliStat(eq(user), eq(0), eq(0f), eq(request)))
                    .thenReturn(true);

            // when
            RecordDTO.RecordTodayHabitResponse response = recordService.recordTodayHabit(token, request);

            // then
            verify(statService).statUpdateAndCheckHandaliStat(user, 0, 0f, request);
            assertNotNull(response);
        }

        @Test
        @DisplayName("습관이 존재하지 않을 때 예외 발생")
        public void testRecordTodayHabit_HabitNotExistsException() {
            // given
            setTokenAndHabit(Optional.empty());

            // when & then
            assertThrows(HabitNotExistsException.class, () ->
                    recordService.recordTodayHabit(token, request));

            verify(recordRepository, never()).save(any());
        }

        @Test
        @DisplayName("하루에 같은 습관 중복 기록 시 예외 발생")
        public void testRecordTodayHabit_TodayHabitAlreadyRecordException() {
            // given
            setTokenAndHabit(Optional.of(habit));
            when(recordRepository.existsByHabitAndDateAndUser(habit, testDate, user))
                    .thenReturn(true);

            // when & then
            TodayHabitAlreadyRecordException exception = assertThrows(
                    TodayHabitAlreadyRecordException.class,
                    () -> recordService.recordTodayHabit(token, request)
            );

            assertTrue(exception.getMessage().contains("테니스"));
            assertTrue(exception.getMessage().contains(testDate.toString()));
            verify(recordRepository, never()).save(any());
        }
    }

    // ========================================
    // recordSummary 테스트
    // ========================================

    @Nested
    @DisplayName("기록 요약 테스트")
    class RecordSummaryTest {

        @Test
        @DisplayName("정상적으로 기록 요약 조회 성공")
        public void testRecordSummary_Success() {
            // given
            when(userService.tokenToUser(token)).thenReturn(user);

            // 이번달 데이터
            List<SatisfactionAvgByCategoryResponse> monthlyAvgSatisfaction = List.of(
                    new SatisfactionAvgByCategoryResponse(Categoryname.ACTIVITY, 80.0)
            );
            List<TotalTimeByCategoryResponse> monthlyTotalTime = List.of(
                    new TotalTimeByCategoryResponse(Categoryname.ACTIVITY, 10.5)
            );
            List<TotalRecordsByCategoryResponse> monthlyTotalRecords = List.of(
                    new TotalRecordsByCategoryResponse(Categoryname.ACTIVITY, 5L)
            );
            List<MonthlyRecordCountResponse> monthlyRecordCounts = List.of(
                    new MonthlyRecordCountResponse(2025, 4, 10L)
            );

            // 이번주 데이터
            List<SatisfactionAvgByCategoryResponse> weeklyAvgSatisfaction = List.of(
                    new SatisfactionAvgByCategoryResponse(Categoryname.ACTIVITY, 75.0)
            );
            List<TotalTimeByCategoryResponse> weeklyTotalTime = List.of(
                    new TotalTimeByCategoryResponse(Categoryname.ACTIVITY, 3.0)
            );
            List<TotalRecordsByCategoryResponse> weeklyTotalRecords = List.of(
                    new TotalRecordsByCategoryResponse(Categoryname.ACTIVITY, 2L)
            );

            when(recordRepository.findAvgSatisfactionByCategoryThisMonth(user))
                    .thenReturn(monthlyAvgSatisfaction);
            when(recordRepository.findTotalTimeByCategoryThisMonth(user))
                    .thenReturn(monthlyTotalTime);
            when(recordRepository.countByDateThisMonth(user))
                    .thenReturn(15);
            when(recordRepository.findTotalRecordsByCategoryThisMonth(user))
                    .thenReturn(monthlyTotalRecords);
            when(recordRepository.findMonthlyRecordCounts(eq(user), any(LocalDate.class), any(LocalDate.class)))
                    .thenReturn(monthlyRecordCounts);
            when(recordRepository.findAvgSatisfactionByCategoryBetweenDates(eq(user), any(LocalDate.class), any(LocalDate.class)))
                    .thenReturn(weeklyAvgSatisfaction);
            when(recordRepository.findTotalTimeByCategoryBetweenDates(eq(user), any(LocalDate.class), any(LocalDate.class)))
                    .thenReturn(weeklyTotalTime);
            when(recordRepository.countByDateBetween(eq(user), any(LocalDate.class), any(LocalDate.class)))
                    .thenReturn(3);
            when(recordRepository.findTotalRecordsByCategoryBetweenDates(eq(user), any(LocalDate.class), any(LocalDate.class)))
                    .thenReturn(weeklyTotalRecords);

            // when
            RecordDTO.RecordSummaryResponse response = recordService.recordSummary(token);

            // then
            assertNotNull(response);

            // 이번달 검증
            assertEquals(1, response.getSatisfaction_avg_by_category_month().size());
            assertEquals(15, response.getTotal_records_month());

            // 이번주 검증
            assertEquals(1, response.getSatisfaction_avg_by_category_week().size());
            assertEquals(3, response.getTotal_records_week());
        }

        @Test
        @DisplayName("기록이 없는 경우 빈 리스트 반환")
        public void testRecordSummary_NoRecords() {
            // given
            when(userService.tokenToUser(token)).thenReturn(user);

            when(recordRepository.findAvgSatisfactionByCategoryThisMonth(user))
                    .thenReturn(List.of());
            when(recordRepository.findTotalTimeByCategoryThisMonth(user))
                    .thenReturn(List.of());
            when(recordRepository.countByDateThisMonth(user))
                    .thenReturn(0);
            when(recordRepository.findTotalRecordsByCategoryThisMonth(user))
                    .thenReturn(List.of());
            when(recordRepository.findMonthlyRecordCounts(eq(user), any(LocalDate.class), any(LocalDate.class)))
                    .thenReturn(List.of());
            when(recordRepository.findAvgSatisfactionByCategoryBetweenDates(eq(user), any(LocalDate.class), any(LocalDate.class)))
                    .thenReturn(List.of());
            when(recordRepository.findTotalTimeByCategoryBetweenDates(eq(user), any(LocalDate.class), any(LocalDate.class)))
                    .thenReturn(List.of());
            when(recordRepository.countByDateBetween(eq(user), any(LocalDate.class), any(LocalDate.class)))
                    .thenReturn(0);
            when(recordRepository.findTotalRecordsByCategoryBetweenDates(eq(user), any(LocalDate.class), any(LocalDate.class)))
                    .thenReturn(List.of());

            // when
            RecordDTO.RecordSummaryResponse response = recordService.recordSummary(token);

            // then
            assertNotNull(response);
            assertTrue(response.getSatisfaction_avg_by_category_month().isEmpty());
            assertTrue(response.getTotal_time_by_category_month().isEmpty());
            assertEquals(0, response.getTotal_records_month());
            assertEquals(0, response.getTotal_records_week());
        }

        @Test
        @DisplayName("여러 카테고리 기록이 있는 경우")
        public void testRecordSummary_MultipleCategories() {
            // given
            when(userService.tokenToUser(token)).thenReturn(user);

            List<SatisfactionAvgByCategoryResponse> monthlyAvgSatisfaction = List.of(
                    new SatisfactionAvgByCategoryResponse(Categoryname.ACTIVITY, 80.0),
                    new SatisfactionAvgByCategoryResponse(Categoryname.INTELLIGENT, 90.0),
                    new SatisfactionAvgByCategoryResponse(Categoryname.ART, 70.0)
            );

            when(recordRepository.findAvgSatisfactionByCategoryThisMonth(user))
                    .thenReturn(monthlyAvgSatisfaction);
            when(recordRepository.findTotalTimeByCategoryThisMonth(user))
                    .thenReturn(List.of());
            when(recordRepository.countByDateThisMonth(user))
                    .thenReturn(30);
            when(recordRepository.findTotalRecordsByCategoryThisMonth(user))
                    .thenReturn(List.of());
            when(recordRepository.findMonthlyRecordCounts(eq(user), any(LocalDate.class), any(LocalDate.class)))
                    .thenReturn(List.of());
            when(recordRepository.findAvgSatisfactionByCategoryBetweenDates(eq(user), any(LocalDate.class), any(LocalDate.class)))
                    .thenReturn(List.of());
            when(recordRepository.findTotalTimeByCategoryBetweenDates(eq(user), any(LocalDate.class), any(LocalDate.class)))
                    .thenReturn(List.of());
            when(recordRepository.countByDateBetween(eq(user), any(LocalDate.class), any(LocalDate.class)))
                    .thenReturn(0);
            when(recordRepository.findTotalRecordsByCategoryBetweenDates(eq(user), any(LocalDate.class), any(LocalDate.class)))
                    .thenReturn(List.of());

            // when
            RecordDTO.RecordSummaryResponse response = recordService.recordSummary(token);

            // then
            assertEquals(3, response.getSatisfaction_avg_by_category_month().size());
            assertEquals(30, response.getTotal_records_month());
        }
    }

    // ========================================
    // 헬퍼 메서드
    // ========================================

    private void setTokenAndHabit(Optional<Habit> habit) {
        when(userService.tokenToUser(token)).thenReturn(user);
        when(habitService.findByCategoryAndDetailedHabitName(Categoryname.ACTIVITY, "테니스"))
                .thenReturn(habit);
    }
}