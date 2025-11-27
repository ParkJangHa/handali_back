package com.handalsali.handali.service;

import com.handalsali.handali.domain.Apart;
import com.handalsali.handali.domain.Handali;
import com.handalsali.handali.domain.Job;
import com.handalsali.handali.domain.User;
import com.handalsali.handali.repository.HandaliRepository;
import com.handalsali.handali.repository.RecordRepository;
import com.handalsali.handali.repository.UserRepository;
import com.handalsali.handali.scheduler.HandaliScheduler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("HandaliScheduler 테스트")
public class HandaliSchedulerTest {

    @InjectMocks
    private HandaliScheduler handaliScheduler;

    @Mock
    private HandaliRepository handaliRepository;

    @Mock
    private JobService jobService;

    @Mock
    private ApartmentService apartmentService;

    @Mock
    private RecordRepository recordRepository;

    @Mock
    private UserRepository userRepository;

    private User user;
    private Handali handali;
    private Job job;
    private Apart apart;

    @BeforeEach
    void setUp() {
        user = new User("test@gmail.com", "테스트유저", "password123", "010-1234-5678", LocalDate.now());
        user.setTotal_coin(1000);

        handali = new Handali();
        handali.setNickname("테스트한달이");
        handali.setStartDate(LocalDate.now().minusMonths(1));
        handali.setUser(user);

        job = new Job("개발자", 7000);

        apart = new Apart();
        apart.setFloor(5);
    }

    /**
     * processMonthlyJobAndApartmentEntry 테스트
     */
    @Test
    @DisplayName("매월 1일 자동 실행 - 한달이 취업 및 입주 성공")
    public void testProcessMonthlyJobAndApartmentEntry_Success() {
        // given
        when(handaliRepository.findUnemployedHandalisForMonth(any(), any()))
                .thenReturn(List.of(handali));
        when(jobService.assignBestJobToHandali(handali)).thenReturn(job);
        when(apartmentService.assignApartmentToHandali(handali)).thenReturn(apart);

        // when
        handaliScheduler.processMonthlyJobAndApartmentEntry();

        // then
        verify(jobService, times(1)).assignBestJobToHandali(handali);
        verify(apartmentService, times(1)).assignApartmentToHandali(handali);

        ArgumentCaptor<Handali> captor = ArgumentCaptor.forClass(Handali.class);
        verify(handaliRepository, times(1)).save(captor.capture());

        Handali savedHandali = captor.getValue();
        assertEquals("개발자", savedHandali.getJob().getName(), "직업이 할당되어야 함");
        assertEquals(apart, savedHandali.getApart(), "아파트가 할당되어야 함");
        assertEquals("테스트한달이", savedHandali.getNickname());
    }

    @Test
    @DisplayName("매월 1일 자동 실행 - 처리할 한달이가 없는 경우")
    public void testProcessMonthlyJobAndApartmentEntry_NoHandalis() {
        // given
        when(handaliRepository.findUnemployedHandalisForMonth(any(), any()))
                .thenReturn(List.of());

        // when
        handaliScheduler.processMonthlyJobAndApartmentEntry();

        // then
        verify(jobService, never()).assignBestJobToHandali(any());
        verify(apartmentService, never()).assignApartmentToHandali(any());
        verify(handaliRepository, never()).save(any());
    }

    @Test
    @DisplayName("매월 1일 자동 실행 - 여러 한달이 동시 처리")
    public void testProcessMonthlyJobAndApartmentEntry_MultipleHandalis() {
        // given
        Handali handali1 = new Handali();
        handali1.setNickname("한달이1");

        Handali handali2 = new Handali();
        handali2.setNickname("한달이2");

        Handali handali3 = new Handali();
        handali3.setNickname("한달이3");

        Job job1 = new Job("개발자", 7000);
        Job job2 = new Job("디자이너", 6500);
        Job job3 = new Job("기획자", 6000);

        Apart apart1 = new Apart();
        Apart apart2 = new Apart();
        Apart apart3 = new Apart();

        when(handaliRepository.findUnemployedHandalisForMonth(any(), any()))
                .thenReturn(List.of(handali1, handali2, handali3));

        when(jobService.assignBestJobToHandali(handali1)).thenReturn(job1);
        when(jobService.assignBestJobToHandali(handali2)).thenReturn(job2);
        when(jobService.assignBestJobToHandali(handali3)).thenReturn(job3);

        when(apartmentService.assignApartmentToHandali(handali1)).thenReturn(apart1);
        when(apartmentService.assignApartmentToHandali(handali2)).thenReturn(apart2);
        when(apartmentService.assignApartmentToHandali(handali3)).thenReturn(apart3);

        // when
        handaliScheduler.processMonthlyJobAndApartmentEntry();

        // then
        verify(jobService, times(3)).assignBestJobToHandali(any());
        verify(apartmentService, times(3)).assignApartmentToHandali(any());
        verify(handaliRepository, times(3)).save(any());
    }

