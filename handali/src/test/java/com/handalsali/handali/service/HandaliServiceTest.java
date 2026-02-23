package com.handalsali.handali.service;

import com.handalsali.handali.DTO.HandaliDTO;
import com.handalsali.handali.domain.*;
import com.handalsali.handali.enums.ItemType;
import com.handalsali.handali.enums.TypeName;
import com.handalsali.handali.exception.HanCreationLimitException;
import com.handalsali.handali.exception.HandaliNotFoundException;
import com.handalsali.handali.repository.HandaliRepository;
import com.handalsali.handali.repository.HandaliStatRepository;
import com.handalsali.handali.repository.UserItemRepository;
import com.handalsali.handali.scheduler.HandaliScheduler;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HandaliServiceTest {

    @InjectMocks
    private HandaliService handaliService;

    @Mock
    private HandaliRepository handaliRepository;

    @Mock
    private UserService userService;

    @Mock
    private HandaliStatRepository handaliStatRepository;

    @Mock
    private StatService statService;

    @Mock
    private UserItemRepository userItemRepository;

    @Mock
    private HandbookService handbookService;

    @Mock
    private HandaliScheduler handaliScheduler;

    private String token;
    private User user;
    private Handali handali;

    @BeforeEach
    void setUp() {
        token = "test-token";
        user = new User("aaa@gmail.com", "name", "1234", "010-1234-5678", LocalDate.now());
        handali = new Handali("aaa", LocalDate.now(), user);
    }

    /** [한달이 상태 변화] */
    @Test
    public void testChangeHandali() {
        when(userService.tokenToUser(token)).thenReturn(user);
        when(handaliRepository.findLatestHandaliByCurrentMonth(user.getUserId())).thenReturn(handali);

        int activityValue = 100;
        int intelligentValue = 1500;
        int artValue = 0;
        List<HandaliStat> stats = getHandaliStats(activityValue, intelligentValue, artValue);

        when(handaliStatRepository.findByHandali(handali)).thenReturn(stats);
        when(statService.checkHandaliStatForLevel(activityValue)).thenReturn(1);
        when(statService.checkHandaliStatForLevel(intelligentValue)).thenReturn(5);
        when(statService.checkHandaliStatForLevel(artValue)).thenReturn(0);

        String image = handaliService.changeHandali(token);

        assertEquals("image_1_5_0.png", image);
        assertEquals("image_1_5_0.png", handali.getImage());
        verify(handaliRepository).save(handali);
        verify(handbookService).addHandbook(user, "image_1_5_0.png");
    }

    private List<HandaliStat> getHandaliStats(int activityValue, int intelligentValue, int artValue) {
        Stat activityStat = new Stat(TypeName.ACTIVITY_SKILL);
        activityStat.setValue(activityValue);
        Stat intelligentStat = new Stat(TypeName.INTELLIGENT_SKILL);
        intelligentStat.setValue(intelligentValue);
        Stat artStat = new Stat(TypeName.ART_SKILL);
        artStat.setValue(artValue);

        return List.of(
                new HandaliStat(handali, activityStat),
                new HandaliStat(handali, intelligentStat),
                new HandaliStat(handali, artStat)
        );
    }

    /** [한달이 생성] */
    @Test
    public void testHandaliCreate() {
        when(userService.tokenToUser(token)).thenReturn(user);
        when(handaliRepository.countPetsByUserIdAndCurrentMonth(user)).thenReturn(0L);

        handaliService.handaliCreate(token, "aaa");

        ArgumentCaptor<Handali> handaliCaptor = ArgumentCaptor.forClass(Handali.class);
        verify(handaliRepository).save(handaliCaptor.capture());
        Handali handali = handaliCaptor.getValue();

        assertEquals("aaa", handali.getNickname());
        assertEquals(user, handali.getUser());
        assertEquals(LocalDate.now(), handali.getStartDate());
    }

    @Test
    public void testHandaliCreate_error() {
        when(userService.tokenToUser(token)).thenReturn(user);
        when(handaliRepository.countPetsByUserIdAndCurrentMonth(user)).thenReturn(1L);

        assertThrows(HanCreationLimitException.class, () -> {
            handaliService.handaliCreate(token, "aaa");
        });
    }

    /** [마지막 생성 한달이 조회] */
    @Test
    public void testGetRecentHandali_Success() {
        String token = "valid-token";
        User user = new User();
        user.setUserId(1L);

        Handali handali = new Handali();
        handali.setHandaliId(10L);
        handali.setNickname("테스트한달이");
        handali.setStartDate(LocalDate.of(2025, 4, 1));
        handali.setImage("image.png");
        handali.setUser(user);

        Job job = new Job();
        job.setName("개발자");
        job.setWeekSalary(500);
        handali.setJob(job);

        when(userService.tokenToUser(token)).thenReturn(user);
        when(handaliRepository.findLatestHandaliByUser(user.getUserId())).thenReturn(Optional.of(handali));

        HandaliDTO.RecentHandaliResponse response = handaliService.getRecentHandali(token);

        assertEquals("테스트한달이", response.getNickname());
        assertEquals(10L, response.getHandali_id());
        assertEquals(LocalDate.of(2025, 4, 1), response.getStart_date());
        assertEquals("개발자", response.getJob_name());
        assertEquals(500, response.getSalary());
        assertEquals("image.png", response.getImage());

        verify(userService).tokenToUser(token);
        verify(handaliRepository).findLatestHandaliByUser(user.getUserId());
    }

    @Test
    public void testGetRecentHandali_NoUser() {
        String token = "invalid-token";
        when(userService.tokenToUser(token)).thenReturn(null);

        assertThrows(HandaliNotFoundException.class, () -> handaliService.getRecentHandali(token));
        verify(userService).tokenToUser(token);
        verifyNoInteractions(handaliRepository);
    }

    @Test
    public void testGetRecentHandali_NoHandali() {
        String token = "valid-token";
        User user = new User();
        user.setUserId(1L);

        when(userService.tokenToUser(token)).thenReturn(user);
        when(handaliRepository.findLatestHandaliByUser(user.getUserId())).thenReturn(Optional.empty());

        assertThrows(HandaliNotFoundException.class, () -> handaliService.getRecentHandali(token));
        verify(userService).tokenToUser(token);
        verify(handaliRepository).findLatestHandaliByUser(user.getUserId());
    }

    /** [한달이 상태 조회] */
    @Test
    public void testGetHandaliStatusByMonth_Success() {
        String token = "valid-token";
        User user = new User();
        user.setTotal_coin(300);

        Handali handali = new Handali();
        handali.setNickname("테스트한달이");
        handali.setStartDate(LocalDate.now().minusDays(4));
        handali.setUser(user);
        handali.setImage("test-image.png");

        Stat activityStat = new Stat(TypeName.ACTIVITY_SKILL);
        activityStat.setValue(50.0f);
        Stat intelligentStat = new Stat(TypeName.INTELLIGENT_SKILL);
        intelligentStat.setValue(70.0f);
        Stat artStat = new Stat(TypeName.ART_SKILL);
        artStat.setValue(30.0f);

        HandaliStat handaliActivityStat = new HandaliStat(handali, activityStat);
        HandaliStat handaliIntelligentStat = new HandaliStat(handali, intelligentStat);
        HandaliStat handaliArtStat = new HandaliStat(handali, artStat);

        when(userService.tokenToUser(token)).thenReturn(user);
        when(handaliRepository.findLatestHandaliByCurrentMonth(user.getUserId())).thenReturn(handali);
        when(userItemRepository.findByUserAndItemType(user, ItemType.BACKGROUND)).thenReturn(Optional.empty());
        when(userItemRepository.findByUserAndItemType(user, ItemType.WALL)).thenReturn(Optional.empty());
        when(userItemRepository.findByUserAndItemType(user, ItemType.SOFA)).thenReturn(Optional.empty());
        when(userItemRepository.findByUserAndItemType(user, ItemType.FLOOR)).thenReturn(Optional.empty());
        when(handaliStatRepository.findByHandaliAndType(handali, TypeName.ACTIVITY_SKILL))
                .thenReturn(Optional.of(handaliActivityStat));
        when(handaliStatRepository.findByHandaliAndType(handali, TypeName.INTELLIGENT_SKILL))
                .thenReturn(Optional.of(handaliIntelligentStat));
        when(handaliStatRepository.findByHandaliAndType(handali, TypeName.ART_SKILL))
                .thenReturn(Optional.of(handaliArtStat));
        when(statService.findMaxLevel(50.0f)).thenReturn(200);
        when(statService.findMaxLevel(70.0f)).thenReturn(300);
        when(statService.findMaxLevel(30.0f)).thenReturn(100);

        HandaliDTO.HandaliStatusResponse response = handaliService.getHandaliStatusByMonth(token);

        assertEquals("테스트한달이", response.getNickname());
        assertEquals(5, response.getDays_since_created());
        assertEquals(300, response.getTotal_coin());
        assertEquals("test-image.png", response.getHandali_img());
        assertEquals("none", response.getBackground_img());
        assertEquals("none", response.getWall_img());
        assertEquals("none", response.getSofa_img());
        assertEquals("none", response.getFloor_img());
        assertEquals(50.0f, response.getActivity_value());
        assertEquals(70.0f, response.getIntelligence_value());
        assertEquals(30.0f, response.getArt_value());
        assertEquals(200, response.getMax_stat_activity());
        assertEquals(300, response.getMax_stat_intelligence());
        assertEquals(100, response.getMax_stat_art());

        verify(userService).tokenToUser(token);
        verify(handaliRepository).findLatestHandaliByCurrentMonth(user.getUserId());
        verify(handaliStatRepository).findByHandaliAndType(handali, TypeName.ACTIVITY_SKILL);
        verify(handaliStatRepository).findByHandaliAndType(handali, TypeName.INTELLIGENT_SKILL);
        verify(handaliStatRepository).findByHandaliAndType(handali, TypeName.ART_SKILL);
    }

    @Test
    public void testGetHandaliStatusByMonth_NoHandali() {
        when(userService.tokenToUser(token)).thenReturn(user);
        when(handaliRepository.findLatestHandaliByCurrentMonth(user.getUserId())).thenReturn(null);

        assertThrows(HandaliNotFoundException.class, () -> {
            handaliService.getHandaliStatusByMonth(token);
        });
    }

    @Test
    public void testGetHandaliStatusByMonth_WithUserItems() {
        User user = new User();
        user.setTotal_coin(500);

        Handali handali = new Handali();
        handali.setNickname("부자한달이");
        handali.setStartDate(LocalDate.now().minusDays(10));
        handali.setUser(user);
        handali.setImage("image_2_3_1.png");

        Store backgroundStore = new Store();
        backgroundStore.setName("예쁜배경");
        backgroundStore.setItemType(ItemType.BACKGROUND);

        Store sofaStore = new Store();
        sofaStore.setName("고급소파");
        sofaStore.setItemType(ItemType.SOFA);

        UserItem userBackground = new UserItem(user, backgroundStore);
        UserItem userSofa = new UserItem(user, sofaStore);

        when(userService.tokenToUser(token)).thenReturn(user);
        when(handaliRepository.findLatestHandaliByCurrentMonth(user.getUserId())).thenReturn(handali);
        when(userItemRepository.findByUserAndItemType(user, ItemType.BACKGROUND))
                .thenReturn(Optional.of(userBackground));
        when(userItemRepository.findByUserAndItemType(user, ItemType.WALL))
                .thenReturn(Optional.empty());
        when(userItemRepository.findByUserAndItemType(user, ItemType.SOFA))
                .thenReturn(Optional.of(userSofa));
        when(userItemRepository.findByUserAndItemType(user, ItemType.FLOOR))
                .thenReturn(Optional.empty());

        Stat activityStat = new Stat(TypeName.ACTIVITY_SKILL);
        activityStat.setValue(80.0f);
        Stat intelligentStat = new Stat(TypeName.INTELLIGENT_SKILL);
        intelligentStat.setValue(90.0f);
        Stat artStat = new Stat(TypeName.ART_SKILL);
        artStat.setValue(40.0f);

        when(handaliStatRepository.findByHandaliAndType(handali, TypeName.ACTIVITY_SKILL))
                .thenReturn(Optional.of(new HandaliStat(handali, activityStat)));
        when(handaliStatRepository.findByHandaliAndType(handali, TypeName.INTELLIGENT_SKILL))
                .thenReturn(Optional.of(new HandaliStat(handali, intelligentStat)));
        when(handaliStatRepository.findByHandaliAndType(handali, TypeName.ART_SKILL))
                .thenReturn(Optional.of(new HandaliStat(handali, artStat)));
        when(statService.findMaxLevel(80.0f)).thenReturn(400);
        when(statService.findMaxLevel(90.0f)).thenReturn(500);
        when(statService.findMaxLevel(40.0f)).thenReturn(150);

        HandaliDTO.HandaliStatusResponse response = handaliService.getHandaliStatusByMonth(token);

        assertEquals("부자한달이", response.getNickname());
        assertEquals(11, response.getDays_since_created());
        assertEquals("예쁜배경", response.getBackground_img());
        assertEquals("none", response.getWall_img());
        assertEquals("고급소파", response.getSofa_img());
        assertEquals("none", response.getFloor_img());
    }

    @Test
    public void testGetHandaliStatusByMonth_WithZeroStats() {
        User user = new User();
        user.setTotal_coin(0);

        Handali handali = new Handali();
        handali.setNickname("신생한달이");
        handali.setStartDate(LocalDate.now());
        handali.setUser(user);
        handali.setImage("image_0_0_0.png");

        when(userService.tokenToUser(token)).thenReturn(user);
        when(handaliRepository.findLatestHandaliByCurrentMonth(user.getUserId())).thenReturn(handali);
        when(userItemRepository.findByUserAndItemType(eq(user), any())).thenReturn(Optional.empty());
        when(handaliStatRepository.findByHandaliAndType(handali, TypeName.ACTIVITY_SKILL))
                .thenReturn(Optional.empty());
        when(handaliStatRepository.findByHandaliAndType(handali, TypeName.INTELLIGENT_SKILL))
                .thenReturn(Optional.empty());
        when(handaliStatRepository.findByHandaliAndType(handali, TypeName.ART_SKILL))
                .thenReturn(Optional.empty());
        when(statService.findMaxLevel(0.0f)).thenReturn(0);

        HandaliDTO.HandaliStatusResponse response = handaliService.getHandaliStatusByMonth(token);

        assertEquals("신생한달이", response.getNickname());
        assertEquals(1, response.getDays_since_created());
        assertEquals(0.0f, response.getActivity_value());
        assertEquals(0.0f, response.getIntelligence_value());
        assertEquals(0.0f, response.getArt_value());
        assertEquals(0, response.getMax_stat_activity());
        assertEquals(0, response.getMax_stat_intelligence());
        assertEquals(0, response.getMax_stat_art());
    }

    /** [주급 계산] */

    @Test
    public void testGetWeekSalaryInfo_singleHandaliWithJob() {
        when(userService.tokenToUser(token)).thenReturn(user);

        Job job = new Job();
        job.setName("개발자");

        Handali handali = new Handali("5월이", LocalDate.of(2024, 5, 1), user);
        handali.setHandaliId(1L);  // ✅ 추가!
        handali.setJob(job);
        handali.setStartDate(LocalDate.of(2024, 11, 1));

        List<Handali> handalis = List.of(handali);
        List<HandaliStat> handaliStats = createHandaliStatsForSalary(handali, 50.0f, 70.0f, 30.0f);
        List<TypeName> typeNames = List.of(TypeName.ACTIVITY_SKILL, TypeName.INTELLIGENT_SKILL, TypeName.ART_SKILL);

        when(handaliRepository.findByUserAndJobIsNotNullWithJob(user)).thenReturn(handalis);
        when(handaliScheduler.calculateSalaryFor(handali)).thenReturn(5000);
        when(handaliStatRepository.findByHandaliInAndStatType(handalis, typeNames))
                .thenReturn(handaliStats);
        when(statService.checkHandaliStatForLevel(50.0f)).thenReturn(2);
        when(statService.checkHandaliStatForLevel(70.0f)).thenReturn(3);
        when(statService.checkHandaliStatForLevel(30.0f)).thenReturn(1);

        HandaliDTO.GetWeekSalaryApiResponseDTO response = handaliService.getWeekSalaryInfo(token);

        verify(userService, times(1)).tokenToUser(token);
        verify(handaliRepository, times(1)).findByUserAndJobIsNotNullWithJob(user);
        verify(handaliScheduler, times(1)).calculateSalaryFor(handali);
        verify(handaliStatRepository, times(1)).findByHandaliInAndStatType(handalis, typeNames);

        assertEquals(1, response.getHandalis_salary().size());
        assertEquals(5000, response.getTotal_salary());
        assertEquals(1, response.getTotal_handali());

        HandaliDTO.GetWeekSalaryResponseDTO handaliResponse = response.getHandalis_salary().get(0);
        assertEquals("5월이", handaliResponse.getNickname());
        assertEquals("개발자", handaliResponse.getJob());
        assertEquals(5000, handaliResponse.getSalary());
        assertEquals(LocalDate.of(2024, 11, 1), handaliResponse.getStart_date());
        assertEquals(2, handaliResponse.getActivity_level());
        assertEquals(3, handaliResponse.getIntelligent_level());
        assertEquals(1, handaliResponse.getArt_level());
    }

    @Test
    public void testGetWeekSalaryInfo_multipleHandalisWithJobs() {
        when(userService.tokenToUser(token)).thenReturn(user);

        Job job1 = new Job();
        job1.setName("개발자");
        Job job2 = new Job();
        job2.setName("디자이너");

        Handali handali1 = new Handali("5월이", LocalDate.of(2024, 5, 1), user);
        handali1.setHandaliId(1L);  // ✅ 추가!
        handali1.setJob(job1);
        handali1.setStartDate(LocalDate.of(2024, 11, 1));

        Handali handali2 = new Handali("6월이", LocalDate.of(2024, 6, 1), user);
        handali2.setHandaliId(2L);  // ✅ 추가!
        handali2.setJob(job2);
        handali2.setStartDate(LocalDate.of(2024, 11, 5));

        List<Handali> handalis = List.of(handali1, handali2);
        List<TypeName> typeNames = List.of(TypeName.ACTIVITY_SKILL, TypeName.INTELLIGENT_SKILL, TypeName.ART_SKILL);

        List<HandaliStat> allStats = new java.util.ArrayList<>();
        allStats.addAll(createHandaliStatsForSalary(handali1, 50.0f, 70.0f, 30.0f));
        allStats.addAll(createHandaliStatsForSalary(handali2, 80.0f, 60.0f, 90.0f));

        when(handaliRepository.findByUserAndJobIsNotNullWithJob(user)).thenReturn(handalis);
        when(handaliScheduler.calculateSalaryFor(handali1)).thenReturn(5000);
        when(handaliScheduler.calculateSalaryFor(handali2)).thenReturn(7000);
        when(handaliStatRepository.findByHandaliInAndStatType(handalis, typeNames))
                .thenReturn(allStats);
        when(statService.checkHandaliStatForLevel(50.0f)).thenReturn(2);
        when(statService.checkHandaliStatForLevel(70.0f)).thenReturn(3);
        when(statService.checkHandaliStatForLevel(30.0f)).thenReturn(1);
        when(statService.checkHandaliStatForLevel(80.0f)).thenReturn(4);
        when(statService.checkHandaliStatForLevel(60.0f)).thenReturn(3);
        when(statService.checkHandaliStatForLevel(90.0f)).thenReturn(4);

        HandaliDTO.GetWeekSalaryApiResponseDTO response = handaliService.getWeekSalaryInfo(token);

        assertEquals(2, response.getHandalis_salary().size());
        assertEquals(12000, response.getTotal_salary());
        assertEquals(2, response.getTotal_handali());

        verify(handaliScheduler, times(1)).calculateSalaryFor(handali1);
        verify(handaliScheduler, times(1)).calculateSalaryFor(handali2);
        verify(handaliStatRepository, times(1)).findByHandaliInAndStatType(handalis, typeNames);
    }

    @Test
    public void testGetWeekSalaryInfo_noHandalisWithJobs() {
        when(userService.tokenToUser(token)).thenReturn(user);
        when(handaliRepository.findByUserAndJobIsNotNullWithJob(user)).thenReturn(List.of());

        HandaliDTO.GetWeekSalaryApiResponseDTO response = handaliService.getWeekSalaryInfo(token);

        verify(handaliRepository, times(1)).findByUserAndJobIsNotNullWithJob(user);
        verify(handaliScheduler, never()).calculateSalaryFor(any());
        verify(handaliStatRepository, never()).findByHandaliInAndStatType(any(), any());

        assertEquals(0, response.getHandalis_salary().size());
        assertEquals(0, response.getTotal_salary());
        assertEquals(0, response.getTotal_handali());
    }

    @Test
    public void testGetWeekSalaryInfo_handaliWithZeroStats() {
        when(userService.tokenToUser(token)).thenReturn(user);

        Job job = new Job();
        job.setName("인턴");

        Handali handali = new Handali("신입이", LocalDate.of(2024, 11, 1), user);
        handali.setHandaliId(1L);  // ✅ 추가!
        handali.setJob(job);
        handali.setStartDate(LocalDate.of(2024, 11, 1));

        List<Handali> handalis = List.of(handali);
        List<HandaliStat> handaliStats = createHandaliStatsForSalary(handali, 0.0f, 0.0f, 0.0f);
        List<TypeName> typeNames = List.of(TypeName.ACTIVITY_SKILL, TypeName.INTELLIGENT_SKILL, TypeName.ART_SKILL);

        when(handaliRepository.findByUserAndJobIsNotNullWithJob(user)).thenReturn(handalis);
        when(handaliScheduler.calculateSalaryFor(handali)).thenReturn(3000);
        when(handaliStatRepository.findByHandaliInAndStatType(handalis, typeNames))
                .thenReturn(handaliStats);
        when(statService.checkHandaliStatForLevel(0.0f)).thenReturn(0);

        HandaliDTO.GetWeekSalaryApiResponseDTO response = handaliService.getWeekSalaryInfo(token);

        assertEquals(1, response.getHandalis_salary().size());
        assertEquals(3000, response.getTotal_salary());

        HandaliDTO.GetWeekSalaryResponseDTO handaliResponse = response.getHandalis_salary().get(0);
        assertEquals("신입이", handaliResponse.getNickname());
        assertEquals(0, handaliResponse.getActivity_level());
        assertEquals(0, handaliResponse.getIntelligent_level());
        assertEquals(0, handaliResponse.getArt_level());
    }

    @Test
    public void testGetWeekSalaryInfo_handaliWithHighStats() {
        when(userService.tokenToUser(token)).thenReturn(user);

        Job job = new Job();
        job.setName("시니어 개발자");

        Handali handali = new Handali("베테랑", LocalDate.of(2024, 1, 1), user);
        handali.setHandaliId(1L);  // ✅ 추가!
        handali.setJob(job);
        handali.setStartDate(LocalDate.of(2024, 1, 1));

        List<Handali> handalis = List.of(handali);
        List<HandaliStat> handaliStats = createHandaliStatsForSalary(handali, 100.0f, 100.0f, 100.0f);
        List<TypeName> typeNames = List.of(TypeName.ACTIVITY_SKILL, TypeName.INTELLIGENT_SKILL, TypeName.ART_SKILL);

        when(handaliRepository.findByUserAndJobIsNotNullWithJob(user)).thenReturn(handalis);
        when(handaliScheduler.calculateSalaryFor(handali)).thenReturn(15000);
        when(handaliStatRepository.findByHandaliInAndStatType(handalis, typeNames))
                .thenReturn(handaliStats);
        when(statService.checkHandaliStatForLevel(100.0f)).thenReturn(5);

        HandaliDTO.GetWeekSalaryApiResponseDTO response = handaliService.getWeekSalaryInfo(token);

        assertEquals(1, response.getHandalis_salary().size());
        assertEquals(15000, response.getTotal_salary());

        HandaliDTO.GetWeekSalaryResponseDTO handaliResponse = response.getHandalis_salary().get(0);
        assertEquals("베테랑", handaliResponse.getNickname());
        assertEquals(5, handaliResponse.getActivity_level());
        assertEquals(5, handaliResponse.getIntelligent_level());
        assertEquals(5, handaliResponse.getArt_level());
    }

    @Test
    public void testGetWeekSalaryInfo_handaliWithPartialStats() {
        when(userService.tokenToUser(token)).thenReturn(user);

        Job job = new Job();
        job.setName("아티스트");

        Handali handali = new Handali("예술가", LocalDate.of(2024, 7, 1), user);
        handali.setHandaliId(1L);  // ✅ 추가!
        handali.setJob(job);
        handali.setStartDate(LocalDate.of(2024, 7, 1));

        List<Handali> handalis = List.of(handali);

        Stat artStat = new Stat(TypeName.ART_SKILL);
        artStat.setValue(85.0f);
        HandaliStat handaliArtStat = new HandaliStat(handali, artStat);
        List<HandaliStat> handaliStats = List.of(handaliArtStat);
        List<TypeName> typeNames = List.of(TypeName.ACTIVITY_SKILL, TypeName.INTELLIGENT_SKILL, TypeName.ART_SKILL);

        when(handaliRepository.findByUserAndJobIsNotNullWithJob(user)).thenReturn(handalis);
        when(handaliScheduler.calculateSalaryFor(handali)).thenReturn(8000);
        when(handaliStatRepository.findByHandaliInAndStatType(handalis, typeNames))
                .thenReturn(handaliStats);
        when(statService.checkHandaliStatForLevel(0.0f)).thenReturn(0);
        when(statService.checkHandaliStatForLevel(85.0f)).thenReturn(4);

        HandaliDTO.GetWeekSalaryApiResponseDTO response = handaliService.getWeekSalaryInfo(token);

        HandaliDTO.GetWeekSalaryResponseDTO handaliResponse = response.getHandalis_salary().get(0);
        assertEquals("예술가", handaliResponse.getNickname());
        assertEquals(0, handaliResponse.getActivity_level());
        assertEquals(0, handaliResponse.getIntelligent_level());
        assertEquals(4, handaliResponse.getArt_level());
    }

    @Test
    public void testGetWeekSalaryInfo_threeHandalisWithDifferentJobsAndStats() {
        when(userService.tokenToUser(token)).thenReturn(user);

        Job job1 = new Job();
        job1.setName("개발자");
        Handali handali1 = new Handali("코드마스터", LocalDate.of(2024, 3, 1), user);
        handali1.setHandaliId(1L);  // ✅ 추가!
        handali1.setJob(job1);
        handali1.setStartDate(LocalDate.of(2024, 3, 1));

        Job job2 = new Job();
        job2.setName("운동선수");
        Handali handali2 = new Handali("스피드러너", LocalDate.of(2024, 6, 1), user);
        handali2.setHandaliId(2L);  // ✅ 추가!
        handali2.setJob(job2);
        handali2.setStartDate(LocalDate.of(2024, 6, 1));

        Job job3 = new Job();
        job3.setName("화가");
        Handali handali3 = new Handali("피카소", LocalDate.of(2024, 9, 1), user);
        handali3.setHandaliId(3L);  // ✅ 추가!
        handali3.setJob(job3);
        handali3.setStartDate(LocalDate.of(2024, 9, 1));

        List<Handali> handalis = List.of(handali1, handali2, handali3);
        List<TypeName> typeNames = List.of(TypeName.ACTIVITY_SKILL, TypeName.INTELLIGENT_SKILL, TypeName.ART_SKILL);

        List<HandaliStat> allStats = new java.util.ArrayList<>();
        allStats.addAll(createHandaliStatsForSalary(handali1, 30.0f, 90.0f, 20.0f));
        allStats.addAll(createHandaliStatsForSalary(handali2, 95.0f, 40.0f, 25.0f));
        allStats.addAll(createHandaliStatsForSalary(handali3, 35.0f, 50.0f, 88.0f));

        when(handaliRepository.findByUserAndJobIsNotNullWithJob(user)).thenReturn(handalis);
        when(handaliScheduler.calculateSalaryFor(handali1)).thenReturn(6000);
        when(handaliScheduler.calculateSalaryFor(handali2)).thenReturn(7500);
        when(handaliScheduler.calculateSalaryFor(handali3)).thenReturn(5500);
        when(handaliStatRepository.findByHandaliInAndStatType(handalis, typeNames))
                .thenReturn(allStats);
        when(statService.checkHandaliStatForLevel(30.0f)).thenReturn(1);
        when(statService.checkHandaliStatForLevel(90.0f)).thenReturn(4);
        when(statService.checkHandaliStatForLevel(20.0f)).thenReturn(1);
        when(statService.checkHandaliStatForLevel(95.0f)).thenReturn(5);
        when(statService.checkHandaliStatForLevel(40.0f)).thenReturn(2);
        when(statService.checkHandaliStatForLevel(25.0f)).thenReturn(1);
        when(statService.checkHandaliStatForLevel(35.0f)).thenReturn(2);
        when(statService.checkHandaliStatForLevel(50.0f)).thenReturn(2);
        when(statService.checkHandaliStatForLevel(88.0f)).thenReturn(4);

        HandaliDTO.GetWeekSalaryApiResponseDTO response = handaliService.getWeekSalaryInfo(token);

        assertEquals(3, response.getHandalis_salary().size());
        assertEquals(19000, response.getTotal_salary());
        assertEquals(3, response.getTotal_handali());

        HandaliDTO.GetWeekSalaryResponseDTO response1 = response.getHandalis_salary().get(0);
        assertEquals("코드마스터", response1.getNickname());
        assertEquals("개발자", response1.getJob());
        assertEquals(6000, response1.getSalary());
        assertEquals(1, response1.getActivity_level());
        assertEquals(4, response1.getIntelligent_level());
        assertEquals(1, response1.getArt_level());

        HandaliDTO.GetWeekSalaryResponseDTO response2 = response.getHandalis_salary().get(1);
        assertEquals("스피드러너", response2.getNickname());
        assertEquals("운동선수", response2.getJob());
        assertEquals(7500, response2.getSalary());
        assertEquals(5, response2.getActivity_level());
        assertEquals(2, response2.getIntelligent_level());
        assertEquals(1, response2.getArt_level());

        HandaliDTO.GetWeekSalaryResponseDTO response3 = response.getHandalis_salary().get(2);
        assertEquals("피카소", response3.getNickname());
        assertEquals("화가", response3.getJob());
        assertEquals(5500, response3.getSalary());
        assertEquals(2, response3.getActivity_level());
        assertEquals(2, response3.getIntelligent_level());
        assertEquals(4, response3.getArt_level());

        verify(handaliScheduler, times(3)).calculateSalaryFor(any(Handali.class));
        verify(handaliStatRepository, times(1))
                .findByHandaliInAndStatType(handalis, typeNames);
    }

    private List<HandaliStat> createHandaliStatsForSalary(
            Handali handali, float activityValue, float intelligentValue, float artValue) {

        Stat activityStat = new Stat(TypeName.ACTIVITY_SKILL);
        activityStat.setValue(activityValue);
        Stat intelligentStat = new Stat(TypeName.INTELLIGENT_SKILL);
        intelligentStat.setValue(intelligentValue);
        Stat artStat = new Stat(TypeName.ART_SKILL);
        artStat.setValue(artValue);

        return List.of(
                new HandaliStat(handali, activityStat),
                new HandaliStat(handali, intelligentStat),
                new HandaliStat(handali, artStat)
        );
    }
}