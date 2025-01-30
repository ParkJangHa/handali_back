package com.handalsali.handali.service;

import com.handalsali.handali.DTO.HandaliDTO;
import com.handalsali.handali.DTO.StatDetail;
import com.handalsali.handali.domain.Handali;
import com.handalsali.handali.domain.User;
import com.handalsali.handali.exception.HanCreationLimitException;
import com.handalsali.handali.repository.HandaliRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Service
@Transactional
public class HandaliService {
    private final UserService userService;
    private final HandaliRepository handaliRepository;

    @Autowired
    public HandaliService(UserService userService, HandaliRepository handaliRepository) {
        this.userService = userService;
        this.handaliRepository = handaliRepository;
    }

    //[한달이 생성]
    public Handali handaliCreate(String token,String nickname){
        //1. 사용자 인증
        User user=userService.tokenToUser(token);
        //2. 한달이는 한달에 한마리만 가능
        if(handaliRepository.countPetsByUserIdAndCurrentMonth(user)>0){
            throw new HanCreationLimitException();
        }
        //3. 한달이 생성
        Handali handali=new Handali(nickname, LocalDate.now(),user);
        handaliRepository.save(handali);
        return handali;
    }

    // 한달이 상태 조회
    public HandaliDTO.HandaliStatusResponse getHandaliStatusByIdAndMonth(Long handaliId, String token) {
        userService.tokenToUser(token);

        Handali handali = handaliRepository.findById(handaliId)
                .orElseThrow(() -> new RuntimeException("Handali not found"));

        // 예: 생성일로부터 경과 일수를 계산하는 로직
        int days_Since_Created = Period.between(handali.getStartDate(), LocalDate.now()).getDays()+1;

        String message = "아직 30일이 되지 않았습니다.";
        if (days_Since_Created == 30) {
            message = "생성된지 30일이 되었습니다.";
        }

        return new HandaliDTO.HandaliStatusResponse(
                handali.getHandaliId(),
                handali.getNickname(),
                days_Since_Created,
                message
        );

    }


    // [스탯 조회]
    public HandaliDTO.StatResponse getStatsByHandaliId(Long handaliId, String token) {
        // Handali 엔티티 존재 여부 확인 (예외 처리 포함)
        Handali handali = handaliRepository.findById(handaliId)
                .orElseThrow(() -> new EntityNotFoundException("해당 handali_id에 대한 데이터가 없습니다."));

        // 스탯 조회
        List<StatDetail> stats = handaliRepository.findStatsByHandaliId(handaliId);

        return new HandaliDTO.StatResponse(stats);
    }
}
