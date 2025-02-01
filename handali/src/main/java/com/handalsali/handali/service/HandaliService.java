package com.handalsali.handali.service;

import com.handalsali.handali.domain.Apartment;
import com.handalsali.handali.domain.Handali;
import com.handalsali.handali.domain.User;
import com.handalsali.handali.enums_multyKey.ApartId;
import com.handalsali.handali.enums_multyKey.Categoryname;
import com.handalsali.handali.exception.HanCreationLimitException;
import com.handalsali.handali.exception.HandaliNotFoundException;
import com.handalsali.handali.repository.ApartmentRepository;
import com.handalsali.handali.repository.HandaliRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@Transactional
public class HandaliService {
    private UserService userService;
    private HandaliRepository handaliRepository;
    private final ApartmentRepository apartmentRepository;
    private StatService statService;

    public HandaliService(UserService userService, HandaliRepository handaliRepository, StatService statService, ApartmentRepository apartmentRepository) {
        this.userService = userService;
        this.handaliRepository = handaliRepository;
        this.statService = statService;
        this.apartmentRepository = apartmentRepository;
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


    // 한달이 ID로 입주 -02.01
    public void enterApartment(Long handaliId) {
        Handali handali = handaliRepository.findById(handaliId)
                .orElseThrow(() -> new IllegalArgumentException("해당 한달이를 찾을 수 없습니다. ID: " + handaliId));

        enterApartment(handali);
    }

    // 30일이 지난 한달이 아파트 입주
    public void enterApartment(Handali handali) {
        // 1. 한달이가 30일을 채웠는지 확인
        if (!isEligibleForApartment(handali)) {
            throw new IllegalStateException("한달이가 30일을 채우지 않아 아파트에 입주할 수 없습니다.");
        }

        // 2. 현재 가장 마지막에 생성된 아파트를 찾기
        Apartment lastApartment = apartmentRepository.findTopByOrderByApartId_ApartIdDesc();
        int nextFloor = 1;
        int nextApartmentId = 1;

        if (lastApartment != null) {
            int currentOccupants = apartmentRepository.countByApartId_ApartId(lastApartment.getApartId().getApartId());
            if (currentOccupants < 12) {
                nextApartmentId = lastApartment.getApartId().getApartId();
                nextFloor = currentOccupants + 1;
            } else {
                nextApartmentId = lastApartment.getApartId().getApartId() + 1;
                nextFloor = 1;
            }
        }

        // 3. 새로운 아파트 객체 생성 및 저장
        ApartId apartmentKey = new ApartId(nextApartmentId, nextFloor);
        Apartment newApartment = new Apartment(apartmentKey, handali.getUser());
        apartmentRepository.save(newApartment);

        // 4. 한달이에게 아파트 배정
        handali.setApartment(newApartment);
        handaliRepository.save(handali);
    }

    //한달이가 30일을 채웠는지 확인하는 메서드
    private boolean isEligibleForApartment(Handali handali) {
        LocalDate today = LocalDate.now();
        return handali.getStartDate().plusDays(30).isBefore(today) || handali.getStartDate().plusDays(30).isEqual(today);
    }
}
