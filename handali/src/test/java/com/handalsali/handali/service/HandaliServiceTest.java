package com.handalsali.handali.service;

import com.handalsali.handali.domain.Handali;
import com.handalsali.handali.domain.Job;
import com.handalsali.handali.domain.User;
import com.handalsali.handali.repository.HandaliRepository;
import com.handalsali.handali.repository.RecordRepository;
import com.handalsali.handali.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HandaliServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private HandaliService handaliService;

    @Mock
    private HandaliRepository handaliRepository;

    @Mock
    private RecordRepository recordRepository;

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
        handaliService.payWeekSalary();

        //then
        verify(userRepository).save(argThat(savedUser ->
                savedUser.getTotal_coin() == totalCoin
        ));
    }
}
