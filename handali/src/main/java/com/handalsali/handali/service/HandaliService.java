package com.handalsali.handali.service;

import com.handalsali.handali.DTO.HandaliDTO;
import com.handalsali.handali.domain.Apartment;
import com.handalsali.handali.domain.Handali;
import com.handalsali.handali.domain.User;
import com.handalsali.handali.enums_multyKey.ApartId;
import com.handalsali.handali.enums_multyKey.Categoryname;
import com.handalsali.handali.exception.HanCreationLimitException;
import com.handalsali.handali.exception.HandaliNotEligibleForApartmentException;
import com.handalsali.handali.exception.HandaliNotFoundException;
import com.handalsali.handali.repository.ApartmentRepository;
import com.handalsali.handali.repository.HandaliRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

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


    //[한달이 아파트 입주]
    public HandaliDTO.ApartmentResponse enterApartment(Long handaliId) {
        // 1. 한달이 찾기
        Handali handali = handaliRepository.findById(handaliId)
                .orElseThrow(() -> new HandaliNotFoundException("해당 한달이를 찾을 수 없습니다. ID: " + handaliId));

        // 2. 30일이 지나야 입주 가능
        if (handali.getStartDate().plusDays(30).isAfter(LocalDate.now())) {
            throw new HandaliNotEligibleForApartmentException("한달이가 30일을 채우지 않아 아파트에 입주할 수 없습니다.");
        }

        // 3. 한달이는 직업이 있어야 입주 가능
        if (handali.getJob() == null) {
            throw new HandaliNotEligibleForApartmentException("직업이 없는 한달이는 아파트에 입주할 수 없습니다.");
        }

        // 4. 현재 마지막 아파트 찾기
        Optional<Apartment> lastApartment = apartmentRepository.findTopByOrderByApartIdDesc();

        int apartId;
        int floor;

        if (lastApartment.isPresent()) {
            Apartment latest = lastApartment.get();
            ApartId latestApartId = latest.getApartId();

            if (latestApartId.getFloor() < 12) {
                // 현재 아파트에 빈 층이 있음 → 다음 층으로 배정
                apartId = latestApartId.getApartId();
                floor = latestApartId.getFloor() + 1;
            } else {
                // 아파트가 가득 찼으면 새 아파트 생성
                apartId = latestApartId.getApartId() + 1;
                floor = 1;
            }
        } else {
            // 첫 번째 아파트 생성
            apartId = 1;
            floor = 1;
        }

        // 5. 새로운 아파트 객체 저장
        ApartId apartmentKey = new ApartId(apartId, floor);
        Apartment apartment = new Apartment(apartmentKey, handali.getUser());
        apartmentRepository.save(apartment);

        // 6. 응답 객체 반환
        return new HandaliDTO.ApartmentResponse(apartId, floor);
    }
}
