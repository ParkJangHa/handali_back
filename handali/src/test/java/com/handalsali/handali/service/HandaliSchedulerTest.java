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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HandaliSchedulerTest {
    // ======================== 의존성 주입 ========================
    @Mock private HandaliRepository handaliRepository;
    @Mock private JobService jobService;
    @Mock private ApartmentService apartmentService;
    @Mock private RecordRepository recordRepository;
    @Mock private UserRepository userRepository;
    @InjectMocks private HandaliScheduler handaliScheduler;

    // ======================== 테스트 공통 자원 ========================
    private Job defaultJob;
    private Apart defaultApart;

    @BeforeEach
    void setUp() {
        defaultJob = new Job("개발자", 7000);
        defaultApart = new Apart();
    }

    /**
     * [생성된 한달이에게 직업과 아파트 할당 후 저장]
     */
    @Test
    public void testProcessMonthlyJobAndApartmentEntry_AssignsJobAndApartment() {
        // Given
        Handali handali = new Handali();
        handali.setNickname("테스트한달이");

        // 이전 달에 생성된 한달이 리스트 설정
        when(handaliRepository.findUnemployedHandalisForMonth(any(), any()))
                .thenReturn(List.of(handali));

        when(jobService.assignBestJobToHandali(handali)).thenReturn(defaultJob);
        when(apartmentService.assignApartmentToHandali(handali)).thenReturn(defaultApart);

        // When
        handaliScheduler.processMonthlyJobAndApartmentEntry();

        // Then
        verify(jobService).assignBestJobToHandali(handali);
        verify(apartmentService).assignApartmentToHandali(handali);

        ArgumentCaptor<Handali> captor = ArgumentCaptor.forClass(Handali.class);
        verify(handaliRepository).save(captor.capture());

        Handali saved = captor.getValue();
        assertEquals("개발자", saved.getJob().getName());
        assertEquals(defaultApart, saved.getApart());
    }

    /**
     * [예외 케이스] 대상 한달이가 없을 경우 처리 없이 종료되어야 함
     */
    @Test
    public void testProcessMonthlyJobAndApartmentEntry_NoHandalisToProcess() {
        // Given
        when(handaliRepository.findUnemployedHandalisForMonth(any(), any()))
                .thenReturn(List.of());

        // When
        handaliScheduler.processMonthlyJobAndApartmentEntry();

        // Then
        verify(jobService, never()).assignBestJobToHandali(any());
        verify(apartmentService, never()).assignApartmentToHandali(any());
        verify(handaliRepository, never()).save(any());
    }

    /**
     * [예외 케이스] 이미 취업 or 입주한 한달이는 처리되지 않아야 함
      */
    @Test
    public void testProcessMonthlyJobAndApartmentEntry_HandaliAlreadyHasJobOrApartment() {
        // Given
        User user = new User();

        Handali employedOnly = new Handali("직업만 있음", LocalDate.now(), user);
        employedOnly.setJob(defaultJob);

        Handali movedInOnly = new Handali("입주만 함", LocalDate.now(), user);
        movedInOnly.setApart(defaultApart);

        Handali fullyAssigned = new Handali("둘 다 있음", LocalDate.now(), user);
        fullyAssigned.setJob(defaultJob);
        fullyAssigned.setApart(defaultApart);

        List<Handali> alreadyProcessedHandalis = List.of(employedOnly, movedInOnly, fullyAssigned);

        when(handaliRepository.findUnemployedHandalisForMonth(any(), any()))
                .thenReturn(alreadyProcessedHandalis);

        // When
        handaliScheduler.processMonthlyJobAndApartmentEntry();

        // Then
        // 실제로 새로 배정되거나 저장된 것이 없어야 함
        verify(jobService, never()).assignBestJobToHandali(any());
        verify(apartmentService, never()).assignApartmentToHandali(any());
        verify(handaliRepository, never()).save(any());
    }

    /**
     * 직업을 가진 한달이에게 주급을 계산하여 유저에게 코인 지급
     * */
    @Test
    public void testPayWeekSalary(){
        //given
        //1. 사용자 생성
        User user=new User("aaa@gmail.com","name","1234","010-1234-5678", LocalDate.now());
        user.setTotal_coin(1000);

        //2. 직업 가진 한달이 생성 및 기간 찾기
        LocalDate handaliNow=LocalDate.now().minusMonths(12); //12달이 지났을때
        Handali handali = new Handali("last",handaliNow,user);

        Job job=new Job("직업",1000);
        handali.setJob(job);

        YearMonth startYearMonth = YearMonth.from(handaliNow);
        LocalDate startDate = startYearMonth.atDay(1); //한달이 달의 시작 년월일
        LocalDate endDate=startYearMonth.atEndOfMonth(); //한달이 달의 마지막 년월일

        //3. 한달이 한개 리스트 만들기, 기록은 3개 리턴한다고 치기
        when(handaliRepository.findAllByJobIsNotNull()).thenReturn(List.of(handali));
        when(recordRepository.countByUserAndDate(eq(user), eq(startDate), eq(endDate))).thenReturn(3);


        //4. 실제 주급 및 토탈 코인 계산    *계산식 수정 필요*
        int totalSalary = 3 * 10 + (int)(1000*(0.0/12));
        int totalCoin=user.getTotal_coin()+totalSalary;

        //when
        handaliScheduler.payWeekSalary();

        //then
        verify(userRepository).save(argThat(savedUser ->
                savedUser.getTotal_coin() == totalCoin
        ));
    }

    /**
     * [경계 테스트] 이번 달 생성된 한달이는 처리 대상에서 제외되어야 함
     */
    @Test
    public void testProcessMonthlyJobAndApartmentEntry_ShouldSkipCurrentMonthHandalis() {
        // Given
        LocalDate now = LocalDate.now(); // 이번 달
        LocalDate lastMonth = now.minusMonths(1); // 전달

        // 이번 달 생성된 한달이 (잘못 처리되면 안 됨)
        Handali currentMonthHandali = new Handali("이번달", now, new User());
        // 전달 생성된 한달이 (처리 대상)
        Handali lastMonthHandali = new Handali("전달", lastMonth, new User());

        // 반환 목록에 둘 다 포함되었을 경우 시뮬레이션
        when(handaliRepository.findUnemployedHandalisForMonth(any(), any()))
                .thenReturn(List.of(currentMonthHandali, lastMonthHandali));

        Job job = defaultJob;
        Apart apart = defaultApart;

        // 전달 한달이만 매핑
        when(jobService.assignBestJobToHandali(lastMonthHandali)).thenReturn(job);
        when(apartmentService.assignApartmentToHandali(lastMonthHandali)).thenReturn(apart);

        // When
        handaliScheduler.processMonthlyJobAndApartmentEntry();

        // Then
        // 전달 생성된 한달이에 대해서만 처리
        verify(jobService).assignBestJobToHandali(lastMonthHandali);
        verify(apartmentService).assignApartmentToHandali(lastMonthHandali);
        verify(handaliRepository).save(lastMonthHandali);

        // 현재 달 생성된 한달이에 대해서는 처리하지 않아야 함
        verify(jobService, never()).assignBestJobToHandali(currentMonthHandali);
        verify(apartmentService, never()).assignApartmentToHandali(currentMonthHandali);
        verify(handaliRepository, never()).save(currentMonthHandali);
    }

}
