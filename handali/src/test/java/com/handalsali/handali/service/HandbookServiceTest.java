package com.handalsali.handali.service;

import com.handalsali.handali.DTO.HandbookDTO;
import com.handalsali.handali.domain.Handbook;
import com.handalsali.handali.domain.User;
import com.handalsali.handali.domain.UserHandbook;
import com.handalsali.handali.repository.HandbookRepository;
import com.handalsali.handali.repository.UserHandbookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class HandbookServiceTest {

    @Mock
    private HandbookRepository handbookRepository;

    @Mock
    private UserHandbookRepository userHandbookRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private HandbookService handbookService;

    private User user;
    private Handbook handbook;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
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
        verify(userHandbookRepository, times(1)).save(any(UserHandbook.class));
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
     * 토큰을 통해 유저를 식별하고,
     * 해당 유저의 UserHandbook 리스트를 조회한 뒤
     * DTO(HandbookApiResponse) 로 올바르게 변환 및 반환되는지 검증
     * → code 와 created_at 필드 매핑 확인
     */
    @Test
    void testGetUserHandbook() {
        // given
        String token = "test-token";
        when(userService.tokenToUser(token)).thenReturn(user);

        UserHandbook userHandbook = mock(UserHandbook.class);
        when(userHandbook.getHandbook()).thenReturn(handbook);
        when(userHandbook.getCreatedAt()).thenReturn(LocalDateTime.of(2025, 1, 1, 12, 0));

        when(userHandbookRepository.findAllByUser(user)).thenReturn(List.of(userHandbook));

        // when
        HandbookDTO.HandbookApiResponse response = handbookService.getUserHandbook(user);

        // then
        assertEquals(1, response.getHandbooks().size());
        assertEquals("image_1_5_0.png", response.getHandbooks().get(0).getCode());
        assertEquals(LocalDateTime.of(2025, 1, 1, 12, 0), response.getHandbooks().get(0).getCreated_at());
    }
}
