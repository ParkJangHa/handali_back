package com.handalsali.handali.service;

import com.handalsali.handali.DTO.HabitDTO;
import com.handalsali.handali.domain.Habit;
import com.handalsali.handali.domain.User;
import com.handalsali.handali.domain.UserHabit;
import com.handalsali.handali.enums_multyKey.Categoryname;
import com.handalsali.handali.enums_multyKey.CreatedType;
import com.handalsali.handali.exception.CreatedTypeOrCategoryNameWrongException;
import com.handalsali.handali.repository.HabitRepository;
import com.handalsali.handali.repository.UserHabitRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class HabitServiceTest {

    @Mock
    private HabitRepository habitRepository;

    @InjectMocks
    private HabitService habitService;

    @Mock
    private UserService userService; // UserService를 Mock으로 추가

    private String token;
    private User testUser;

    @BeforeEach
    void setUp() {
        token = "valid_token"; // 테스트용 토큰 값 설정
        testUser = new User(); // 가상의 User 객체 생성

        when(userService.tokenToUser(token)).thenReturn(testUser);
    }

    /** 개발자의 특정 카테고리 습관 조회 */
    @Test
    void getHabitsByDev_ShouldReturnHabits_WhenCategoryIsValid() {
        // given
        String category = Categoryname.ACTIVITY.toString();
        System.out.println("category = " + category);
        List<Habit> habits = List.of(
                new Habit(Categoryname.ACTIVITY, "서핑", CreatedType.DEVELOPER),
                new Habit(Categoryname.ACTIVITY, "요가", CreatedType.DEVELOPER)
        );

        when(habitRepository.findByCreatedTypeAndCategoryName(CreatedType.DEVELOPER, Categoryname.ACTIVITY))
                .thenReturn(habits);

        // when
        HabitDTO.getHabitsApiResponse response = habitService.getHabitsByDev(token, category);

        // then
        assertNotNull(response);
        assertEquals(category, response.getCategory());
        assertEquals(2, response.getHabits().size());
        assertEquals("서핑", response.getHabits().get(0).getDetail());
        assertEquals("요가", response.getHabits().get(1).getDetail());
    }

    /** ✅ 잘못된 카테고리 입력 시 예외 발생 */
    @Test
    void getHabitsByDev_ShouldThrowException_WhenCategoryIsInvalid() {
        // given
        String invalidCategory = "INVALID";

        // when & then
        assertThrows(CreatedTypeOrCategoryNameWrongException.class, () -> {
            habitService.getHabitsByDev(token,invalidCategory);
        });
    }

    /** ✅ INTELLIGENT 카테고리 조회 테스트 */
    @Test
    void getHabitsByDev_ShouldReturnHabits_WhenCategoryIsIntelligent() {
        // given
        String category = "INTELLIGENT";
        List<Habit> habits = List.of(
                new Habit(Categoryname.INTELLIGENT, "독서", CreatedType.DEVELOPER),
                new Habit(Categoryname.INTELLIGENT, "퍼즐 풀기", CreatedType.DEVELOPER)
        );

        when(habitRepository.findByCreatedTypeAndCategoryName(CreatedType.DEVELOPER, Categoryname.INTELLIGENT))
                .thenReturn(habits);

        // when
        HabitDTO.getHabitsApiResponse response = habitService.getHabitsByDev(token, category);

        // then
        assertNotNull(response);
        assertEquals(category, response.getCategory());
        assertEquals(2, response.getHabits().size());
        assertEquals("독서", response.getHabits().get(0).getDetail());
        assertEquals("퍼즐 풀기", response.getHabits().get(1).getDetail());
    }

    /** ✅ ART 카테고리 조회 테스트 */
    @Test
    void getHabitsByDev_ShouldReturnHabits_WhenCategoryIsArt() {
        // given
        String category = "ART";
        List<Habit> habits = List.of(
                new Habit(Categoryname.ART, "드로잉", CreatedType.DEVELOPER),
                new Habit(Categoryname.ART, "음악 감상", CreatedType.DEVELOPER)
        );

        when(habitRepository.findByCreatedTypeAndCategoryName(CreatedType.DEVELOPER, Categoryname.ART))
                .thenReturn(habits);

        // when
        HabitDTO.getHabitsApiResponse response = habitService.getHabitsByDev(token, category);

        // then
        assertNotNull(response);
        assertEquals(category, response.getCategory());
        assertEquals(2, response.getHabits().size());
        assertEquals("드로잉", response.getHabits().get(0).getDetail());
        assertEquals("음악 감상", response.getHabits().get(1).getDetail());
    }
}
