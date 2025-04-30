package com.handalsali.handali.service;

import com.handalsali.handali.domain.Apart;
import com.handalsali.handali.domain.Handali;
import com.handalsali.handali.domain.Job;
import com.handalsali.handali.domain.User;
import com.handalsali.handali.repository.HandaliRepository;
import com.handalsali.handali.repository.RecordRepository;
import com.handalsali.handali.repository.UserRepository;
import com.handalsali.handali.scheduler.HandaliScheduler;
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

    @InjectMocks
    private HandaliScheduler handaliScheduler;

    /**
     * [한달이 자동 취업 및 아파트 입주]
     */
    @Test
    public void testProcessMonthlyJobAndApartmentEntry_AssignsJobAndApartment() {
        // Given
        Handali handali = new Handali();
        handali.setNickname("테스트한달이");

        // 이전 달에 생성된 한달이 리스트 설정
        when(handaliRepository.findUnemployedHandalisForMonth(any(), any()))
                .thenReturn(List.of(handali));

        Job job = new Job("개발자", 7000);
        Apart apart = new Apart();

        when(jobService.assignBestJobToHandali(handali)).thenReturn(job);
        when(apartmentService.assignApartmentToHandali(handali)).thenReturn(apart);

        // When
        handaliScheduler.assignApartmentsToHandalis();

        // Then
        verify(jobService).assignBestJobToHandali(handali);
        verify(apartmentService).assignApartmentToHandali(handali);

        ArgumentCaptor<Handali> captor = ArgumentCaptor.forClass(Handali.class);
        verify(handaliRepository).save(captor.capture());

        Handali saved = captor.getValue();
        assertEquals("개발자", saved.getJob().getName());
        assertEquals(apart, saved.getApart());
    }

    //한달이가 없을 경우 테스트
    @Test
    public void testProcessMonthlyJobAndApartmentEntry_NoHandalisToProcess() {
        // Given
        when(handaliRepository.findUnemployedHandalisForMonth(any(), any()))
                .thenReturn(List.of());

        // When
        handaliScheduler.assignApartmentsToHandalis();

        // Then
        verify(jobService, never()).assignBestJobToHandali(any());
        verify(apartmentService, never()).assignApartmentToHandali(any());
        verify(handaliRepository, never()).save(any());
    }

    // 한달이가 이미 취업했거나 입주했을 때 테스트
    @Test
    public void testProcessMonthlyJobAndApartmentEntry_HandaliAlreadyHasJobOrApartment() {
        // Given
        User user = new User();

        // 이미 직업이 있음 (입주 X)
        Job existingJob = new Job("경비", 6000);
        Handali employedOnly = new Handali();
        employedOnly.setNickname("직업만 있음");
        employedOnly.setJob(existingJob);
        employedOnly.setApart(null);

        // 이미 아파트 입주함 (직업 X)
        Apart existingApart = new Apart(user, null, "닉", 2, 202);
        Handali movedInOnly = new Handali();
        movedInOnly.setNickname("입주만 함");
        movedInOnly.setJob(null);
        movedInOnly.setApart(existingApart);

        // 둘 다 있음
        Handali fullyAssigned = new Handali();
        fullyAssigned.setNickname("둘 다 있음");
        fullyAssigned.setJob(existingJob);
        fullyAssigned.setApart(existingApart);

        List<Handali> alreadyProcessedHandalis = List.of(employedOnly, movedInOnly, fullyAssigned);

        when(handaliRepository.findUnemployedHandalisForMonth(any(), any()))
                .thenReturn(alreadyProcessedHandalis);

        // When
        handaliScheduler.assignApartmentsToHandalis();

        // Then
        // 실제로 새로 배정되거나 저장된 것이 없어야 함
        verify(jobService, never()).assignBestJobToHandali(any());
        verify(apartmentService, never()).assignApartmentToHandali(any());
        verify(handaliRepository, never()).save(any());
    }

    /**직업에 따른 주급 사용자에게 지급*/
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


        //4. 실제 주급 및 토탈 코인 계산
        int totalSalary = 3 * 10 + (int)(1000*(0.0/12));
        int totalCoin=user.getTotal_coin()+totalSalary;

        //when
        handaliScheduler.payWeekSalary();

        //then
        verify(userRepository).save(argThat(savedUser ->
                savedUser.getTotal_coin() == totalCoin
        ));
    }
}
