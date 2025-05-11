package com.handalsali.handali.service;

import com.handalsali.handali.DTO.HabitDTO;
import com.handalsali.handali.domain.Habit;
import com.handalsali.handali.domain.User;
import com.handalsali.handali.domain.UserHabit;
import com.handalsali.handali.enums.Categoryname;
import com.handalsali.handali.enums.CreatedType;
import com.handalsali.handali.exception.HabitNotExistsException;
import com.handalsali.handali.repository.HabitRepository;
import com.handalsali.handali.repository.UserHabitRepository;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HabitServiceTest {
    @InjectMocks
    private HabitService habitService;
    @Mock
    private HabitRepository habitRepository;
    @Mock
    private UserHabitRepository userHabitRepository;
    @Mock
    private UserService userService;

    private String token;
    private User user;
    private Categoryname category;
    private String details;
    private CreatedType createdType;
    private HabitDTO.AddHabitApiRequest addHabitApiRequest;

    @BeforeEach
    void setUp() {
        token = "test-token";
        user = new User("aaa@gmail.com", "name", "1234", "010-1234-5678", LocalDate.now());
        category = Categoryname.ACTIVITY;
        details = "아침 운동";
        createdType = CreatedType.USER;

        HabitDTO.AddHabitRequest addHabitRequest=new HabitDTO.AddHabitRequest(category, details, createdType);
        addHabitApiRequest=new HabitDTO.AddHabitApiRequest(List.of(addHabitRequest));
    }


    /**[습관 추가]*/
    //새로운 습관, 새로운 사용자-습관 관계
    @Test
    public void testCreateUserHabit_newHabit_newUserHabit() {
        //given
        when(userService.tokenToUser(token)).thenReturn(user);

        //when
        //새로운 습관일 경우
        when(habitRepository.findByCategoryNameAndDetailedHabitName(category, details))
                .thenReturn(Optional.empty());
        Habit newHabit = new Habit(category, details, createdType);
        when(habitRepository.save(any(Habit.class))).thenReturn(newHabit);

        //새로운 사용자-습관 관계일 경우
        when(userHabitRepository.existsByUserAndHabit(user, newHabit))
                .thenReturn(false);

        habitService.createUserHabit(token,addHabitApiRequest);

        //then
        ArgumentCaptor<Habit> habitCaptor=ArgumentCaptor.forClass(Habit.class);
        verify(habitRepository).save(habitCaptor.capture());
        Habit capturedHabit=habitCaptor.getValue();
        assertEquals(category,capturedHabit.getCategoryName());
        assertEquals(details,capturedHabit.getDetailedHabitName());
        assertEquals(createdType,capturedHabit.getCreatedType());

        ArgumentCaptor<UserHabit> userHabitCaptor=ArgumentCaptor.forClass(UserHabit.class);
        verify(userHabitRepository).save(userHabitCaptor.capture());
        UserHabit userHabit=userHabitCaptor.getValue();
        assertEquals(user,userHabit.getUser());
        assertEquals(newHabit,userHabit.getHabit());
    }

    //습관은 존재, 새로운 사용자-습관 관계
    @Test
    public void testCreateUserHabit_existHabit_newUserHabit(){
        //given
        when(userService.tokenToUser(token)).thenReturn(user);

        //when
        //습관이 기존에 존재할 경우
        Habit oldHabit=new Habit(category, details, createdType);
        when(habitRepository.findByCategoryNameAndDetailedHabitName(category, details))
                .thenReturn(Optional.of(oldHabit));

        //새로운 사용자-습관 관계일 경우
        when(userHabitRepository.existsByUserAndHabit(user,oldHabit))
                .thenReturn(false);

        habitService.createUserHabit(token,addHabitApiRequest);

        //then
        verify(habitRepository,never()).save(any());

        ArgumentCaptor<UserHabit> userHabitCaptor=ArgumentCaptor.forClass(UserHabit.class);
        verify(userHabitRepository).save(userHabitCaptor.capture());
        UserHabit userHabit=userHabitCaptor.getValue();
        assertEquals(user,userHabit.getUser());
        assertEquals(oldHabit,userHabit.getHabit());
    }

    //습관은 존재, 사용자-습관 관계도 존재
    @Test
    public void testCreateUserHabit_existHabit_existUserHabit(){
        //given
        when(userService.tokenToUser(token)).thenReturn(user);

        //when
        //습관이 기존에 존재할 경우
        Habit oldHabit=new Habit(category, details, createdType);
        when(habitRepository.findByCategoryNameAndDetailedHabitName(category, details))
                .thenReturn(Optional.of(oldHabit));

        //사용자-습관 관계가 기존에 존재할 경우
        when(userHabitRepository.existsByUserAndHabit(user,oldHabit))
                .thenReturn(true);

        habitService.createUserHabit(token,addHabitApiRequest);

        //then
        verify(habitRepository,never()).save(any());
        verify(userHabitRepository,never()).save(any());
    }

    /**[이번달 습관으로 지정]*/
    //이미 등록했던 습관
    @Test
    public void testAddHabitsForCurrentMonth_existUserHabit(){
        //given
        when(userService.tokenToUser(token)).thenReturn(user);

        Habit habit=new Habit(category, details, createdType);
        when(habitRepository.findByCategoryNameAndDetailedHabitName(category, details))
                .thenReturn(Optional.of(habit));

        //when
        //이미 등록했던 습관일 경우
        when(userHabitRepository.existsByUserAndHabit(user, habit))
                .thenReturn(true);
        UserHabit existUserHabit=new UserHabit(user,habit);
        when(userHabitRepository.findByUserAndHabit(user,habit))
                .thenReturn(existUserHabit);

        habitService.addHabitsForCurrentMonth(token,addHabitApiRequest);

        //then
        ArgumentCaptor<UserHabit> userHabitCaptor=ArgumentCaptor.forClass(UserHabit.class);
        verify(userHabitRepository).save(userHabitCaptor.capture());
        UserHabit userHabit=userHabitCaptor.getValue();
        assertEquals(user,userHabit.getUser());
        assertEquals(habit,userHabit.getHabit());
        assertEquals(LocalDate.now().getMonthValue(),userHabit.getMonth());
    }

    //새로 등록하는 습관
    @Test
    public void testAddHabitsForCurrentMonth_newUserHabit(){
        //given
        when(userService.tokenToUser(token)).thenReturn(user);

        Habit habit=new Habit(category, details, createdType);
        when(habitRepository.findByCategoryNameAndDetailedHabitName(category, details))
                .thenReturn(Optional.of(habit));

        //when
        //새로 등록하는 습관일 경우
        when(userHabitRepository.existsByUserAndHabit(user, habit))
                .thenReturn(false);

        habitService.addHabitsForCurrentMonth(token,addHabitApiRequest);

        //then
        ArgumentCaptor<UserHabit> userHabitCaptor=ArgumentCaptor.forClass(UserHabit.class);
        verify(userHabitRepository).save(userHabitCaptor.capture());
        UserHabit userHabit=userHabitCaptor.getValue();
        assertEquals(user,userHabit.getUser());
        assertEquals(habit,userHabit.getHabit());
        assertEquals(LocalDate.now().getMonthValue(),userHabit.getMonth());
    }

    //습관이 존재하지 않을 경우
    @Test
    public void testAddHabitsForCurrentMonth_habitNotFound(){
        //given
        when(userService.tokenToUser(token)).thenReturn(user);

        //when
        when(habitRepository.findByCategoryNameAndDetailedHabitName(any(), any()))
                .thenReturn(Optional.empty());

        //then
        assertThrows(HabitNotExistsException.class,()->{
            habitService.addHabitsForCurrentMonth(token,addHabitApiRequest);
        });

    }
}