    @Test
    @DisplayName("매월 1일 자동 실행 - 이미 직업이 있는 한달이는 제외")
    public void testProcessMonthlyJobAndApartmentEntry_AlreadyHasJob() {
        // given
        Job existingJob = new Job("경비", 6000);
        handali.setJob(existingJob);

        when(handaliRepository.findUnemployedHandalisForMonth(any(), any()))
                .thenReturn(List.of(handali));

        // when
        handaliScheduler.processMonthlyJobAndApartmentEntry();

        // then
        verify(jobService, never()).assignBestJobToHandali(any());
        verify(apartmentService, never()).assignApartmentToHandali(any());
        verify(handaliRepository, never()).save(any());
    }

    @Test
    @DisplayName("매월 1일 자동 실행 - 이미 아파트가 있는 한달이는 제외")
    public void testProcessMonthlyJobAndApartmentEntry_AlreadyHasApart() {
        // given
        Apart existingApart = new Apart();
        handali.setApart(existingApart);

        when(handaliRepository.findUnemployedHandalisForMonth(any(), any()))
                .thenReturn(List.of(handali));

        // when
        handaliScheduler.processMonthlyJobAndApartmentEntry();

        // then
        verify(jobService, never()).assignBestJobToHandali(any());
        verify(apartmentService, never()).assignApartmentToHandali(any());
        verify(handaliRepository, never()).save(any());
    }

    @Test
    @DisplayName("매월 1일 자동 실행 - 직업과 아파트 둘 다 있는 한달이는 제외")
    public void testProcessMonthlyJobAndApartmentEntry_AlreadyFullyAssigned() {
        // given
        Job existingJob = new Job("경비", 6000);
        Apart existingApart = new Apart();
        handali.setJob(existingJob);
        handali.setApart(existingApart);

        when(handaliRepository.findUnemployedHandalisForMonth(any(), any()))
                .thenReturn(List.of(handali));

        // when
        handaliScheduler.processMonthlyJobAndApartmentEntry();

        // then
        verify(jobService, never()).assignBestJobToHandali(any());
        verify(apartmentService, never()).assignApartmentToHandali(any());
        verify(handaliRepository, never()).save(any());
    }

    /**
     * processEmploymentAndMoveIn 테스트
     */
    @Test
    @DisplayName("한달이 취업 및 입주 처리 - 정상 처리")
    public void testProcessEmploymentAndMoveIn_Success() {
        // given
        when(jobService.assignBestJobToHandali(handali)).thenReturn(job);
        when(apartmentService.assignApartmentToHandali(handali)).thenReturn(apart);

        // when
        handaliScheduler.processEmploymentAndMoveIn(handali);

        // then
        ArgumentCaptor<Handali> captor = ArgumentCaptor.forClass(Handali.class);
        verify(handaliRepository, times(1)).save(captor.capture());

        Handali savedHandali = captor.getValue();
        assertNotNull(savedHandali.getJob(), "직업이 할당되어야 함");
        assertNotNull(savedHandali.getApart(), "아파트가 할당되어야 함");
        assertEquals("개발자", savedHandali.getJob().getName());
    }

    @Test
    @DisplayName("한달이 취업 및 입주 처리 - null 한달이 예외")
    public void testProcessEmploymentAndMoveIn_NullHandali() {
        // when & then
        assertThrows(IllegalArgumentException.class, () -> {
            handaliScheduler.processEmploymentAndMoveIn(null);
        }, "한달이가 null이면 예외가 발생해야 함");

        verify(jobService, never()).assignBestJobToHandali(any());
        verify(apartmentService, never()).assignApartmentToHandali(any());
        verify(handaliRepository, never()).save(any());
    }

