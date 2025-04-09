package com.handalsali.handali.service;

import com.handalsali.handali.DTO.HabitDTO;
import com.handalsali.handali.domain.Habit;
import com.handalsali.handali.domain.User;
import com.handalsali.handali.enums_multyKey.Categoryname;
import com.handalsali.handali.enums_multyKey.CreatedType;
import com.handalsali.handali.exception.CreatedTypeOrCategoryNameWrongException;
import com.handalsali.handali.repository.HabitRepository;
import com.handalsali.handali.repository.UserHabitRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HabitServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private HabitRepository habitRepository;

    @Mock
    private UserHabitRepository userHabitRepository;

    @InjectMocks
    private HabitService habitService;

    //사용자 습관 조회 테스트
    @Test
    public void testGetHabitsByUser_Success() {
        // Given
        String token = "valid-token";
        String category = "ACTIVITY"; // enum으로 존재하는 값
        Categoryname categoryEnum = Categoryname.valueOf(category);

        User user = new User("test@example.com", "Tester", "pw", "010-0000-0000", LocalDate.now());

        Habit habit1 = new Habit(categoryEnum, "명상하기", CreatedType.USER);
        Habit habit2 = new Habit(categoryEnum, "감사일기 쓰기", CreatedType.USER);

        List<Habit> habits = List.of(habit1, habit2);

        when(userService.tokenToUser(token)).thenReturn(user);
        when(habitRepository.findByUserAndCreatedTypeAndCategory(user, CreatedType.USER, categoryEnum)).thenReturn(habits);

        // When
        HabitDTO.getHabitsApiResponse response = habitService.getHabitsByUser(token, category);

        // Then
        assertEquals(category, response.getCategory());
        assertEquals(2, response.getHabits().size());
        assertEquals("명상하기", response.getHabits().get(0).getDetail());
        assertEquals("감사일기 쓰기", response.getHabits().get(1).getDetail());
    }

    //개발자 습관 조회 테스트
    @Test
    public void testGetHabitsByDev_Success() {
        // Given
        String token = "dev-token";
        String category = "ACTIVITY";
        Categoryname categoryEnum = Categoryname.valueOf(category);

        Habit habit1 = new Habit(categoryEnum, "운동하기", CreatedType.DEVELOPER);
        when(userService.tokenToUser(token)).thenReturn(new User());
        when(habitRepository.findByCreatedTypeAndCategoryName(CreatedType.DEVELOPER, categoryEnum))
                .thenReturn(List.of(habit1));

        // When
        HabitDTO.getHabitsApiResponse response = habitService.getHabitsByDev(token, category);

        // Then
        assertEquals(category, response.getCategory());
        assertEquals(1, response.getHabits().size());
        assertEquals("운동하기", response.getHabits().get(0).getDetail());
    }

    //존재하지 않은 category를 넣었을 때 예외 발생 하는지 테스트
    @Test
    public void testGetHabitsByUser_InvalidCategory_ThrowsException() {
        String token = "valid-token";
        String invalidCategory = "NOT_EXISTING";

        when(userService.tokenToUser(token)).thenReturn(new User());

        assertThrows(CreatedTypeOrCategoryNameWrongException.class, () -> {
            habitService.getHabitsByUser(token, invalidCategory);
        });
    }

    // 달별 습관 조회 코드 테스트
    @Test
    public void testGetHabitsByUserAndCategoryAndMonth_Success() {
        // Given
        String token = "valid-token";
        int month = 3;
        Categoryname category = Categoryname.ART;

        User mockUser = new User(); // 사용자 객체는 최소 생성
        when(userService.tokenToUser(token)).thenReturn(mockUser);

        Habit habit1 = new Habit();
        habit1.setHabitId(1L);
        habit1.setDetailedHabitName("걷기 30분");

        Habit habit2 = new Habit();
        habit2.setHabitId(2L);
        habit2.setDetailedHabitName("물 2L 마시기");

        List<Habit> habitList = List.of(habit1, habit2);
        when(habitRepository.findByUserAndCategoryAndMonth(mockUser, category, month))
                .thenReturn(habitList);

        // When
        Map<String, Object> result = habitService.getHabitsByUserAndCategoryAndMonth(token, category, month);

        // Then
        assertEquals(category.name(), result.get("category"));

        List<Map<String, Object>> habitsResponse = (List<Map<String, Object>>) result.get("habits");
        assertEquals(2, habitsResponse.size());

        assertEquals(1L, habitsResponse.get(0).get("habit_id"));
        assertEquals("걷기 30분", habitsResponse.get(0).get("detail"));

        assertEquals(2L, habitsResponse.get(1).get("habit_id"));
        assertEquals("물 2L 마시기", habitsResponse.get(1).get("detail"));

        verify(userService).tokenToUser(token);
        verify(habitRepository).findByUserAndCategoryAndMonth(mockUser, category, month);
    }

    //해당 월에 습관이 없는 경우 테스트
    @Test
    public void testGetHabitsByUserAndCategoryAndMonth_NoHabits() {
        // Given
        String token = "valid-token";
        int month = 4;
        Categoryname category = Categoryname.ART;

        User mockUser = new User();
        when(userService.tokenToUser(token)).thenReturn(mockUser);
        when(habitRepository.findByUserAndCategoryAndMonth(mockUser, category, month))
                .thenReturn(List.of()); // 습관 없음

        // When
        Map<String, Object> result = habitService.getHabitsByUserAndCategoryAndMonth(token, category, month);

        // Then
        assertEquals(category.name(), result.get("category"));

        List<Map<String, Object>> habitsResponse = (List<Map<String, Object>>) result.get("habits");
        assertEquals(0, habitsResponse.size()); // 리스트가 비어 있어야 함

        verify(userService).tokenToUser(token);
        verify(habitRepository).findByUserAndCategoryAndMonth(mockUser, category, month);
    }
}
