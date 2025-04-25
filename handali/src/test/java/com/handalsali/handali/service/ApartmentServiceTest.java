package com.handalsali.handali.service;

import com.handalsali.handali.domain.Apart;
import com.handalsali.handali.domain.Handali;
import com.handalsali.handali.domain.Job;
import com.handalsali.handali.domain.User;
import com.handalsali.handali.exception.HandaliNotFoundException;
import com.handalsali.handali.repository.HandaliRepository;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ApartmentServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private HandaliRepository handaliRepository;

    @InjectMocks
    private ApartmentService apartmentService;

    //아파트 내 모든 한달이 조회 잘 나오는지 테스트
    @Test
    public void testGetAllHandalisInApartments_Success() {
        // Given
        String token = "valid-token";
        User user = new User(); // 필요한 값 있으면 생성자 추가
        when(userService.tokenToUser(token)).thenReturn(user);

        Job job = new Job("청소부", 5000);
        Handali handali1 = new Handali();
        handali1.setNickname("한달이1");
        handali1.setStartDate(LocalDate.of(2024, 1, 1));
        handali1.setJob(job);
        handali1.setImage("image1.png");

        Apart apart1 = new Apart(user, handali1, "nickname1", 3, 101);
        handali1.setApart(apart1);

        List<Handali> handalis = List.of(handali1);
        when(handaliRepository.findAllByUser(user)).thenReturn(handalis);

        // When
        List<Map<String, Object>> result = apartmentService.getAllHandalisInApartments(token);

        // Then
        assertEquals(1, result.size());
        Map<String, Object> handaliMap = result.get(0);
        assertEquals(101, handaliMap.get("apart_id"));
        assertEquals(3, handaliMap.get("floor"));
        assertEquals("한달이1", handaliMap.get("nickname"));
        assertEquals(LocalDate.of(2024, 1, 1), handaliMap.get("start_date"));
        assertEquals("청소부", handaliMap.get("job_name"));
        assertEquals(5000, handaliMap.get("week_salary"));
        assertEquals("image1.png", handaliMap.get("image"));
    }

    //한달이가 없을 때 예외 케이스 확인
    @Test
    public void testGetAllHandalisInApartments_NotFound() {
        // Given
        String token = "valid-token";
        User mockUser = new User();
        when(userService.tokenToUser(token)).thenReturn(mockUser);
        when(handaliRepository.findAllByUser(mockUser)).thenReturn(List.of());

        // Then + When
        assertThrows(HandaliNotFoundException.class, () -> {
            apartmentService.getAllHandalisInApartments(token);
        });
    }
}
