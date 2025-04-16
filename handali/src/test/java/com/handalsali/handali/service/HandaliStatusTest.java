package com.handalsali.handali.service;

import com.handalsali.handali.DTO.HandaliDTO;
import com.handalsali.handali.domain.Handali;
import com.handalsali.handali.domain.User;
import com.handalsali.handali.repository.HandaliRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

//한달이 상태 조회 테스트
@ExtendWith(MockitoExtension.class)
public class HandaliStatusTest {
    @Mock
    private UserService userService;

    @Mock
    private HandaliRepository handaliRepository;

    @InjectMocks
    private HandaliService handaliService;

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
