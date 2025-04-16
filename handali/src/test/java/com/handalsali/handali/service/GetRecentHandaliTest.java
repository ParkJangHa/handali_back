package com.handalsali.handali.service;

import java.util.Optional;
import com.handalsali.handali.DTO.HandaliDTO;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GetRecentHandaliTest {

    @Mock
    private UserService userService;

    @Mock
    private HandaliRepository handaliRepository;

    @InjectMocks
    private HandaliService handaliService;

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
}
