package com.handalsali.handali.service;

import com.handalsali.handali.DTO.HandbookDTO;
import com.handalsali.handali.domain.Handbook;
import com.handalsali.handali.domain.User;
import com.handalsali.handali.domain.UserHandbook;
import com.handalsali.handali.repository.HandbookRepository;
import com.handalsali.handali.repository.UserHandbookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HandbookServiceTest {

    @Mock
    private HandbookRepository handbookRepository;

    @Mock
    private UserHandbookRepository userHandbookRepository;

    @InjectMocks
    private HandbookService handbookService;

    private User user;
    private Handbook handbook;

    @BeforeEach
    void setUp() {
        user = new User("aaa@gmail.com", "name", "1234", "010-1234-5678", java.time.LocalDate.now());
        handbook = new Handbook("image_1_5_0.png");
    }

    /**
     * ✅ [테스트 목적]
     * 도감 코드가 Handbook 테이블에 존재하고,
     * 해당 유저가 아직 도감을 보유하지 않은 경우
     * → UserHandbookRepository.save() 가 정상적으로 호출되어 도감이 추가되는지 검증
     */
    @Test
    void testAddHandbook_NewEntry() {
        // given
        when(handbookRepository.findByCode("image_1_5_0.png")).thenReturn(handbook);
        when(userHandbookRepository.existsByUserAndHandbook(user, handbook)).thenReturn(false);

        // when
        handbookService.addHandbook(user, "image_1_5_0.png");

        // then
        ArgumentCaptor<UserHandbook> captor = ArgumentCaptor.forClass(UserHandbook.class);
        verify(userHandbookRepository, times(1)).save(captor.capture());

        UserHandbook savedUserHandbook = captor.getValue();
        assertEquals(user, savedUserHandbook.getUser());
        assertEquals(handbook, savedUserHandbook.getHandbook());
    }

    /**
     * ✅ [테스트 목적]
     * 도감 코드가 Handbook 테이블에 존재하지만,
     * 해당 유저가 이미 해당 도감을 보유하고 있는 경우
     * → UserHandbookRepository.save() 가 호출되지 않고 중복 저장이 방지되는지 검증
     */
    @Test
    void testAddHandbook_AlreadyExists() {
        // given
        when(handbookRepository.findByCode("image_1_5_0.png")).thenReturn(handbook);
        when(userHandbookRepository.existsByUserAndHandbook(user, handbook)).thenReturn(true);

        // when
        handbookService.addHandbook(user, "image_1_5_0.png");

        // then
        verify(userHandbookRepository, never()).save(any(UserHandbook.class));
    }

    /**
     * ✅ [테스트 목적]
     * 도감 코드가 Handbook 테이블에 존재하지 않는 경우
     * → 아무 작업도 수행되지 않고 조용히 종료되는지 검증
     */
    @Test
    void testAddHandbook_HandbookNotFound() {
        // given
        when(handbookRepository.findByCode("invalid_code.png")).thenReturn(null);

        // when
        handbookService.addHandbook(user, "invalid_code.png");

        // then
        verify(userHandbookRepository, never()).existsByUserAndHandbook(any(), any());
        verify(userHandbookRepository, never()).save(any());
    }

    /**
     * ✅ [테스트 목적]
     * 유저의 도감 목록을 조회하고 DTO로 변환
     */
    @Test
    void testGetUserHandbook_Success() {
        // given

        UserHandbook userHandbook = new UserHandbook(user, handbook);

        when(userHandbookRepository.findAllByUser(user)).thenReturn(List.of(userHandbook));

        // when
        HandbookDTO.HandbookApiResponse response = handbookService.getUserHandbook(user);

        // then
        assertEquals(1, response.getHandbooks().size());
        assertEquals("image_1_5_0.png", response.getHandbooks().get(0).getCode());

        verify(userHandbookRepository, times(1)).findAllByUser(user);
    }

    /**
     * ✅ [테스트 목적]
     * 유저가 보유한 도감이 없는 경우
     * → 빈 리스트가 반환되는지 검증
     */
    @Test
    void testGetUserHandbook_EmptyList() {
        // given
        when(userHandbookRepository.findAllByUser(user)).thenReturn(List.of());

        // when
        HandbookDTO.HandbookApiResponse response = handbookService.getUserHandbook(user);

        // then
        assertEquals(0, response.getHandbooks().size());
        verify(userHandbookRepository, times(1)).findAllByUser(user);
    }

    /**
     * ✅ [테스트 목적]
     * 유저가 여러 개의 도감을 보유한 경우
     * → 모든 도감이 올바르게 반환되는지 검증
     */
    @Test
    void testGetUserHandbook_MultipleHandbooks() {
        // given
        Handbook handbook1 = new Handbook("image_0_0_0.png");
        Handbook handbook2 = new Handbook("image_1_2_3.png");
        Handbook handbook3 = new Handbook("image_5_5_5.png");

        UserHandbook uh1 = new UserHandbook(user, handbook1);
        UserHandbook uh2 = new UserHandbook(user, handbook2);
        UserHandbook uh3 = new UserHandbook(user, handbook3);

        when(userHandbookRepository.findAllByUser(user)).thenReturn(List.of(uh1, uh2, uh3));

        // when
        HandbookDTO.HandbookApiResponse response = handbookService.getUserHandbook(user);

        // then
        assertEquals(3, response.getHandbooks().size());
        assertEquals("image_0_0_0.png", response.getHandbooks().get(0).getCode());
        assertEquals("image_1_2_3.png", response.getHandbooks().get(1).getCode());
        assertEquals("image_5_5_5.png", response.getHandbooks().get(2).getCode());
    }
}