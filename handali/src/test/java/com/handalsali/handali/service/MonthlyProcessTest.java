package com.handalsali.handali.service;

import com.handalsali.handali.domain.Apart;
import com.handalsali.handali.domain.Handali;
import com.handalsali.handali.domain.Job;
import com.handalsali.handali.domain.User;
import com.handalsali.handali.repository.HandaliRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MonthlyProcessTest {
    @Mock
    private HandaliRepository handaliRepository;

    @Mock
    private JobService jobService;

    @Mock
    private ApartmentService apartmentService;

    @InjectMocks
    private HandaliService handaliService;

    //한달이 자동 취업 및 아파트 입주
    @Test
    public void testProcessMonthlyJobAndApartmentEntry_AssignsJobAndApartment() {
        // Given
        Handali handali = new Handali();
        handali.setNickname("테스트한달이");

        // 이전 달에 생성된 한달이 리스트 설정
        when(handaliRepository.findUnemployedHandalisForMonth(any(), any()))
                .thenReturn(List.of(handali));

        Job job = new Job("개발자", 7000);
        Apart apart = new Apart();

        when(jobService.assignBestJobToHandali(handali)).thenReturn(job);
        when(apartmentService.assignApartmentToHandali(handali)).thenReturn(apart);

        // When
        handaliService.processMonthlyJobAndApartmentEntry();

        // Then
        verify(jobService).assignBestJobToHandali(handali);
        verify(apartmentService).assignApartmentToHandali(handali);

        ArgumentCaptor<Handali> captor = ArgumentCaptor.forClass(Handali.class);
        verify(handaliRepository).save(captor.capture());

        Handali saved = captor.getValue();
        assertEquals("개발자", saved.getJob().getName());
        assertEquals(apart, saved.getApart());
    }

    //한달이가 없을 경우 테스트
    @Test
    public void testProcessMonthlyJobAndApartmentEntry_NoHandalisToProcess() {
        // Given
        when(handaliRepository.findUnemployedHandalisForMonth(any(), any()))
                .thenReturn(List.of());

        // When
        handaliService.processMonthlyJobAndApartmentEntry();

        // Then
        verify(jobService, never()).assignBestJobToHandali(any());
        verify(apartmentService, never()).assignApartmentToHandali(any());
        verify(handaliRepository, never()).save(any());
    }

    // 한달이가 이미 취업했거나 입주했을 때 테스트
    @Test
    public void testProcessMonthlyJobAndApartmentEntry_HandaliAlreadyHasJobOrApartment() {
        // Given
        User user = new User();

        // 이미 직업이 있음 (입주 X)
        Job existingJob = new Job("경비", 6000);
        Handali employedOnly = new Handali();
        employedOnly.setNickname("직업만 있음");
        employedOnly.setJob(existingJob);
        employedOnly.setApart(null);

        // 이미 아파트 입주함 (직업 X)
        Apart existingApart = new Apart(user, null, "닉", 2, 202);
        Handali movedInOnly = new Handali();
        movedInOnly.setNickname("입주만 함");
        movedInOnly.setJob(null);
        movedInOnly.setApart(existingApart);

        // 둘 다 있음
        Handali fullyAssigned = new Handali();
        fullyAssigned.setNickname("둘 다 있음");
        fullyAssigned.setJob(existingJob);
        fullyAssigned.setApart(existingApart);

        List<Handali> alreadyProcessedHandalis = List.of(employedOnly, movedInOnly, fullyAssigned);

        when(handaliRepository.findUnemployedHandalisForMonth(any(), any()))
                .thenReturn(alreadyProcessedHandalis);

        // When
        handaliService.processMonthlyJobAndApartmentEntry();

        // Then
        // 실제로 새로 배정되거나 저장된 것이 없어야 함
        verify(jobService, never()).assignBestJobToHandali(any());
        verify(apartmentService, never()).assignApartmentToHandali(any());
        verify(handaliRepository, never()).save(any());
    }
}
