package com.handalsali.handali.service;

import com.handalsali.handali.DTO.RecordDTO;
import com.handalsali.handali.domain.Handali;
import com.handalsali.handali.domain.HandaliStat;
import com.handalsali.handali.domain.Stat;
import com.handalsali.handali.domain.User;
import com.handalsali.handali.enums_multyKey.Categoryname;
import com.handalsali.handali.enums_multyKey.TypeName;
import com.handalsali.handali.exception.HandaliNotFoundException;
import com.handalsali.handali.exception.HandaliStatNotFoundException;
import com.handalsali.handali.repository.HandaliRepository;
import com.handalsali.handali.repository.HandaliStatRepository;
import com.handalsali.handali.repository.StatRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.jayway.jsonpath.internal.path.PathCompiler.fail;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StatServiceTest {

    @InjectMocks
    private StatService statService;
    @Mock
    private StatRepository statRepository;
    @Mock
    private HandaliRepository handaliRepository;
    @Mock
    private HandaliStatRepository handaliStatRepository;

    private User user;
    private Handali currentHandali;
    private HandaliStat currentHandaliStat;
    private Stat activityStat;
    private RecordDTO.recordTodayHabitRequest request;
    private final LocalDate testDate=LocalDate.of(2025,4,13);

    @BeforeEach
    void setUp() {
        user = new User("aaa@gmail.com", "name", "1234", "010-1234-5678", LocalDate.now());

        currentHandali=new Handali("aaa",LocalDate.now(),user);
        activityStat=new Stat();
        activityStat.setValue(0);
        activityStat.setLastMonthValue(0);
        currentHandaliStat=new HandaliStat(currentHandali,activityStat);

        request=new RecordDTO.recordTodayHabitRequest(Categoryname.ACTIVITY,"테니스",3.0f,50,testDate);
    }

    /**한달이 생성후, 스탯 초기화*/
    //지난달 한달이가 있을 경우
    @Test
    public void testStatInit_existLastHandali(){
        //given
        //지난 한달이 초기화
        Handali lastHandali=new Handali("aaa",LocalDate.from(LocalDate.now().minusMonths(1).atStartOfDay()),user);

        Stat lastActivity = new Stat(TypeName.ACTIVITY_SKILL);
        lastActivity.setValue(50);
        Stat lastIntelligent = new Stat(TypeName.INTELLIGENT_SKILL);
        lastIntelligent.setValue(60);
        Stat lastArt = new Stat(TypeName.ART_SKILL);
        lastArt.setValue(70);

        List<HandaliStat> lastMonthStats = List.of(
                new HandaliStat(lastHandali, lastActivity),
                new HandaliStat(lastHandali, lastIntelligent),
                new HandaliStat(lastHandali, lastArt)
        );

        when(handaliRepository.findLastMonthHandali(any(), any(), any()))
                .thenReturn(lastHandali);
        when(handaliStatRepository.findByHandaliAndStatType(eq(lastHandali), any()))
                .thenReturn(lastMonthStats);


        //when
        statService.statInit(user,currentHandali);

        //then
        ArgumentCaptor<Stat> statCaptor = ArgumentCaptor.forClass(Stat.class);
        verify(statRepository, times(3)).save(statCaptor.capture());

        List<Stat> savedStats = statCaptor.getAllValues();

        assertEquals(3, savedStats.size());

        for (Stat stat : savedStats) {
            switch (stat.getTypeName()) {
                case ACTIVITY_SKILL -> assertEquals(50f, stat.getLastMonthValue());
                case INTELLIGENT_SKILL -> assertEquals(60f, stat.getLastMonthValue());
                case ART_SKILL -> assertEquals(70f, stat.getLastMonthValue());
                default -> fail("예상치 못한 Stat 타입: " + stat.getTypeName());
            }
            assertEquals(0, stat.getValue());
        }
    }

    //지난달 한달이가 존재하지 않을 경우
    @Test
    public void testStatInit_NotExistLastHandali(){
        //given
        //지난달 한달이가 존재하지 않음
        when(handaliRepository.findLastMonthHandali(any(), any(), any()))
                .thenReturn(null);

        //when
        statService.statInit(user,currentHandali);

        //then
        ArgumentCaptor<Stat> statCaptor = ArgumentCaptor.forClass(Stat.class);
        verify(statRepository, times(3)).save(statCaptor.capture());
        List<Stat> savedStats=statCaptor.getAllValues();
        assertEquals(3, savedStats.size());

        for(Stat stat: savedStats) {
            assertEquals(0, stat.getLastMonthValue());
            assertEquals(0, stat.getValue());
        }
    }

    /**[스탯 업데이트] 및 한달이 상태 변화 여부 체크*/
    //상태가 변화하는 경우
    @Test
    public void testStatUpdateAndCheckHandaliStat_changeStatus() {
        //given
        when(handaliRepository.findLatestHandaliByCurrentMonth(user.getUserId()))
                .thenReturn(currentHandali); //한달이
        when(handaliStatRepository.findByHandaliAndType(eq(currentHandali), any()))
                .thenReturn(Optional.of(currentHandaliStat)); //한달이-스탯, 활동 현재,지난 스탯=0
        //when
        boolean status = statService.statUpdateAndCheckHandaliStat(user, 1000, 100f, request);

        //then
        assertTrue(status);
        assertTrue(currentHandaliStat.getStat().getValue()>0); //활동 스탯 증가 여부 확인
    }

    //상태가 변하지 않는 경우
    @Test
    public void testStatUpdateAndCheckHandaliStat_NotChangeStatus() {
        //given
        when(handaliRepository.findLatestHandaliByCurrentMonth(user.getUserId()))
                .thenReturn(currentHandali); //한달이
        when(handaliStatRepository.findByHandaliAndType(eq(currentHandali), any()))
                .thenReturn(Optional.of(currentHandaliStat)); //한달이-스탯, 활동 현재,지난 스탯=0
        //when
        boolean status = statService.statUpdateAndCheckHandaliStat(user, 1, 100f, request);

        //then
        assertFalse(status);
        assertTrue(currentHandaliStat.getStat().getValue()>0); //활동 스탯 증가 여부 확인
    }

    //한달이 존재하지 않는 예외
    @Test
    public void testStatUpdateAndCheckHandaliStat_HandaliNotFoundException() {
        when(handaliRepository.findLatestHandaliByCurrentMonth(user.getUserId()))
                .thenReturn(null);
        assertThrows(HandaliNotFoundException.class, ()
                ->{statService.statUpdateAndCheckHandaliStat(user, 1, 100f, request);});


    }

    //한달이 스탯이 존재하지 않는 예외
    @Test
    public void testStatUpdateAndCheckHandaliStat_HandaliStatNotFoundException() {
        when(handaliRepository.findLatestHandaliByCurrentMonth(user.getUserId()))
                .thenReturn(currentHandali); //한달이
        when(handaliStatRepository.findByHandaliAndType(any(),any()))
                .thenReturn(Optional.empty());

        assertThrows(HandaliStatNotFoundException.class, ()
                ->{statService.statUpdateAndCheckHandaliStat(user, 1, 100f, request);});
    }

    /**
     * 스탯 증가 계산
     */

    @Test
    public void testCalculateStatValue_basic(){
        //given
        int recordedDays=15;
        float lastRecordedTime=0f;
        HandaliStat handaliStat=currentHandaliStat; //활동 현재,지난 스탯=0
        float currentTime=3.0f;
        int satisfaction=50;

        //when
        float result=statService.calculateStatValue(recordedDays,lastRecordedTime,handaliStat,currentTime,satisfaction);

        //then
        System.out.println(result);
        assertTrue(result>0);
    }


    /**
     * 스탯에 따른 레벨 반환
     */
    @Test
    public void testCheckHandaliStat(){
        assertEquals(1,statService.checkHandaliStat(100));
        assertEquals(2,statService.checkHandaliStat(250));
        assertEquals(3,statService.checkHandaliStat(450));
        assertEquals(4,statService.checkHandaliStat(700));
        assertEquals(5,statService.checkHandaliStat(1100));
    }
}