    @Test
    @DisplayName("한달이 취업 및 입주 처리 - 이미 처리된 한달이는 스킵")
    public void testProcessEmploymentAndMoveIn_AlreadyProcessed() {
        // given
        handali.setJob(job);
        handali.setApart(apart);

        // when
        handaliScheduler.processEmploymentAndMoveIn(handali);

        // then
        verify(jobService, never()).assignBestJobToHandali(any());
        verify(apartmentService, never()).assignApartmentToHandali(any());
        verify(handaliRepository, never()).save(any());
    }

    /**
     * payWeekSalary 테스트
     */
    @Test
    @DisplayName("주급 지급 - 신규 한달이 (0개월 경과)")
    public void testPayWeekSalary_NewHandali() {
        // given
        LocalDate now = LocalDate.now();
        handali.setStartDate(now);
        handali.setJob(job);

        YearMonth startYearMonth = YearMonth.from(now);
        LocalDate startDate = startYearMonth.atDay(1);
        LocalDate endDate = startYearMonth.atEndOfMonth();

        when(handaliRepository.findAllByJobIsNotNull()).thenReturn(List.of(handali));
        when(recordRepository.countByUserAndDate(eq(user), eq(startDate), eq(endDate)))
                .thenReturn(10);

        // 기록 횟수*10 + 주급*(12-0)/12
        int expectedSalary = 10 * 10 + (int)(7000 * (12.0 / 12.0));
        int expectedTotalCoin = 1000 + expectedSalary;

        // when
        handaliScheduler.payWeekSalary();

        // then
        verify(userRepository, times(1)).save(argThat(savedUser ->
                savedUser.getTotal_coin() == expectedTotalCoin
        ));
    }

    @Test
    @DisplayName("주급 지급 - 6개월 경과 한달이")
    public void testPayWeekSalary_SixMonthsOld() {
        // given
        LocalDate sixMonthsAgo = LocalDate.now().minusMonths(6);
        handali.setStartDate(sixMonthsAgo);
        handali.setJob(job);

        YearMonth startYearMonth = YearMonth.from(sixMonthsAgo);
        LocalDate startDate = startYearMonth.atDay(1);
        LocalDate endDate = startYearMonth.atEndOfMonth();

        when(handaliRepository.findAllByJobIsNotNull()).thenReturn(List.of(handali));
        when(recordRepository.countByUserAndDate(eq(user), eq(startDate), eq(endDate)))
                .thenReturn(15);

        // 기록 횟수*10 + 주급*(12-6)/12 = 50%
        int expectedSalary = 15 * 10 + (int)(7000 * (6.0 / 12.0));
        int expectedTotalCoin = 1000 + expectedSalary;

        // when
        handaliScheduler.payWeekSalary();

        // then
        verify(userRepository, times(1)).save(argThat(savedUser ->
                savedUser.getTotal_coin() == expectedTotalCoin
        ));
    }

    @Test
    @DisplayName("주급 지급 - 12개월 이상 경과 한달이 (주급 0)")
    public void testPayWeekSalary_TwelveMonthsOld() {
        // given
        LocalDate twelveMonthsAgo = LocalDate.now().minusMonths(12);
        handali.setStartDate(twelveMonthsAgo);
        handali.setJob(job);

        YearMonth startYearMonth = YearMonth.from(twelveMonthsAgo);
        LocalDate startDate = startYearMonth.atDay(1);
        LocalDate endDate = startYearMonth.atEndOfMonth();

        when(handaliRepository.findAllByJobIsNotNull()).thenReturn(List.of(handali));
        when(recordRepository.countByUserAndDate(eq(user), eq(startDate), eq(endDate)))
                .thenReturn(3);

        // 기록 횟수*10 + 주급*(12-12)/12 = 0
        int expectedSalary = 3 * 10 + (int)(7000 * (0.0 / 12.0));
        int expectedTotalCoin = 1000 + expectedSalary;

        // when
        handaliScheduler.payWeekSalary();

        // then
        verify(userRepository, times(1)).save(argThat(savedUser ->
                savedUser.getTotal_coin() == expectedTotalCoin
        ));
    }

