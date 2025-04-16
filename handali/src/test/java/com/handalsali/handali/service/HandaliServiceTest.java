package com.handalsali.handali.service;

import com.handalsali.handali.DTO.HabitDTO;
import com.handalsali.handali.DTO.HandaliDTO;
import com.handalsali.handali.domain.*;
import com.handalsali.handali.enums_multyKey.Categoryname;
import com.handalsali.handali.enums_multyKey.CreatedType;
import com.handalsali.handali.enums_multyKey.TypeName;
import com.handalsali.handali.exception.HanCreationLimitException;
import com.handalsali.handali.exception.HandaliNotFoundException;
import com.handalsali.handali.repository.HandaliRepository;
import com.handalsali.handali.repository.HandaliStatRepository;
import com.handalsali.handali.repository.RecordRepository;
import com.handalsali.handali.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HandaliServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private HandaliService handaliService;

    @Mock
    private HandaliRepository handaliRepository;

    @Mock
    private RecordRepository recordRepository;

    @Mock
    private UserService userService;

    @Mock
    private HandaliStatRepository handaliStatRepository;
    @Mock
    private StatService statService;

    private String token;
    private User user;
    private Handali handali;

    @BeforeEach
    void setUp() {
        token = "test-token";
        user = new User("aaa@gmail.com", "name", "1234", "010-1234-5678", LocalDate.now());
        handali=new Handali("aaa",LocalDate.now(),user);
    }

    /**[한달이 상태 변화]*/
    @Test
    public void testChangeHandali(){
        //given
        when(userService.tokenToUser(token)).thenReturn(user);
        when(handaliRepository.findLatestHandaliByCurrentMonth(user.getUserId())).thenReturn(handali);

        int activityValue=100; //정상레벨 1
        int intelligentValue=1500; //최대치 초과 레벨
        int artValue=0; //정상레벨 0
        List<HandaliStat> stats = getHandaliStats(activityValue,intelligentValue,artValue);

        when(handaliStatRepository.findByHandali(handali)).thenReturn(stats);
        when(statService.checkHandaliStat(activityValue)).thenReturn(1);
        when(statService.checkHandaliStat(intelligentValue)).thenReturn(5);
        when(statService.checkHandaliStat(artValue)).thenReturn(0);

        //when
        String image = handaliService.changeHandali(token);

        //then
        assertEquals("image_1_5_0.png",image);
        assertEquals("image_1_5_0.png",handali.getImage());
        verify(handaliRepository).save(handali);
    }

    private List<HandaliStat> getHandaliStats(int activityValue,int intelligentValue,int artValue) {
        Stat activityStat=new Stat(TypeName.ACTIVITY_SKILL);
        activityStat.setValue(activityValue);
        Stat intelligentStat=new Stat(TypeName.INTELLIGENT_SKILL);
        intelligentStat.setValue(intelligentValue);
        Stat artStat=new Stat(TypeName.ART_SKILL);
        artStat.setValue(artValue);

        return List.of(
                new HandaliStat(handali,activityStat),
                new HandaliStat(handali,intelligentStat),
                new HandaliStat(handali,artStat)
        );
    }

    /**
     * [한달이 생성]
     */
    @Test
    public void testHandaliCreate(){
        //given
        when(userService.tokenToUser(token)).thenReturn(user);
        when(handaliRepository.countPetsByUserIdAndCurrentMonth(user)).thenReturn(0L);


        //when
        handaliService.handaliCreate(token, "aaa");

        //then
        ArgumentCaptor<Handali> handaliCaptor = ArgumentCaptor.forClass(Handali.class);
        verify(handaliRepository).save(handaliCaptor.capture());
        Handali handali = handaliCaptor.getValue();

        assertEquals("aaa",handali.getNickname());
        assertEquals(user,handali.getUser());
        assertEquals(LocalDate.now(),handali.getStartDate());
    }

    @Test
    public void testHandaliCreate_error(){
        //given
        when(userService.tokenToUser(token)).thenReturn(user);

        //when
        when(handaliRepository.countPetsByUserIdAndCurrentMonth(user)).thenReturn(1L);

        //then
        assertThrows(HanCreationLimitException.class,()->{
            handaliService.handaliCreate(token, "aaa");
        });
    }

    /**
     * [마지막 생성 한달이 조회]
     */
    @Test
    public void testGetRecentHandali_Success() {
        // Given
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

        // When
        HandaliDTO.RecentHandaliResponse response = handaliService.getRecentHandali(token);

        // Then
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
        // Given
        String token = "invalid-token";
        when(userService.tokenToUser(token)).thenReturn(null);

        // When & Then
        assertThrows(HandaliNotFoundException.class, () -> handaliService.getRecentHandali(token));
        verify(userService).tokenToUser(token);
        verifyNoInteractions(handaliRepository);
    }

    @Test
    public void testGetRecentHandali_NoHandali() {
        // Given
        String token = "valid-token";
        User user = new User();
        user.setUserId(1L);

        when(userService.tokenToUser(token)).thenReturn(user);
        when(handaliRepository.findLatestHandaliByUser(user.getUserId())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(HandaliNotFoundException.class, () -> handaliService.getRecentHandali(token));
        verify(userService).tokenToUser(token);
        verify(handaliRepository).findLatestHandaliByUser(user.getUserId());
    }

    /**
     * [한달이 상태 조회]
     */
    @Test
    public void testGetHandaliStatusByMonth_Success() {
        // Given
        String token = "valid-token";
        User user = new User();
        user.setTotal_coin(300);

        Handali handali = new Handali();
        handali.setNickname("테스트한달이");
        handali.setStartDate(LocalDate.now().minusDays(4)); // 생성된지 4일 됨
        handali.setUser(user);
        handali.setImage("test-image.png");

        when(userService.tokenToUser(token)).thenReturn(user);
        when(handaliRepository.findLatestHandaliByCurrentMonth(user.getUserId())).thenReturn(handali);

        // When
        HandaliDTO.HandaliStatusResponse response = handaliService.getHandaliStatusByMonth(token);

        // Then
        assertEquals("테스트한달이", response.getNickname());
        assertEquals(5, response.getDays_since_created()); // 오늘 포함이므로 4 + 1
        assertEquals(300, response.getTotal_coin());
        assertEquals("test-image.png", response.getImage());

        verify(userService).tokenToUser(token);
        verify(handaliRepository).findLatestHandaliByCurrentMonth(user.getUserId());
    }
}
