package com.handalsali.handali.service;

import com.handalsali.handali.DTO.HabitDTO;
import com.handalsali.handali.domain.Habit;
import com.handalsali.handali.domain.User;
import com.handalsali.handali.domain.UserHabit;
import com.handalsali.handali.enums.Categoryname;
import com.handalsali.handali.enums.CreatedType;
import com.handalsali.handali.exception.CreatedTypeOrCategoryNameWrongException;
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
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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

    /**[지난달 습관 갱신]*/
    //지난달 습관이 존재하는 경우
    @Test
    public void testRefreshLastMonthHabits_existLastMonthHabits(){
        //given
        when(userService.tokenToUser(token)).thenReturn(user);

        //지난달 월 값 계산
        int lastMonthValue = LocalDate.now().minusMonths(1).getMonthValue();
        int thisMonthValue = LocalDate.now().getMonthValue();

        //지난달 습관 리스트 생성
        Habit habit1 = new Habit(Categoryname.ACTIVITY, "아침 운동", CreatedType.USER);
        Habit habit2 = new Habit(Categoryname.INTELLIGENT, "독서", CreatedType.USER);

        UserHabit userHabit1 = new UserHabit(user, habit1);
        userHabit1.setMonth(lastMonthValue);
        UserHabit userHabit2 = new UserHabit(user, habit2);
        userHabit2.setMonth(lastMonthValue);

        List<UserHabit> lastMonthHabits = List.of(userHabit1, userHabit2);

        //when
        when(userHabitRepository.findByUserAndMonth(user, lastMonthValue))
                .thenReturn(lastMonthHabits);

        habitService.refreshLastMonthHabits(token);

        //then
        ArgumentCaptor<UserHabit> userHabitCaptor = ArgumentCaptor.forClass(UserHabit.class);
        verify(userHabitRepository, times(2)).save(userHabitCaptor.capture());

        List<UserHabit> savedUserHabits = userHabitCaptor.getAllValues();
        assertEquals(2, savedUserHabits.size());

        for(UserHabit savedUserHabit : savedUserHabits){
            assertEquals(user, savedUserHabit.getUser());
            assertEquals(thisMonthValue, savedUserHabit.getMonth());
        }
    }

    //지난달 습관이 존재하지 않는 경우
    @Test
    public void testRefreshLastMonthHabits_noLastMonthHabits(){
        //given
        when(userService.tokenToUser(token)).thenReturn(user);

        //지난달 월 값 계산
        int lastMonthValue = LocalDate.now().minusMonths(1).getMonthValue();

        //when
        when(userHabitRepository.findByUserAndMonth(user, lastMonthValue))
                .thenReturn(List.of());

        //then
        assertThrows(HabitNotExistsException.class, ()->{
            habitService.refreshLastMonthHabits(token);
        });

        verify(userHabitRepository, never()).save(any());
    }

    //지난달 습관이 1개만 존재하는 경우
    @Test
    public void testRefreshLastMonthHabits_singleLastMonthHabit(){
        //given
        when(userService.tokenToUser(token)).thenReturn(user);

        //지난달 월 값 계산
        int lastMonthValue = LocalDate.now().minusMonths(1).getMonthValue();
        int thisMonthValue = LocalDate.now().getMonthValue();

        //지난달 습관 1개 생성
        Habit habit = new Habit(Categoryname.ACTIVITY, "물 마시기", CreatedType.USER);
        UserHabit userHabit = new UserHabit(user, habit);
        userHabit.setMonth(lastMonthValue);

        List<UserHabit> lastMonthHabits = List.of(userHabit);

        //when
        when(userHabitRepository.findByUserAndMonth(user, lastMonthValue))
                .thenReturn(lastMonthHabits);

        habitService.refreshLastMonthHabits(token);

        //then
        ArgumentCaptor<UserHabit> userHabitCaptor = ArgumentCaptor.forClass(UserHabit.class);
        verify(userHabitRepository, times(1)).save(userHabitCaptor.capture());

        UserHabit savedUserHabit = userHabitCaptor.getValue();
        assertEquals(user, savedUserHabit.getUser());
        assertEquals(habit, savedUserHabit.getHabit());
        assertEquals(thisMonthValue, savedUserHabit.getMonth());
    }

    /**[사용자, 카테고리별 습관 조회]*/
    //정상적으로 습관을 조회하는 경우
    @Test
    public void testGetHabitsByUser_success(){
        //given
        when(userService.tokenToUser(token)).thenReturn(user);

        String category = "ACTIVITY";
        Categoryname categoryNameEnum = Categoryname.ACTIVITY;

        Habit habit1 = new Habit(categoryNameEnum, "아침 운동", CreatedType.USER);
        Habit habit2 = new Habit(categoryNameEnum, "저녁 운동", CreatedType.USER);
        List<Habit> habits = List.of(habit1, habit2);

        //when
        when(habitRepository.findByUserAndCreatedTypeAndCategory(user, CreatedType.USER, categoryNameEnum))
                .thenReturn(habits);

        HabitDTO.getHabitsApiResponse response = habitService.getHabitsByUser(token, category);

        //then
        verify(userService, times(1)).tokenToUser(token);
        verify(habitRepository, times(1)).findByUserAndCreatedTypeAndCategory(user, CreatedType.USER, categoryNameEnum);
        assertEquals(category, response.getCategory());
    }

    //습관이 없는 경우
    @Test
    public void testGetHabitsByUser_noHabits(){
        //given
        when(userService.tokenToUser(token)).thenReturn(user);

        String category = "ART";
        Categoryname categoryNameEnum = Categoryname.ART;

        //when
        when(habitRepository.findByUserAndCreatedTypeAndCategory(user, CreatedType.USER, categoryNameEnum))
                .thenReturn(List.of());

        HabitDTO.getHabitsApiResponse response = habitService.getHabitsByUser(token, category);

        //then
        verify(habitRepository, times(1)).findByUserAndCreatedTypeAndCategory(user, CreatedType.USER, categoryNameEnum);
        assertEquals(category, response.getCategory());
    }

    //잘못된 카테고리명을 입력한 경우
    @Test
    public void testGetHabitsByUser_invalidCategory(){
        //given
        when(userService.tokenToUser(token)).thenReturn(user);

        String invalidCategory = "INVALID_CATEGORY";

        //when & then
        assertThrows(CreatedTypeOrCategoryNameWrongException.class, ()->{
            habitService.getHabitsByUser(token, invalidCategory);
        });

        verify(habitRepository, never()).findByUserAndCreatedTypeAndCategory(any(), any(), any());
    }

    //여러 카테고리에 대한 조회 테스트
    @Test
    public void testGetHabitsByUser_multipleCategories(){
        //given
        when(userService.tokenToUser(token)).thenReturn(user);

        //ACTIVITY 카테고리
        String activityCategory = "ACTIVITY";
        Categoryname activityEnum = Categoryname.ACTIVITY;
        Habit habit1 = new Habit(activityEnum, "아침 운동", CreatedType.USER);
        when(habitRepository.findByUserAndCreatedTypeAndCategory(user, CreatedType.USER, activityEnum))
                .thenReturn(List.of(habit1));

        //ART 카테고리
        String artCategory = "ART";
        Categoryname artEnum = Categoryname.ART;
        Habit habit2 = new Habit(artEnum, "그림 그리기", CreatedType.USER);
        when(habitRepository.findByUserAndCreatedTypeAndCategory(user, CreatedType.USER, artEnum))
                .thenReturn(List.of(habit2));

        //when
        HabitDTO.getHabitsApiResponse activityResponse = habitService.getHabitsByUser(token, activityCategory);
        HabitDTO.getHabitsApiResponse artResponse = habitService.getHabitsByUser(token, artCategory);

        //then
        assertEquals(activityCategory, activityResponse.getCategory());
        assertEquals(artCategory, artResponse.getCategory());
        verify(habitRepository, times(1)).findByUserAndCreatedTypeAndCategory(user, CreatedType.USER, activityEnum);
        verify(habitRepository, times(1)).findByUserAndCreatedTypeAndCategory(user, CreatedType.USER, artEnum);
    }

    /**[개발자, 카테고리별 습관 조회]*/
    //정상적으로 개발자 습관을 조회하는 경우
    @Test
    public void testGetHabitsByDev_success(){
        //given
        when(userService.tokenToUser(token)).thenReturn(user);

        String category = "INTELLIGENT";
        Categoryname categoryNameEnum = Categoryname.INTELLIGENT;

        Habit habit1 = new Habit(categoryNameEnum, "독서하기", CreatedType.DEVELOPER);
        Habit habit2 = new Habit(categoryNameEnum, "코딩 공부", CreatedType.DEVELOPER);
        List<Habit> habits = List.of(habit1, habit2);

        //when
        when(habitRepository.findByCreatedTypeAndCategoryName(CreatedType.DEVELOPER, categoryNameEnum))
                .thenReturn(habits);

        HabitDTO.getHabitsApiResponse response = habitService.getHabitsByDev(token, category);

        //then
        verify(userService, times(1)).tokenToUser(token);
        verify(habitRepository, times(1)).findByCreatedTypeAndCategoryName(CreatedType.DEVELOPER, categoryNameEnum);
        assertEquals(category, response.getCategory());
    }

    //개발자 습관이 없는 경우
    @Test
    public void testGetHabitsByDev_noHabits(){
        //given
        when(userService.tokenToUser(token)).thenReturn(user);

        String category = "ACTIVITY";
        Categoryname categoryNameEnum = Categoryname.ACTIVITY;

        //when
        when(habitRepository.findByCreatedTypeAndCategoryName(CreatedType.DEVELOPER, categoryNameEnum))
                .thenReturn(List.of());

        HabitDTO.getHabitsApiResponse response = habitService.getHabitsByDev(token, category);

        //then
        verify(habitRepository, times(1)).findByCreatedTypeAndCategoryName(CreatedType.DEVELOPER, categoryNameEnum);
        assertEquals(category, response.getCategory());
    }

    //잘못된 카테고리명을 입력한 경우
    @Test
    public void testGetHabitsByDev_invalidCategory(){
        //given
        when(userService.tokenToUser(token)).thenReturn(user);

        String invalidCategory = "WRONG_CATEGORY";

        //when & then
        assertThrows(CreatedTypeOrCategoryNameWrongException.class, ()->{
            habitService.getHabitsByDev(token, invalidCategory);
        });

        verify(habitRepository, never()).findByCreatedTypeAndCategoryName(any(), any());
    }

    //여러 카테고리에 대한 개발자 습관 조회
    @Test
    public void testGetHabitsByDev_allCategories(){
        //given
        when(userService.tokenToUser(token)).thenReturn(user);

        //ART 카테고리
        String artCategory = "ART";
        Categoryname artEnum = Categoryname.ART;
        Habit habit1 = new Habit(artEnum, "그림 그리기", CreatedType.DEVELOPER);
        when(habitRepository.findByCreatedTypeAndCategoryName(CreatedType.DEVELOPER, artEnum))
                .thenReturn(List.of(habit1));

        //INTELLIGENT 카테고리
        String intelligentCategory = "INTELLIGENT";
        Categoryname intelligentEnum = Categoryname.INTELLIGENT;
        Habit habit2 = new Habit(intelligentEnum, "영어 공부", CreatedType.DEVELOPER);
        when(habitRepository.findByCreatedTypeAndCategoryName(CreatedType.DEVELOPER, intelligentEnum))
                .thenReturn(List.of(habit2));

        //when
        HabitDTO.getHabitsApiResponse artResponse = habitService.getHabitsByDev(token, artCategory);
        HabitDTO.getHabitsApiResponse intelligentResponse = habitService.getHabitsByDev(token, intelligentCategory);

        //then
        assertEquals(artCategory, artResponse.getCategory());
        assertEquals(intelligentCategory, intelligentResponse.getCategory());
        verify(habitRepository, times(1)).findByCreatedTypeAndCategoryName(CreatedType.DEVELOPER, artEnum);
        verify(habitRepository, times(1)).findByCreatedTypeAndCategoryName(CreatedType.DEVELOPER, intelligentEnum);
    }

    //사용자 습관과 개발자 습관이 구분되는지 확인
    @Test
    public void testGetHabits_userVsDeveloper(){
        //given
        when(userService.tokenToUser(token)).thenReturn(user);

        String category = "ACTIVITY";
        Categoryname categoryNameEnum = Categoryname.ACTIVITY;

        //사용자 습관
        Habit userHabit = new Habit(categoryNameEnum, "아침 운동", CreatedType.USER);
        when(habitRepository.findByUserAndCreatedTypeAndCategory(user, CreatedType.USER, categoryNameEnum))
                .thenReturn(List.of(userHabit));

        //개발자 습관
        Habit devHabit = new Habit(categoryNameEnum, "스트레칭", CreatedType.DEVELOPER);
        when(habitRepository.findByCreatedTypeAndCategoryName(CreatedType.DEVELOPER, categoryNameEnum))
                .thenReturn(List.of(devHabit));

        //when
        HabitDTO.getHabitsApiResponse userResponse = habitService.getHabitsByUser(token, category);
        HabitDTO.getHabitsApiResponse devResponse = habitService.getHabitsByDev(token, category);

        //then
        assertEquals(category, userResponse.getCategory());
        assertEquals(category, devResponse.getCategory());
        verify(habitRepository, times(1)).findByUserAndCreatedTypeAndCategory(user, CreatedType.USER, categoryNameEnum);
        verify(habitRepository, times(1)).findByCreatedTypeAndCategoryName(CreatedType.DEVELOPER, categoryNameEnum);
    }

    //소문자 카테고리명 입력 시 예외 발생 확인
    @Test
    public void testGetHabitsByUser_lowercaseCategory(){
        //given
        when(userService.tokenToUser(token)).thenReturn(user);

        String lowercaseCategory = "activity"; // 소문자

        //when & then
        assertThrows(CreatedTypeOrCategoryNameWrongException.class, ()->{
            habitService.getHabitsByUser(token, lowercaseCategory);
        });
    }

    //소문자 카테고리명 입력 시 예외 발생 확인 (개발자)
    @Test
    public void testGetHabitsByDev_lowercaseCategory(){
        //given
        when(userService.tokenToUser(token)).thenReturn(user);

        String lowercaseCategory = "art"; // 소문자

        //when & then
        assertThrows(CreatedTypeOrCategoryNameWrongException.class, ()->{
            habitService.getHabitsByDev(token, lowercaseCategory);
        });
    }

    //세 가지 카테고리 모두 조회 가능한지 확인
    @Test
    public void testGetHabitsByUser_allThreeCategories(){
        //given
        when(userService.tokenToUser(token)).thenReturn(user);

        //ACTIVITY
        when(habitRepository.findByUserAndCreatedTypeAndCategory(user, CreatedType.USER, Categoryname.ACTIVITY))
                .thenReturn(List.of(new Habit(Categoryname.ACTIVITY, "운동", CreatedType.USER)));

        //ART
        when(habitRepository.findByUserAndCreatedTypeAndCategory(user, CreatedType.USER, Categoryname.ART))
                .thenReturn(List.of(new Habit(Categoryname.ART, "그림", CreatedType.USER)));

        //INTELLIGENT
        when(habitRepository.findByUserAndCreatedTypeAndCategory(user, CreatedType.USER, Categoryname.INTELLIGENT))
                .thenReturn(List.of(new Habit(Categoryname.INTELLIGENT, "독서", CreatedType.USER)));

        //when
        HabitDTO.getHabitsApiResponse activityResponse = habitService.getHabitsByUser(token, "ACTIVITY");
        HabitDTO.getHabitsApiResponse artResponse = habitService.getHabitsByUser(token, "ART");
        HabitDTO.getHabitsApiResponse intelligentResponse = habitService.getHabitsByUser(token, "INTELLIGENT");

        //then
        assertEquals("ACTIVITY", activityResponse.getCategory());
        assertEquals("ART", artResponse.getCategory());
        assertEquals("INTELLIGENT", intelligentResponse.getCategory());
    }

    /**[달, 카테고리별 습관 조회]*/
    //정상적으로 습관을 조회하는 경우
    @Test
    public void testGetHabitsByUserAndCategoryAndMonth_success(){
        //given
        when(userService.tokenToUser(token)).thenReturn(user);

        Categoryname category = Categoryname.ACTIVITY;
        int month = 3;

        Habit habit1 = new Habit(category, "아침 운동", CreatedType.USER);
        habit1.setHabitId(1L);
        Habit habit2 = new Habit(category, "저녁 운동", CreatedType.USER);
        habit2.setHabitId(2L);

        List<Habit> habits = List.of(habit1, habit2);

        //when
        when(habitRepository.findByUserAndCategoryAndMonth(user, category, month))
                .thenReturn(habits);

        Map<String, Object> response = habitService.getHabitsByUserAndCategoryAndMonth(token, category, month);

        //then
        verify(userService, times(1)).tokenToUser(token);
        verify(habitRepository, times(1)).findByUserAndCategoryAndMonth(user, category, month);

        assertEquals("ACTIVITY", response.get("category"));
        List<Map<String, Object>> habitsResponse = (List<Map<String, Object>>) response.get("habits");
        assertEquals(2, habitsResponse.size());
        assertEquals(1L, habitsResponse.get(0).get("habit_id"));
        assertEquals("아침 운동", habitsResponse.get(0).get("detail"));
        assertEquals(2L, habitsResponse.get(1).get("habit_id"));
        assertEquals("저녁 운동", habitsResponse.get(1).get("detail"));
    }

    //습관이 없는 경우
    @Test
    public void testGetHabitsByUserAndCategoryAndMonth_noHabits(){
        //given
        when(userService.tokenToUser(token)).thenReturn(user);

        Categoryname category = Categoryname.ART;
        int month = 5;

        //when
        when(habitRepository.findByUserAndCategoryAndMonth(user, category, month))
                .thenReturn(List.of());

        Map<String, Object> response = habitService.getHabitsByUserAndCategoryAndMonth(token, category, month);

        //then
        verify(habitRepository, times(1)).findByUserAndCategoryAndMonth(user, category, month);

        assertEquals("ART", response.get("category"));
        List<Map<String, Object>> habitsResponse = (List<Map<String, Object>>) response.get("habits");
        assertEquals(0, habitsResponse.size());
    }

    //1월 습관 조회
    @Test
    public void testGetHabitsByUserAndCategoryAndMonth_january(){
        //given
        when(userService.tokenToUser(token)).thenReturn(user);

        Categoryname category = Categoryname.INTELLIGENT;
        int month = 1;

        Habit habit = new Habit(category, "독서하기", CreatedType.USER);
        habit.setHabitId(10L);

        //when
        when(habitRepository.findByUserAndCategoryAndMonth(user, category, month))
                .thenReturn(List.of(habit));

        Map<String, Object> response = habitService.getHabitsByUserAndCategoryAndMonth(token, category, month);

        //then
        assertEquals("INTELLIGENT", response.get("category"));
        List<Map<String, Object>> habitsResponse = (List<Map<String, Object>>) response.get("habits");
        assertEquals(1, habitsResponse.size());
        assertEquals(10L, habitsResponse.get(0).get("habit_id"));
        assertEquals("독서하기", habitsResponse.get(0).get("detail"));
    }

    //12월 습관 조회
    @Test
    public void testGetHabitsByUserAndCategoryAndMonth_december(){
        //given
        when(userService.tokenToUser(token)).thenReturn(user);

        Categoryname category = Categoryname.ACTIVITY;
        int month = 12;

        Habit habit = new Habit(category, "요가", CreatedType.USER);
        habit.setHabitId(20L);

        //when
        when(habitRepository.findByUserAndCategoryAndMonth(user, category, month))
                .thenReturn(List.of(habit));

        Map<String, Object> response = habitService.getHabitsByUserAndCategoryAndMonth(token, category, month);

        //then
        assertEquals("ACTIVITY", response.get("category"));
        List<Map<String, Object>> habitsResponse = (List<Map<String, Object>>) response.get("habits");
        assertEquals(1, habitsResponse.size());
        assertEquals(20L, habitsResponse.get(0).get("habit_id"));
        assertEquals("요가", habitsResponse.get(0).get("detail"));
    }

    //여러 카테고리와 월의 조합 테스트
    @Test
    public void testGetHabitsByUserAndCategoryAndMonth_multipleCombinations(){
        //given
        when(userService.tokenToUser(token)).thenReturn(user);

        //3월 ACTIVITY
        Habit habit1 = new Habit(Categoryname.ACTIVITY, "달리기", CreatedType.USER);
        habit1.setHabitId(1L);
        when(habitRepository.findByUserAndCategoryAndMonth(user, Categoryname.ACTIVITY, 3))
                .thenReturn(List.of(habit1));

        //3월 ART
        Habit habit2 = new Habit(Categoryname.ART, "피아노 연습", CreatedType.USER);
        habit2.setHabitId(2L);
        when(habitRepository.findByUserAndCategoryAndMonth(user, Categoryname.ART, 3))
                .thenReturn(List.of(habit2));

        //6월 ACTIVITY
        Habit habit3 = new Habit(Categoryname.ACTIVITY, "수영", CreatedType.USER);
        habit3.setHabitId(3L);
        when(habitRepository.findByUserAndCategoryAndMonth(user, Categoryname.ACTIVITY, 6))
                .thenReturn(List.of(habit3));

        //when
        Map<String, Object> response1 = habitService.getHabitsByUserAndCategoryAndMonth(token, Categoryname.ACTIVITY, 3);
        Map<String, Object> response2 = habitService.getHabitsByUserAndCategoryAndMonth(token, Categoryname.ART, 3);
        Map<String, Object> response3 = habitService.getHabitsByUserAndCategoryAndMonth(token, Categoryname.ACTIVITY, 6);

        //then
        assertEquals("ACTIVITY", response1.get("category"));
        List<Map<String, Object>> habits1 = (List<Map<String, Object>>) response1.get("habits");
        assertEquals(1L, habits1.get(0).get("habit_id"));

        assertEquals("ART", response2.get("category"));
        List<Map<String, Object>> habits2 = (List<Map<String, Object>>) response2.get("habits");
        assertEquals(2L, habits2.get(0).get("habit_id"));

        assertEquals("ACTIVITY", response3.get("category"));
        List<Map<String, Object>> habits3 = (List<Map<String, Object>>) response3.get("habits");
        assertEquals(3L, habits3.get(0).get("habit_id"));
    }

    //같은 카테고리, 같은 월에 여러 습관이 있는 경우
    @Test
    public void testGetHabitsByUserAndCategoryAndMonth_multipleHabitsInSameMonthAndCategory(){
        //given
        when(userService.tokenToUser(token)).thenReturn(user);

        Categoryname category = Categoryname.INTELLIGENT;
        int month = 7;

        Habit habit1 = new Habit(category, "영어 공부", CreatedType.USER);
        habit1.setHabitId(100L);
        Habit habit2 = new Habit(category, "수학 공부", CreatedType.USER);
        habit2.setHabitId(101L);
        Habit habit3 = new Habit(category, "역사 공부", CreatedType.USER);
        habit3.setHabitId(102L);

        List<Habit> habits = List.of(habit1, habit2, habit3);

        //when
        when(habitRepository.findByUserAndCategoryAndMonth(user, category, month))
                .thenReturn(habits);

        Map<String, Object> response = habitService.getHabitsByUserAndCategoryAndMonth(token, category, month);

        //then
        assertEquals("INTELLIGENT", response.get("category"));
        List<Map<String, Object>> habitsResponse = (List<Map<String, Object>>) response.get("habits");
        assertEquals(3, habitsResponse.size());
        assertEquals(100L, habitsResponse.get(0).get("habit_id"));
        assertEquals("영어 공부", habitsResponse.get(0).get("detail"));
        assertEquals(101L, habitsResponse.get(1).get("habit_id"));
        assertEquals("수학 공부", habitsResponse.get(1).get("detail"));
        assertEquals(102L, habitsResponse.get(2).get("habit_id"));
        assertEquals("역사 공부", habitsResponse.get(2).get("detail"));
    }

    //응답 구조 검증
    @Test
    public void testGetHabitsByUserAndCategoryAndMonth_responseStructure(){
        //given
        when(userService.tokenToUser(token)).thenReturn(user);

        Categoryname category = Categoryname.ART;
        int month = 9;

        Habit habit = new Habit(category, "그림 그리기", CreatedType.USER);
        habit.setHabitId(50L);

        //when
        when(habitRepository.findByUserAndCategoryAndMonth(user, category, month))
                .thenReturn(List.of(habit));

        Map<String, Object> response = habitService.getHabitsByUserAndCategoryAndMonth(token, category, month);

        //then
        //응답에 필수 키가 포함되어 있는지 확인
        assertTrue(response.containsKey("category"));
        assertTrue(response.containsKey("habits"));

        //habits가 List<Map> 형태인지 확인
        Object habitsObj = response.get("habits");
        assertTrue(habitsObj instanceof List);

        List<Map<String, Object>> habitsResponse = (List<Map<String, Object>>) habitsObj;
        assertFalse(habitsResponse.isEmpty());

        //각 habit이 habit_id와 detail을 가지고 있는지 확인
        Map<String, Object> firstHabit = habitsResponse.get(0);
        assertTrue(firstHabit.containsKey("habit_id"));
        assertTrue(firstHabit.containsKey("detail"));
    }

    //모든 월(1-12)에 대한 조회 테스트
    @Test
    public void testGetHabitsByUserAndCategoryAndMonth_allMonths(){
        //given
        when(userService.tokenToUser(token)).thenReturn(user);

        Categoryname category = Categoryname.ACTIVITY;

        //모든 월에 대해 습관 설정
        for(int month = 1; month <= 12; month++){
            Habit habit = new Habit(category, month + "월 운동", CreatedType.USER);
            habit.setHabitId((long) month);
            when(habitRepository.findByUserAndCategoryAndMonth(user, category, month))
                    .thenReturn(List.of(habit));
        }

        //when & then
        for(int month = 1; month <= 12; month++){
            Map<String, Object> response = habitService.getHabitsByUserAndCategoryAndMonth(token, category, month);

            assertEquals("ACTIVITY", response.get("category"));
            List<Map<String, Object>> habitsResponse = (List<Map<String, Object>>) response.get("habits");
            assertEquals(1, habitsResponse.size());
            assertEquals((long) month, habitsResponse.get(0).get("habit_id"));
            assertEquals(month + "월 운동", habitsResponse.get(0).get("detail"));
        }

        verify(habitRepository, times(12)).findByUserAndCategoryAndMonth(eq(user), eq(category), anyInt());
    }
}