    @Test
    @DisplayName("주급 지급 - 13개월 이상 경과 한달이 (음수 방지)")
    public void testPayWeekSalary_OverTwelveMonths() {
        // given
        LocalDate fifteenMonthsAgo = LocalDate.now().minusMonths(15);
        handali.setStartDate(fifteenMonthsAgo);
        handali.setJob(job);

        YearMonth startYearMonth = YearMonth.from(fifteenMonthsAgo);
        LocalDate startDate = startYearMonth.atDay(1);
        LocalDate endDate = startYearMonth.atEndOfMonth();

        when(handaliRepository.findAllByJobIsNotNull()).thenReturn(List.of(handali));
        when(recordRepository.countByUserAndDate(eq(user), eq(startDate), eq(endDate)))
                .thenReturn(5);

        // Math.max(0, 12-15) = 0이므로 주급은 0
        int expectedSalary = 5 * 10 + 0;
        int expectedTotalCoin = 1000 + expectedSalary;

        // when
        handaliScheduler.payWeekSalary();

        // then
        verify(userRepository, times(1)).save(argThat(savedUser ->
                savedUser.getTotal_coin() == expectedTotalCoin
        ));
    }

    @Test
    @DisplayName("주급 지급 - 기록이 없는 경우")
    public void testPayWeekSalary_NoRecords() {
        // given
        LocalDate threeMonthsAgo = LocalDate.now().minusMonths(3);
        handali.setStartDate(threeMonthsAgo);
        handali.setJob(job);

        YearMonth startYearMonth = YearMonth.from(threeMonthsAgo);
        LocalDate startDate = startYearMonth.atDay(1);
        LocalDate endDate = startYearMonth.atEndOfMonth();

        when(handaliRepository.findAllByJobIsNotNull()).thenReturn(List.of(handali));
        when(recordRepository.countByUserAndDate(eq(user), eq(startDate), eq(endDate)))
                .thenReturn(0);

        // 기록 0 * 10 + 주급*(12-3)/12
        int expectedSalary = 0 + (int)(7000 * (9.0 / 12.0));
        int expectedTotalCoin = 1000 + expectedSalary;

        // when
        handaliScheduler.payWeekSalary();

        // then
        verify(userRepository, times(1)).save(argThat(savedUser ->
                savedUser.getTotal_coin() == expectedTotalCoin
        ));
    }

