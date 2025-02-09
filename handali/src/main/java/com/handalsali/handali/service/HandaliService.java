package com.handalsali.handali.service;

import com.handalsali.handali.DTO.HandaliDTO;
import com.handalsali.handali.DTO.StatDetailDTO;
import com.handalsali.handali.domain.Apart;
import com.handalsali.handali.enums_multyKey.ApartId;
import com.handalsali.handali.repository.ApartRepository;
import com.handalsali.handali.domain.Handali;
import com.handalsali.handali.domain.Stat;
import com.handalsali.handali.domain.User;
import com.handalsali.handali.enums_multyKey.Categoryname;
import com.handalsali.handali.exception.HanCreationLimitException;
import com.handalsali.handali.exception.HandaliNotFoundException;
import com.handalsali.handali.repository.HandaliRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Service
@Transactional
public class HandaliService {
    private UserService userService;
    private HandaliRepository handaliRepository;
    private StatService statService;
    private final ApartRepository apartRepository;

    public HandaliService(UserService userService, HandaliRepository handaliRepository, ApartRepository apartRepository, StatService statService) {
        this.userService = userService;
        this.apartRepository = apartRepository;
        this.handaliRepository = handaliRepository;
        this.statService = statService;
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

        //4. 한달이의 스탯 초기화
        statService.statInit(handali);

        return handali;
    }

    //유저의 이번달 한달이 조회 - 다음 달로 넘어가는 순간 호출되면 한달이를 찾을 수 없는 예외 발생
    public Handali findHandaliByCurrentDateAndUser(User user){
        return handaliRepository.findHandaliByCurrentDateAndUser(user);
    }

    //한달이 찾고, [스탯 업데이트]
    public void statUpdate(User user, Categoryname categoryname, float time, int satisfaction) {
        // 1. 한달이 찾기
        Handali handali = findHandaliByCurrentDateAndUser(user);
        if (handali == null) throw new HandaliNotFoundException("한달이를 찾을 수 없습니다.");

        // 2. StatService로 한달이 객체 전달
        statService.statUpdate(handali, categoryname, time, satisfaction);
    }

    //한달이 저장
    public void save(Handali handali){
        handaliRepository.save(handali);
    }

    // [한달이 상태 조회]
    // 02-07 - 한달이 없을때 예외 처리 추가
    public HandaliDTO.HandaliStatusResponse getHandaliStatusByIdAndMonth(Long handaliId, String token) {
        userService.tokenToUser(token);

        //한달이 조회
        Handali handali = handaliRepository.findById(handaliId)
                .orElseThrow(() -> new HandaliNotFoundException("해당 한달이가 존재하지 않습니다."));

        // 생성일로부터 경과 일수를 계산하는 로직
        int days_Since_Created = Period.between(handali.getStartDate(), LocalDate.now()).getDays()+1;

        String message = "아직 30일이 되지 않았습니다.";
        if (days_Since_Created == 30) {
            message = "생성된지 30일이 되었습니다.";
        }

        return new HandaliDTO.HandaliStatusResponse(
                handali.getNickname(),
                days_Since_Created,
                message
        );

    }

    // [스탯 조회]
    public HandaliDTO.StatResponse getStatsByHandaliId(Long handaliId, String token) {
        // Handali 엔티티 존재 여부 확인 (예외 처리 포함)
        handaliRepository.findById(handaliId)
                .orElseThrow(() -> new EntityNotFoundException("해당 handali_id에 대한 데이터가 없습니다."));

        // 스탯 조회
        List<StatDetailDTO> stats = handaliRepository.findStatsByHandaliId(handaliId);

        return new HandaliDTO.StatResponse(stats);
    }


    // [아파트 입주]
    public HandaliDTO.ApartEnterResponse moveHandaliToApartment(Long handaliId, String token) {
        userService.tokenToUser(token);

        // 1. 한달이 조회
        Handali handali = handaliRepository.findById(handaliId)
                .orElseThrow(() -> new HandaliNotFoundException("해당 한달이가 존재하지 않습니다."));

        // 2. 한달이가 직업을 가지고 있는지 확인
        if (handali.getJob() == null) {
            throw new IllegalStateException("한달이가 직업을 가져야 아파트에 입주할 수 있습니다.");
        }

        // 3. 생성 달과 현재 달 비교 (입주 조건)
        if (handali.getStartDate().getMonthValue() == LocalDate.now().getMonthValue()) {
            throw new IllegalStateException("한달이가 생성된 달과 현재 달이 같아 입주할 수 없습니다.");
        }

        // 4. 최신 아파트 조회
        Apart latestApartment = apartRepository.findLatestApartment();

        if (latestApartment == null) {
            // 아파트가 없으면 새로 생성 (apart_id = 1, floor = 1)
            latestApartment = apartRepository.save(new Apart(new ApartId(1, 1), handali.getUser()));
        }

        // 5. 해당 아파트의 현재 층 개수 확인
        int currentFloor = handaliRepository.countHandalisInApartment(latestApartment.getApartId().getApartId());

        if (currentFloor >= 12) {
            latestApartment = apartRepository.save(new Apart(new ApartId(latestApartment.getApartId().getApartId() + 1, 1), handali.getUser()));
            currentFloor = 1;
        } else {
            currentFloor += 1;
        }

        // 6. 한달이의 아파트 정보 업데이트
        handali.setApart(latestApartment);
        handaliRepository.save(handali);

        // 7. 응답 DTO 반환
        return new HandaliDTO.ApartEnterResponse(
                latestApartment.getApartId().getApartId(),
                currentFloor
        );
    }
}
