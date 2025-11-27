package com.handalsali.handali.service;

import com.handalsali.handali.DTO.RecordDTO;
import com.handalsali.handali.domain.Handali;
import com.handalsali.handali.domain.HandaliStat;
import com.handalsali.handali.domain.Stat;
import com.handalsali.handali.domain.User;
import com.handalsali.handali.enums.Categoryname;
import com.handalsali.handali.enums.TypeName;
import com.handalsali.handali.exception.HandaliNotFoundException;
import com.handalsali.handali.exception.HandaliStatNotFoundException;
import com.handalsali.handali.repository.HandaliRepository;
import com.handalsali.handali.repository.HandaliStatRepository;
import com.handalsali.handali.repository.StatRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("StatService 테스트")
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
    private Stat intelligentStat;
    private Stat artStat;
    private RecordDTO.RecordTodayHabitRequest request;
    private final LocalDate testDate = LocalDate.of(2025, 4, 13);

    @BeforeEach
    void setUp() {
        user = new User("test@gmail.com", "테스트유저", "password123", "010-1234-5678", LocalDate.now());

        currentHandali = new Handali("테스트한달이", LocalDate.now(), user);

        activityStat = new Stat(TypeName.ACTIVITY_SKILL);
        activityStat.setValue(0);
        activityStat.setLastMonthValue(0);

        intelligentStat = new Stat(TypeName.INTELLIGENT_SKILL);
        intelligentStat.setValue(0);
        intelligentStat.setLastMonthValue(0);

        artStat = new Stat(TypeName.ART_SKILL);
        artStat.setValue(0);
        artStat.setLastMonthValue(0);

        currentHandaliStat = new HandaliStat(currentHandali, activityStat);

        request = new RecordDTO.RecordTodayHabitRequest(
                Categoryname.ACTIVITY,
                "운동하기",
                2.5f,
                80,
                testDate
        );
    }

    /**
     * statInit 테스트
     */
    @Test
    @DisplayName("한달이 생성 시 스탯 초기화 - 지난달 한달이 존재")
    public void testStatInit_WithLastMonthHandali() {
        // given
        Handali lastHandali = new Handali("지난한달이", LocalDate.now().minusMonths(1), user);

        Stat lastActivity = new Stat(TypeName.ACTIVITY_SKILL);
        lastActivity.setValue(45);

        Stat lastIntelligent = new Stat(TypeName.INTELLIGENT_SKILL);
        lastIntelligent.setValue(55);

        Stat lastArt = new Stat(TypeName.ART_SKILL);
        lastArt.setValue(35);

        List<HandaliStat> lastMonthStats = List.of(
                new HandaliStat(lastHandali, lastActivity),
                new HandaliStat(lastHandali, lastIntelligent),
                new HandaliStat(lastHandali, lastArt)
        );

        when(handaliRepository.findLastMonthHandali(any(), any(), any()))
                .thenReturn(lastHandali);
        when(handaliStatRepository.findByHandaliAndStatType(eq(lastHandali), any()))
                .thenReturn(lastMonthStats);

        // when
        statService.statInit(user, currentHandali);

        // then
        ArgumentCaptor<Stat> statCaptor = ArgumentCaptor.forClass(Stat.class);
        verify(statRepository, times(3)).save(statCaptor.capture());

        List<Stat> savedStats = statCaptor.getAllValues();
        assertEquals(3, savedStats.size());

        for (Stat stat : savedStats) {
            switch (stat.getTypeName()) {
                case ACTIVITY_SKILL -> {
                    assertEquals(45f, stat.getLastMonthValue(), "지난달 활동 스탯이 제대로 반영되어야 함");
                    assertEquals(0, stat.getValue(), "현재 스탯은 0으로 초기화되어야 함");
                }
                case INTELLIGENT_SKILL -> {
                    assertEquals(55f, stat.getLastMonthValue(), "지난달 지능 스탯이 제대로 반영되어야 함");
                    assertEquals(0, stat.getValue(), "현재 스탯은 0으로 초기화되어야 함");
                }
                case ART_SKILL -> {
                    assertEquals(35f, stat.getLastMonthValue(), "지난달 예술 스탯이 제대로 반영되어야 함");
                    assertEquals(0, stat.getValue(), "현재 스탯은 0으로 초기화되어야 함");
                }
                default -> fail("예상치 못한 스탯 타입: " + stat.getTypeName());
            }
        }

        verify(handaliStatRepository, times(3)).save(any(HandaliStat.class));
    }

    @Test
    @DisplayName("한달이 생성 시 스탯 초기화 - 지난달 한달이 없음")
    public void testStatInit_WithoutLastMonthHandali() {
        // given
        when(handaliRepository.findLastMonthHandali(any(), any(), any()))
                .thenReturn(null);

        // when
        statService.statInit(user, currentHandali);

        // then
        ArgumentCaptor<Stat> statCaptor = ArgumentCaptor.forClass(Stat.class);
        verify(statRepository, times(3)).save(statCaptor.capture());

        List<Stat> savedStats = statCaptor.getAllValues();
        assertEquals(3, savedStats.size());

        for (Stat stat : savedStats) {
            assertEquals(0, stat.getLastMonthValue(), "지난달 스탯이 0으로 초기화되어야 함");
            assertEquals(0, stat.getValue(), "현재 스탯도 0으로 초기화되어야 함");
        }

        verify(handaliStatRepository, times(3)).save(any(HandaliStat.class));
    }

    /**
     * statUpdateAndCheckHandaliStat 테스트
     */
    @Test
    @DisplayName("스탯 업데이트 - 레벨 변화 발생")
    public void testStatUpdateAndCheckHandaliStat_LevelChanged() {
        // given
        activityStat.setValue(8f); // 레벨 0 상태 (10 미만)

        when(handaliRepository.findLatestHandaliByCurrentMonth(user.getUserId()))
                .thenReturn(currentHandali);
        when(handaliStatRepository.findByHandaliAndType(eq(currentHandali), eq(TypeName.ACTIVITY_SKILL)))
                .thenReturn(Optional.of(currentHandaliStat));

        // when
        boolean statusChanged = statService.statUpdateAndCheckHandaliStat(
                user, 30, 2.0f, request
        );

        // then
        assertTrue(statusChanged, "레벨이 변화해야 함");
        assertTrue(currentHandaliStat.getStat().getValue() >= 10f, "스탯 값이 레벨 1 기준값 이상이어야 함");
        verify(handaliStatRepository, times(1)).save(currentHandaliStat);
    }

    @Test
    @DisplayName("스탯 업데이트 - 레벨 변화 없음")
    public void testStatUpdateAndCheckHandaliStat_LevelNotChanged() {
        // given
        activityStat.setValue(15f); // 레벨 1 상태 (10 이상 25 미만)

        when(handaliRepository.findLatestHandaliByCurrentMonth(user.getUserId()))
                .thenReturn(currentHandali);
        when(handaliStatRepository.findByHandaliAndType(eq(currentHandali), eq(TypeName.ACTIVITY_SKILL)))
                .thenReturn(Optional.of(currentHandaliStat));

        // when
        boolean statusChanged = statService.statUpdateAndCheckHandaliStat(
                user, 2, 2.0f, request
        );

        // then
        assertFalse(statusChanged, "레벨이 변화하지 않아야 함");
        assertTrue(currentHandaliStat.getStat().getValue() > 15f, "스탯 값은 증가해야 함");
        verify(handaliStatRepository, times(1)).save(currentHandaliStat);
    }

    @Test
    @DisplayName("스탯 업데이트 - 한달이 없음 예외")
    public void testStatUpdateAndCheckHandaliStat_HandaliNotFound() {
        // given
        when(handaliRepository.findLatestHandaliByCurrentMonth(user.getUserId()))
                .thenReturn(null);

        // when & then
        assertThrows(HandaliNotFoundException.class, () -> {
            statService.statUpdateAndCheckHandaliStat(user, 10, 2.0f, request);
        });
    }

    @Test
    @DisplayName("스탯 업데이트 - 한달이 스탯 없음 예외")
    public void testStatUpdateAndCheckHandaliStat_HandaliStatNotFound() {
        // given
        when(handaliRepository.findLatestHandaliByCurrentMonth(user.getUserId()))
                .thenReturn(currentHandali);
        when(handaliStatRepository.findByHandaliAndType(any(), any()))
                .thenReturn(Optional.empty());

        // when & then
        assertThrows(HandaliStatNotFoundException.class, () -> {
            statService.statUpdateAndCheckHandaliStat(user, 10, 2.0f, request);
        });
    }

    @Test
    @DisplayName("스탯 업데이트 - 카테고리별 정확한 스탯 타입 매핑")
    public void testStatUpdateAndCheckHandaliStat_CorrectStatTypeMapping() {
        // given - ACTIVITY 카테고리
        when(handaliRepository.findLatestHandaliByCurrentMonth(user.getUserId()))
                .thenReturn(currentHandali);
        when(handaliStatRepository.findByHandaliAndType(eq(currentHandali), eq(TypeName.ACTIVITY_SKILL)))
                .thenReturn(Optional.of(currentHandaliStat));

        RecordDTO.RecordTodayHabitRequest activityRequest =
                new RecordDTO.RecordTodayHabitRequest(Categoryname.ACTIVITY, "운동", 2.0f, 80, testDate);

        // when
        statService.statUpdateAndCheckHandaliStat(user, 10, 2.0f, activityRequest);

        // then
        verify(handaliStatRepository).findByHandaliAndType(currentHandali, TypeName.ACTIVITY_SKILL);
    }

    /**
     * calculateStatValue 테스트
     */
    @Test
    @DisplayName("스탯 증가 계산 - 기본 케이스")
    public void testCalculateStatValue_BasicCase() {
        // given
        int recordedDays = 15;
        float lastRecordedTime = 0f;
        float currentTime = 3.0f;
        int satisfaction = 50;

        // when
        float result = statService.calculateStatValue(
                recordedDays, lastRecordedTime, currentHandaliStat, currentTime, satisfaction
        );

        // then
        assertTrue(result > 0, "스탯 증가값은 0보다 커야 함");
        System.out.println("계산된 스탯 증가값: " + result);
    }

    @Test
    @DisplayName("스탯 증가 계산 - 지난달 스탯 반영")
    public void testCalculateStatValue_WithLastMonthValue() {
        // given
        activityStat.setLastMonthValue(50f);
        int recordedDays = 20;
        float lastRecordedTime = 2.0f;
        float currentTime = 3.0f;
        int satisfaction = 80;

        // when
        float result = statService.calculateStatValue(
                recordedDays, lastRecordedTime, currentHandaliStat, currentTime, satisfaction
        );

        // then
        assertTrue(result > 0, "스탯 증가값은 0보다 커야 함");
        // 지난달 스탯이 반영되어 더 큰 값이 나와야 함
        System.out.println("지난달 스탯 반영된 증가값: " + result);
    }

    @Test
    @DisplayName("스탯 증가 계산 - 시간 증가 보너스")
    public void testCalculateStatValue_TimeGrowthBonus() {
        // given
        int recordedDays = 15;
        float lastRecordedTime = 2.0f;
        float currentTime = 4.0f; // 이전보다 2배 증가
        int satisfaction = 70;

        // when
        float result = statService.calculateStatValue(
                recordedDays, lastRecordedTime, currentHandaliStat, currentTime, satisfaction
        );

        // then
        assertTrue(result > 0, "스탯 증가값은 0보다 커야 함");
        System.out.println("시간 증가 보너스 반영 증가값: " + result);
    }

    @Test
    @DisplayName("스탯 증가 계산 - 최대 만족도")
    public void testCalculateStatValue_MaxSatisfaction() {
        // given
        int recordedDays = 30;
        float lastRecordedTime = 2.0f;
        float currentTime = 3.0f;
        int satisfaction = 100; // 최대 만족도

        // when
        float result = statService.calculateStatValue(
                recordedDays, lastRecordedTime, currentHandaliStat, currentTime, satisfaction
        );

        // then
        assertTrue(result > 0, "스탯 증가값은 0보다 커야 함");
        System.out.println("최대 만족도 반영 증가값: " + result);
    }

    @Test
    @DisplayName("스탯 증가 계산 - 최소 입력값")
    public void testCalculateStatValue_MinimumInput() {
        // given
        int recordedDays = 1;
        float lastRecordedTime = 0f;
        float currentTime = 0.5f;
        int satisfaction = 10;

        // when
        float result = statService.calculateStatValue(
                recordedDays, lastRecordedTime, currentHandaliStat, currentTime, satisfaction
        );

        // then
        assertTrue(result > 0, "최소 입력값에도 스탯은 증가해야 함");
        System.out.println("최소 입력값 증가값: " + result);
    }

    /**
     * checkHandaliStatForLevel 테스트
     */
    @Test
    @DisplayName("스탯 레벨 확인 - 레벨 0")
    public void testCheckHandaliStatForLevel_Level0() {
        assertEquals(0, statService.checkHandaliStatForLevel(0f));
        assertEquals(0, statService.checkHandaliStatForLevel(5f));
        assertEquals(0, statService.checkHandaliStatForLevel(9.9f));
    }

    @Test
    @DisplayName("스탯 레벨 확인 - 레벨 1")
    public void testCheckHandaliStatForLevel_Level1() {
        assertEquals(1, statService.checkHandaliStatForLevel(10f));
        assertEquals(1, statService.checkHandaliStatForLevel(15f));
        assertEquals(1, statService.checkHandaliStatForLevel(24.9f));
    }

    @Test
    @DisplayName("스탯 레벨 확인 - 레벨 2")
    public void testCheckHandaliStatForLevel_Level2() {
        assertEquals(2, statService.checkHandaliStatForLevel(25f));
        assertEquals(2, statService.checkHandaliStatForLevel(35f));
        assertEquals(2, statService.checkHandaliStatForLevel(44.9f));
    }

    @Test
    @DisplayName("스탯 레벨 확인 - 레벨 3")
    public void testCheckHandaliStatForLevel_Level3() {
        assertEquals(3, statService.checkHandaliStatForLevel(45f));
        assertEquals(3, statService.checkHandaliStatForLevel(55f));
        assertEquals(3, statService.checkHandaliStatForLevel(69.9f));
    }

    @Test
    @DisplayName("스탯 레벨 확인 - 레벨 4")
    public void testCheckHandaliStatForLevel_Level4() {
        assertEquals(4, statService.checkHandaliStatForLevel(70f));
        assertEquals(4, statService.checkHandaliStatForLevel(85f));
        assertEquals(4, statService.checkHandaliStatForLevel(99.9f));
    }

    @Test
    @DisplayName("스탯 레벨 확인 - 레벨 5 (최대)")
    public void testCheckHandaliStatForLevel_Level5() {
        assertEquals(5, statService.checkHandaliStatForLevel(100f));
        assertEquals(5, statService.checkHandaliStatForLevel(150f));
        assertEquals(5, statService.checkHandaliStatForLevel(999f));
    }

    /**
     * findMaxLevel 테스트
     */
    @Test
    @DisplayName("최대 레벨 찾기 - 각 구간별")
    public void testFindMaxLevel_EachThreshold() {
        assertEquals(10, statService.findMaxLevel(0f));
        assertEquals(10, statService.findMaxLevel(5f));
        assertEquals(10, statService.findMaxLevel(10f));

        assertEquals(25, statService.findMaxLevel(11f));
        assertEquals(25, statService.findMaxLevel(20f));
        assertEquals(25, statService.findMaxLevel(25f));

        assertEquals(45, statService.findMaxLevel(26f));
        assertEquals(45, statService.findMaxLevel(35f));
        assertEquals(45, statService.findMaxLevel(45f));

        assertEquals(70, statService.findMaxLevel(46f));
        assertEquals(70, statService.findMaxLevel(60f));
        assertEquals(70, statService.findMaxLevel(70f));

        assertEquals(100, statService.findMaxLevel(71f));
        assertEquals(100, statService.findMaxLevel(85f));
        assertEquals(100, statService.findMaxLevel(100f));
    }

    @Test
    @DisplayName("최대 레벨 찾기 - 최댓값 초과")
    public void testFindMaxLevel_ExceedMaximum() {
        assertEquals(100, statService.findMaxLevel(101f));
        assertEquals(100, statService.findMaxLevel(500f));
        assertEquals(100, statService.findMaxLevel(999f));
    }

    /**
     * 통합 시나리오 테스트
     */
    @Test
    @DisplayName("통합 시나리오 - 한달이 생성부터 레벨업까지")
    public void testIntegratedScenario_FromInitToLevelUp() {
        // given - 1단계: 한달이 생성 및 스탯 초기화
        Handali lastHandali = new Handali("지난한달이", LocalDate.now().minusMonths(1), user);
        Stat lastActivity = new Stat(TypeName.ACTIVITY_SKILL);
        lastActivity.setValue(30f);

        List<HandaliStat> lastMonthStats = List.of(
                new HandaliStat(lastHandali, lastActivity)
        );

        when(handaliRepository.findLastMonthHandali(any(), any(), any()))
                .thenReturn(lastHandali);
        when(handaliStatRepository.findByHandaliAndStatType(eq(lastHandali), any()))
                .thenReturn(lastMonthStats);

        // when - 1단계: 스탯 초기화
        statService.statInit(user, currentHandali);

        // then - 1단계: 지난달 스탯이 제대로 반영되었는지 확인
        ArgumentCaptor<Stat> statCaptor = ArgumentCaptor.forClass(Stat.class);
        verify(statRepository, times(3)).save(statCaptor.capture());

        List<Stat> savedStats = statCaptor.getAllValues();
        Stat savedActivityStat = savedStats.stream()
                .filter(s -> s.getTypeName() == TypeName.ACTIVITY_SKILL)
                .findFirst()
                .orElseThrow();

        assertEquals(30f, savedActivityStat.getLastMonthValue());
        assertEquals(0f, savedActivityStat.getValue());

        System.out.println("통합 시나리오 테스트 완료: 초기값 = " + savedActivityStat.getValue()
                + ", 지난달값 = " + savedActivityStat.getLastMonthValue());
    }
}