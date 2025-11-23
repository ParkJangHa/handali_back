package com.handalsali.handali.service;

import com.handalsali.handali.domain.*;
import com.handalsali.handali.exception.HandaliNotFoundException;
import com.handalsali.handali.repository.ApartRepository;
import com.handalsali.handali.repository.HandaliRepository;
import com.handalsali.handali.repository.JobRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApartmentServiceTest {

    @Mock
    private HandaliRepository handaliRepository;

    @Mock
    private UserService userService;

    @Mock
    private ApartRepository apartRepository;

    @Mock
    private JobRepository jobRepository;

    @Mock
    private HandbookService handbookService;

    @InjectMocks
    private ApartmentService apartmentService;

    private User testUser;
    private Handali testHandali;
    private Apart testApart;
    private Job testJob;

    @BeforeEach
    void setUp() {
        // 테스트용 User 객체 생성
        testUser = new User("aaa@gmail.com", "name", "1234", "010-1234-5678", LocalDate.now());

        // 테스트용 Job 객체 생성
        testJob = new Job("개발자",100000);

        // 테스트용 Handali 객체 생성
        testHandali = new Handali("3월이", LocalDate.of(2024, 3, 1), testUser);
        testHandali.setJob(testJob);
        testHandali.setImage("image_1_2_3.png");
        testHandali.setBackground("bg.png");
        testHandali.setSofa("sofa.png");
        testHandali.setFloor("floor.png");
        testHandali.setWall("wall.png");

        // 테스트용 Apart 객체 생성
        testApart = new Apart(testUser, testHandali, "3월이", 3, 2024);
        testHandali.setApart(testApart);
    }

    @Test
    @DisplayName("한달이에게 아파트 배정 성공")
    void assignApartmentToHandali_Success() {
        // given
        Handali handali = new Handali("5월이", LocalDate.of(2024, 5, 1), testUser);

        // when
        Apart result = apartmentService.assignApartmentToHandali(handali);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getFloor()).isEqualTo(5);
        assertThat(result.getApartId()).isEqualTo(2024);
        assertThat(result.getNickname()).isEqualTo("5월이");
        verify(apartRepository, times(1)).save(any(Apart.class));
    }

    @Test
    @DisplayName("아파트 내 모든 한달이 조회 성공")
    void getAllHandalisInApartments_Success() {
        // given
        String token = "test-token";
        List<Handali> handaliList = Arrays.asList(testHandali);

        when(userService.tokenToUser(token)).thenReturn(testUser);
        when(handaliRepository.findAllByUser(testUser)).thenReturn(handaliList);

        // when
        List<Map<String, Object>> result = apartmentService.getAllHandalisInApartments(token);

        // then
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);

        Map<String, Object> handaliData = result.get(0);
        assertThat(handaliData.get("apart_id")).isEqualTo(2024);
        assertThat(handaliData.get("floor")).isEqualTo(3);
        assertThat(handaliData.get("nickname")).isEqualTo("3월이");
        assertThat(handaliData.get("job_name")).isEqualTo("개발자");
        assertThat(handaliData.get("week_salary")).isEqualTo(100000);
        assertThat(handaliData.get("image")).isEqualTo("image_1_2_3.png");

        verify(userService, times(1)).tokenToUser(token);
        verify(handaliRepository, times(1)).findAllByUser(testUser);
    }

    @Test
    @DisplayName("아파트 내 한달이 조회 시 한달이가 없으면 예외 발생")
    void getAllHandalisInApartments_NoHandali_ThrowsException() {
        // given
        String token = "test-token";
        when(userService.tokenToUser(token)).thenReturn(testUser);
        when(handaliRepository.findAllByUser(testUser)).thenReturn(Collections.emptyList());

        // when & then
        assertThatThrownBy(() -> apartmentService.getAllHandalisInApartments(token))
                .isInstanceOf(HandaliNotFoundException.class)
                .hasMessage("한달이가 존재하지 않습니다.");
    }

    @Test
    @DisplayName("한달이와 아파트 생성 성공")
    void createHandaliAndApartment_Success() {
        // given
        int year = 2024;
        int month = 6;

        when(apartRepository.findByApartIdAndFloorAndUser(year, month, testUser))
                .thenReturn(Optional.empty());
        when(handaliRepository.findLastMonthHandali(eq(testUser), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(null);
        when(jobRepository.findById(anyLong())).thenReturn(Optional.of(testJob));

        // when
        apartmentService.createHandaliAndApartment(testUser, year, month);

        // then
        verify(apartRepository, times(1)).findByApartIdAndFloorAndUser(year, month, testUser);
        verify(handaliRepository, times(1)).save(any(Handali.class));
        verify(apartRepository, times(1)).save(any(Apart.class));
        verify(handbookService, atLeastOnce()).addHandbook(eq(testUser), anyString());
    }

    @Test
    @DisplayName("이미 존재하는 위치에 아파트 생성 시도 시 예외 발생")
    void createHandaliAndApartment_ApartAlreadyExists_ThrowsException() {
        // given
        int year = 2024;
        int month = 3;

        when(apartRepository.findByApartIdAndFloorAndUser(year, month, testUser))
                .thenReturn(Optional.of(testApart));

        // when & then
        assertThatThrownBy(() -> apartmentService.createHandaliAndApartment(testUser, year, month))
                .isInstanceOf(HandaliNotFoundException.class)
                .hasMessage("이미 해당 위치(2024년 3월)에 아파트가 존재합니다.");

        verify(handaliRepository, never()).save(any());
        verify(apartRepository, never()).save(any());
    }

    @Test
    @DisplayName("해당 년도에 한달이가 이미 존재할 때 예외 발생")
    void createHandaliAndApartment_HandaliAlreadyExists_ThrowsException() {
        // given
        int year = 2024;
        int month = 4;

        when(apartRepository.findByApartIdAndFloorAndUser(year, month, testUser))
                .thenReturn(Optional.empty());
        when(handaliRepository.findLastMonthHandali(eq(testUser), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(testHandali);

        // when & then
        assertThatThrownBy(() -> apartmentService.createHandaliAndApartment(testUser, year, month))
                .isInstanceOf(HandaliNotFoundException.class)
                .hasMessage("해당 년도에 한달이가 이미 존재 합니다.");

        verify(handaliRepository, never()).save(any());
        verify(apartRepository, never()).save(any());
    }

    @Test
    @DisplayName("Job을 찾을 수 없을 때 예외 발생")
    void createHandaliAndApartment_JobNotFound_ThrowsException() {
        // given
        int year = 2024;
        int month = 7;

        when(apartRepository.findByApartIdAndFloorAndUser(year, month, testUser))
                .thenReturn(Optional.empty());
        when(handaliRepository.findLastMonthHandali(eq(testUser), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(null);
        when(jobRepository.findById(anyLong())).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> apartmentService.createHandaliAndApartment(testUser, year, month))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Job을 찾을 수 없습니다");
    }

    @Test
    @DisplayName("여러 한달이가 있을 때 apart_id 기준 정렬 확인")
    void getAllHandalisInApartments_MultipleHandalis_SortedByApartId() {
        // given
        String token = "test-token";

        // 2025년 한달이
        Handali handali2025 = new Handali("1월이", LocalDate.of(2025, 1, 1), testUser);
        Apart apart2025 = new Apart(testUser, handali2025, "1월이", 1, 2025);
        apart2025.setApartId(2025);
        handali2025.setApart(apart2025);
        handali2025.setJob(testJob);

        // 2024년 한달이 (testHandali)
        List<Handali> handaliList = Arrays.asList(handali2025, testHandali);

        when(userService.tokenToUser(token)).thenReturn(testUser);
        when(handaliRepository.findAllByUser(testUser)).thenReturn(handaliList);

        // when
        List<Map<String, Object>> result = apartmentService.getAllHandalisInApartments(token);

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).get("apart_id")).isEqualTo(2024); // 오름차순 정렬
        assertThat(result.get(1).get("apart_id")).isEqualTo(2025);
    }
}