package com.handalsali.handali.service;

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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RecordServiceTest {
    @Mock
    private RecordRepository recordRepository;
    @InjectMocks
    private RecordService recordService;
    @Mock
    private UserService userService;
    @Mock
    private HabitService habitService;
    @Mock
    private StatService statService;

    private User user;
    private String token;
    private Habit habit;
    private Record record;
    private RecordDTO.recordTodayHabitRequest request;
    private final LocalDate testDate=LocalDate.of(2025,4,13);

    @BeforeEach
    public void setUp() {
        token = "test-token";
        user = new User("aaa@gmail.com", "name", "1234", "010-1234-5678", testDate);
        habit=new Habit(Categoryname.ACTIVITY,"테니스", CreatedType.USER);
        record=new Record(user,habit,3.0f,50,testDate);
        request=new RecordDTO.recordTodayHabitRequest(Categoryname.ACTIVITY,"테니스",3.0f,50,testDate);
    }

    /**[습관 기록] 및 스탯 업데이트*/
    @Test
    public void testRecordTodayHabit(){
        //given
        setTokenAndHabit(Optional.of(habit));
        when(recordRepository.existsByHabitAndDateAndUser(habit, testDate,user))
                .thenReturn(false);

        //getLastRecordTime()
        when( recordRepository.findTopByUserAndHabitOrderByDateDesc(user, habit))
        .thenReturn(record);

        //getRecordCount()
        when(recordRepository.countByUserAndHabitAndDate(eq(user), eq(habit), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(3);

        //스탯 업데이트------------
        when(statService.statUpdateAndCheckHandaliStat(eq(user), anyInt(), anyFloat(), eq(request)))
                .thenReturn(true);


        //when
        recordService.recordTodayHabit(token,request);

        //then
        ArgumentCaptor<Record> captor = ArgumentCaptor.forClass(Record.class);
        verify(recordRepository).save(captor.capture());
        Record record = captor.getValue();
        assertEquals(user,record.getUser());
        assertEquals(habit,record.getHabit());
        assertEquals(request.getDate(),record.getDate());
        assertEquals(3.0f,record.getTime());
        assertEquals(50,record.getSatisfaction());
    }


    @Test
    //습관을 찾을 수 없을 때
    public void testRecordTodayHabit_HabitNotExistsException(){
        setTokenAndHabit(Optional.empty());

        assertThrows(HabitNotExistsException.class,()->
                recordService.recordTodayHabit(token,request));
    }

    @Test
    //하루에 습관을 중복 기록 하려고 할 때
    public void testRecordTodayHabit_TodayHabitAlreadyRecordException(){
        setTokenAndHabit(Optional.of(habit));
        when(recordRepository.existsByHabitAndDateAndUser(habit, testDate,user))
                .thenReturn(true);

        assertThrows(TodayHabitAlreadyRecordException.class,()->
                recordService.recordTodayHabit(token,request));
    }

    private void setTokenAndHabit(Optional<Habit> habit) {
        when(userService.tokenToUser(token)).thenReturn(user);
        when(habitService.findByCategoryAndDetailedHabitName(Categoryname.ACTIVITY, "테니스"))
                .thenReturn(habit);
    }
}