    @Test
    @DisplayName("주급 지급 - 직업이 있는 한달이가 없는 경우")
    public void testPayWeekSalary_NoEmployedHandalis() {
        // given
        when(handaliRepository.findAllByJobIsNotNull()).thenReturn(List.of());

        // when
        handaliScheduler.payWeekSalary();

        // then
        verify(recordRepository, never()).countByUserAndDate(any(), any(), any());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("주급 지급 - 여러 한달이 동시 지급")
    public void testPayWeekSalary_MultipleHandalis() {
        // given
        User user1 = new User("user1@gmail.com", "유저1", "pw1", "010-1111-1111", LocalDate.now());
        user1.setTotal_coin(1000);

        User user2 = new User("user2@gmail.com", "유저2", "pw2", "010-2222-2222", LocalDate.now());
        user2.setTotal_coin(2000);

        Handali handali1 = new Handali();
        handali1.setNickname("한달이1");
        handali1.setStartDate(LocalDate.now());
        handali1.setUser(user1);
        handali1.setJob(new Job("개발자", 7000));

        Handali handali2 = new Handali();
        handali2.setNickname("한달이2");
        handali2.setStartDate(LocalDate.now().minusMonths(6));
        handali2.setUser(user2);
        handali2.setJob(new Job("디자이너", 6000));

        YearMonth now = YearMonth.now();
        YearMonth sixMonthsAgo = YearMonth.from(LocalDate.now().minusMonths(6));

        when(handaliRepository.findAllByJobIsNotNull()).thenReturn(List.of(handali1, handali2));
        when(recordRepository.countByUserAndDate(eq(user1), any(), any())).thenReturn(10);
        when(recordRepository.countByUserAndDate(eq(user2), any(), any())).thenReturn(5);

        // when
        handaliScheduler.payWeekSalary();

        // then
        verify(userRepository, times(2)).save(any());
        verify(recordRepository, times(2)).countByUserAndDate(any(), any(), any());
    }

    /**
     * calculateSalaryFor 테스트
     */
    @Test
    @DisplayName("주급 계산 - 정확한 계산")
    public void testCalculateSalaryFor() {
        // given
        LocalDate threeMonthsAgo = LocalDate.now().minusMonths(3);
        handali.setStartDate(threeMonthsAgo);
        handali.setJob(job);

        YearMonth startYearMonth = YearMonth.from(threeMonthsAgo);
        LocalDate startDate = startYearMonth.atDay(1);
        LocalDate endDate = startYearMonth.atEndOfMonth();

        when(recordRepository.countByUserAndDate(eq(user), eq(startDate), eq(endDate)))
                .thenReturn(20);

        // when
        int salary = handaliScheduler.calculateSalaryFor(handali);

        // then
        // 기록 20*10 + 주급 7000*(9/12) = 200 + 5250 = 5450
        int expectedSalary = 20 * 10 + (int)(7000 * (9.0 / 12.0));
        assertEquals(expectedSalary, salary, "주급이 정확히 계산되어야 함");
    }

    @Test
    @DisplayName("주급 계산 - 다양한 경과 개월 수")
    public void testCalculateSalaryFor_VariousMonths() {
        // given
        handali.setJob(new Job("테스트직업", 12000));

        YearMonth startYearMonth;
        LocalDate startDate;
        LocalDate endDate;

        // 0개월 경과
        handali.setStartDate(LocalDate.now());
        startYearMonth = YearMonth.from(LocalDate.now());
        startDate = startYearMonth.atDay(1);
        endDate = startYearMonth.atEndOfMonth();
        when(recordRepository.countByUserAndDate(eq(user), eq(startDate), eq(endDate)))
                .thenReturn(10);

        int salary0 = handaliScheduler.calculateSalaryFor(handali);
        assertEquals(10 * 10 + 12000, salary0, "0개월: 100% 주급");

        // 3개월 경과
        handali.setStartDate(LocalDate.now().minusMonths(3));
        startYearMonth = YearMonth.from(LocalDate.now().minusMonths(3));
        startDate = startYearMonth.atDay(1);
        endDate = startYearMonth.atEndOfMonth();
        when(recordRepository.countByUserAndDate(eq(user), eq(startDate), eq(endDate)))
                .thenReturn(10);

        int salary3 = handaliScheduler.calculateSalaryFor(handali);
        assertEquals(10 * 10 + (int)(12000 * (9.0/12.0)), salary3, "3개월: 75% 주급");

        // 12개월 경과
        handali.setStartDate(LocalDate.now().minusMonths(12));
        startYearMonth = YearMonth.from(LocalDate.now().minusMonths(12));
        startDate = startYearMonth.atDay(1);
        endDate = startYearMonth.atEndOfMonth();
        when(recordRepository.countByUserAndDate(eq(user), eq(startDate), eq(endDate)))
                .thenReturn(10);

        int salary12 = handaliScheduler.calculateSalaryFor(handali);
        assertEquals(10 * 10, salary12, "12개월: 0% 주급 (기록 보상만)");
    }

    /**
     * 통합 시나리오 테스트
     */
    @Test
    @DisplayName("시나리오 테스트 - 한달이 생성부터 주급 지급까지")
    public void testScenario_FromCreationToSalary() {
        // given - 1단계: 신규 한달이 (직업 없음)
        Handali newHandali = new Handali();
        newHandali.setNickname("신규한달이");
        newHandali.setStartDate(LocalDate.now().minusMonths(1));
        newHandali.setUser(user);

        Job assignedJob = new Job("신입개발자", 5000);
        Apart assignedApart = new Apart();

        when(handaliRepository.findUnemployedHandalisForMonth(any(), any()))
                .thenReturn(List.of(newHandali));
        when(jobService.assignBestJobToHandali(newHandali)).thenReturn(assignedJob);
        when(apartmentService.assignApartmentToHandali(newHandali)).thenReturn(assignedApart);

        // when - 1단계: 취업 및 입주 처리
        handaliScheduler.processMonthlyJobAndApartmentEntry();

        // then - 1단계: 검증
        verify(handaliRepository, times(1)).save(any());

        // given - 2단계: 취업한 한달이로 주급 지급 준비
        newHandali.setJob(assignedJob);

        YearMonth startYearMonth = YearMonth.from(newHandali.getStartDate());
        LocalDate startDate = startYearMonth.atDay(1);
        LocalDate endDate = startYearMonth.atEndOfMonth();

        when(handaliRepository.findAllByJobIsNotNull()).thenReturn(List.of(newHandali));
        when(recordRepository.countByUserAndDate(eq(user), eq(startDate), eq(endDate)))
                .thenReturn(15);

        // when - 2단계: 주급 지급
        handaliScheduler.payWeekSalary();

        // then - 2단계: 주급 지급 검증
        verify(userRepository, times(1)).save(any());

        System.out.println("시나리오 테스트 완료: 취업 → 주급 지급");
    }
}