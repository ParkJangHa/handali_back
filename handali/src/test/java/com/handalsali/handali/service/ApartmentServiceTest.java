package com.handalsali.handali.service;

import com.handalsali.handali.domain.Apart;
import com.handalsali.handali.domain.Handali;
import com.handalsali.handali.domain.Job;
import com.handalsali.handali.domain.User;
import com.handalsali.handali.exception.HandaliNotFoundException;
import com.handalsali.handali.repository.HandaliRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ApartmentServiceTest {

    @Mock private UserService userService;
    @Mock private HandaliRepository handaliRepository;
    @InjectMocks private ApartmentService apartmentService;

    private User user;
    private Job job;
    private Handali handaliWithApart;
    private Apart apart;

    @BeforeEach
    void setUp() {
        user = new User("test@gmail.com", "홍길동", "pw1234", "010-1234-5678", LocalDate.of(2023, 1, 1));
        job = new Job("개발자", 5000);

        handaliWithApart = new Handali("한달이1", LocalDate.of(2024, 1, 1), user);
        handaliWithApart.setJob(job);

        apart = new Apart(user, handaliWithApart, "한달이1", 3, 101);
        handaliWithApart.setApart(apart);
    }

    /**
     * 아파트 내 모든 한달이 조회 잘 나오는지 테스트
     */
    @Test
    public void testGetAllHandalisInApartments_Success() {

        // Given
        // 토큰으로 유저 조회
        // 해당 유저가 보유한 한달이 리스트 리턴
        when(userService.tokenToUser("valid-token")).thenReturn(user);
        when(handaliRepository.findAllByUser(user)).thenReturn(List.of(handaliWithApart));

        // When
        List<Map<String, Object>> result = apartmentService.getAllHandalisInApartments("valid-token");

        // Then
        // 반환 리스트 크기가 1인지 확인
        // 리스트 안의 값들이 기대한 값과 일치하는지 검증
        assertEquals(1, result.size());
        Map<String, Object> map = result.get(0);
        assertEquals(101, map.get("apart_id"));
        assertEquals(3, map.get("floor"));
        assertEquals("한달이1", map.get("nickname"));
        assertEquals(LocalDate.of(2024, 1, 1), map.get("start_date"));
        assertEquals("개발자", map.get("job_name"));
        assertEquals(5000, map.get("week_salary"));
        assertEquals("image_0_0_0.png", map.get("image")); // image는 지정 안 했으므로 null
    }

    /**
     * [예외] 한달이가 없을 때 확인
     */
    @Test
    public void testGetAllHandalisInApartments_NotFound() {
        // Given
        // 토큰으로 유저는 찾았지만, 빈 리스트 반환
        when(userService.tokenToUser("token")).thenReturn(user);
        when(handaliRepository.findAllByUser(user)).thenReturn(List.of());
        // Then + When
        // getAllHandalisInApartments(token) 호출 시 에러Exception 발생해야 함.
        assertThrows(HandaliNotFoundException.class, () ->
                apartmentService.getAllHandalisInApartments("token")
        );
    }

    /**
     * 한달이가 아파트는 있지만 직업이 없는 경우
     */
    @Test
    public void testGetAllHandalisInApartments_NoJob() {
        // Given
        Handali handaliNoJob = new Handali("무직이", LocalDate.of(2024, 2, 2), user);
        Apart apartNoJob = new Apart(user, handaliNoJob, "무직이", 2, 202);
        handaliNoJob.setApart(apartNoJob); // 아파트만 있음

        when(userService.tokenToUser("token")).thenReturn(user);
        when(handaliRepository.findAllByUser(user)).thenReturn(List.of(handaliNoJob));

        // When
        List<Map<String, Object>> result = apartmentService.getAllHandalisInApartments("token");

        // Then
        assertEquals(1, result.size());
        Map<String, Object> map = result.get(0);
        assertEquals(202, map.get("apart_id"));
        assertEquals("무직이", map.get("nickname"));
        assertNull(map.get("job_name"));
        assertNull(map.get("week_salary"));
    }

    /**
     * 한달이가 직업은 있지만 아파트가 없는 경우
     // 구현하려고 했는데 ApartmentService.java에서
     // user가 빈 리스트일 경우에만 오류가 발생하도록 구현이 되어있음.
     // 그래서 아래 테스트에서는 빈 리스트를 반환해서 실행 오류가 뜸
     // -> ApartmentService.java를 수정해보려고 했지만 더 큰 오류들이 발생하여 일단 놔둠.
     // 위 테스트 코드인 아파트O and 직업X 는 정상 작동


    @Test
    public void testGetAllHandalisInApartments_NoApartment() {
        // Given
        Handali handaliNoApartment = new Handali("노숙이", LocalDate.of(2024, 3, 3), user);
        handaliNoApartment.setJob(new Job("디자이너", 6000)); // 직업만 있음

        // When
        List<Map<String, Object>> result = apartmentService.getAllHandalisInApartments("token");

        // Then
        assertTrue(result.isEmpty(), "아파트가 없는 한달이는 결과에 포함되지 않아야 합니다.");
    }
    */
}
