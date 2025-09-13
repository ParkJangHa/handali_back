package com.handalsali.handali.service;

import com.handalsali.handali.DTO.HandaliDTO;
import com.handalsali.handali.domain.Handali;
import com.handalsali.handali.domain.HandaliStat;
import com.handalsali.handali.domain.User;
import com.handalsali.handali.enums.ItemType;
import com.handalsali.handali.enums.TypeName;
import com.handalsali.handali.repository.*;
import com.handalsali.handali.exception.HanCreationLimitException;
import com.handalsali.handali.exception.HandaliNotFoundException;
import com.handalsali.handali.scheduler.HandaliScheduler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class HandaliService {
    private final UserService userService;
    private final HandaliRepository handaliRepository;
    private final StatService statService;
    private final HandaliStatRepository handaliStatRepository;
    private final UserItemRepository userItemRepository;
    private final HandbookService handbookService;
    private final HandaliScheduler handaliScheduler;

    /**[한달이 생성]*/
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
        statService.statInit(user,handali);

        return handali;
    }

    /**유저의 이번달 한달이 조회 - 다음 달로 넘어가는 순간 호출되면 한달이를 찾을 수 없는 예외 발생*/
    public Handali findHandaliByCurrentDateAndUser(User user){
        Handali handali = handaliRepository.findLatestHandaliByCurrentMonth(user.getUserId());
        return handali;
    }


    /**[한달이 상태 변화]-이미지 반환*/
    public String changeHandali(String token){
        //1. 사용자 확인
        User user=userService.tokenToUser(token);

        //2. 한달이 찾기
//        Handali handali = findHandaliByCurrentDateAndUser(user);
        Handali handali=handaliRepository.findLatestHandaliByCurrentMonth(user.getUserId());

        //3. 이미지 생성 - image_활동_지능_예술.png
        StringBuilder imageName= new StringBuilder("image");
        List<HandaliStat> handaliStats=handaliStatRepository.findByHandali(handali);
        for(HandaliStat handaliStat:handaliStats){
            int level=statService.checkHandaliStatForLevel(handaliStat.getStat().getValue());
            imageName.append("_").append(level);
        }
        imageName.append(".png");

        String resultImage=imageName.toString();

        //4. handali 테이블에 변경된 이미지 저장
        handali.setImage(resultImage);
        handaliRepository.save(handali);

        //5. 도감에 추가
        handbookService.addHandbook(user,resultImage);

        return resultImage;
    }

    /** [한달이 상태 조회]*/
    public HandaliDTO.HandaliStatusResponse getHandaliStatusByMonth(String token) {
        User user = userService.tokenToUser(token);

        Handali handali = findHandaliByCurrentDateAndUser(user);
        if(handali==null){throw new HandaliNotFoundException("한달이가 존재하지 않습니다.");}

        int days_Since_Created = Period.between(handali.getStartDate(), LocalDate.now()).getDays()+1;

        String backgroundImg=getUserItemName(user,ItemType.BACKGROUND);
        String wallImg=getUserItemName(user,ItemType.WALL);
        String sofaImg=getUserItemName(user,ItemType.SOFA);
        String floorImg=getUserItemName(user,ItemType.FLOOR);

        //스탯 타입별 스탯값 추출하기
        // 스탯값 변수 초기화 (기본값)
        float activityValue = 0.0f;
        float artValue = 0.0f;
        float intelligentValue = 0.0f;

        Optional<HandaliStat> activityStat = handaliStatRepository.findByHandaliAndType(handali, TypeName.ACTIVITY_SKILL);
        if (activityStat.isPresent()) {
            activityValue = activityStat.get().getStat().getValue();
        }

        Optional<HandaliStat> artStat = handaliStatRepository.findByHandaliAndType(handali, TypeName.ART_SKILL);
        if (artStat.isPresent()) {
            artValue = artStat.get().getStat().getValue();
        }
        Optional<HandaliStat> intelligentStat = handaliStatRepository.findByHandaliAndType(handali, TypeName.INTELLIGENT_SKILL);
        if (intelligentStat.isPresent()) {
            intelligentValue = intelligentStat.get().getStat().getValue();
        }

        //스탯 값에 가장 가까운 최대 스탯값 반환
        int maxStatActivity = statService.findMaxLevel(activityValue);
        int maxStatArt = statService.findMaxLevel(artValue);
        int maxStatIntelligent = statService.findMaxLevel(intelligentValue);


        return new HandaliDTO.HandaliStatusResponse(
                handali.getNickname(),
                days_Since_Created,
                handali.getUser().getTotal_coin(),
                handali.getImage(),
                backgroundImg,
                wallImg,
                sofaImg,
                floorImg,
                activityValue,
                artValue,
                intelligentValue,
                maxStatActivity,
                maxStatArt,
                maxStatIntelligent
        );

    }

    private String getUserItemName(User user,ItemType itemType) {
        return userItemRepository.findByUserAndItemType(user, itemType)
                .map(userItem->userItem.getStore().getName())
                .orElse("none");
    }

    /**[마지막 생성 한달이 조회]*/
    public HandaliDTO.RecentHandaliResponse getRecentHandali(String token) {
        // 사용자 인증
        User user = userService.tokenToUser(token);
        if (user == null) {
            throw new HandaliNotFoundException("사용자를 찾을 수 없습니다.");
        }

        // 가장 최근 생성된 한달이 찾기 (없으면 예외 발생)
        return handaliRepository.findLatestHandaliByUser(user.getUserId())
                .map(handali
                        -> {
                    String jobName = (handali.getJob() != null) ? handali.getJob().getName() : "미취업";
                    int weekSalary = (handali.getJob() != null) ? handali.getJob().getWeekSalary() : 0;

                    return new HandaliDTO.RecentHandaliResponse(
                            handali.getNickname(),
                            handali.getHandaliId(),
                            handali.getStartDate(),
                            jobName,
                            weekSalary,
                            handali.getImage()
                    );
                }).orElseThrow(() -> new HandaliNotFoundException("최근 생성된 한달이가 없습니다."));
    }

    /**
     * [주급 계산]
     */
    @Transactional(readOnly = true) // 조회 기능이므로 readOnly=true 설정
    public HandaliDTO.GetWeekSalaryApiResponseDTO getWeekSalaryInfo(String token) {
        User user = userService.tokenToUser(token);

        //1. 직업 가진 한달이 찾기
        List<Handali> handalis = handaliRepository.findByUserAndJobIsNotNull(user);

        //2. 각 한달이의 정보 조회
        List<HandaliDTO.GetWeekSalaryResponseDTO> responses = new ArrayList<>();
        List<TypeName> typeNames=List.of(TypeName.ACTIVITY_SKILL, TypeName.INTELLIGENT_SKILL,TypeName.ART_SKILL);
        int total_salary = 0; //모든 한달이의 주급의 합

        for (Handali handali : handalis) {
            //주급 계산
            int expectedSalary = handaliScheduler.calculateSalaryFor(handali);

            //스탯에 따른 레벨 계산
            List<HandaliStat> handaliStats = handaliStatRepository.findByHandaliAndStatType(handali, typeNames);
            float activityStat=0.0f;
            float intelligentStat=0.0f;
            float artStat=0.0f;

            for (HandaliStat handaliStat : handaliStats) {
                float value=handaliStat.getStat().getValue();
                switch(handaliStat.getStat().getTypeName()){
                    case ACTIVITY_SKILL -> activityStat=value;
                    case INTELLIGENT_SKILL -> intelligentStat=value;
                    case ART_SKILL -> artStat=value;
                }
            }

            //응답 생성
            responses.add(new HandaliDTO.GetWeekSalaryResponseDTO(
                    handali.getNickname(),
                    handali.getJob().getName(),
                    expectedSalary,
                    handali.getStartDate(),
                    statService.checkHandaliStatForLevel(activityStat),
                    statService.checkHandaliStatForLevel(intelligentStat),
                    statService.checkHandaliStatForLevel(artStat)));

            //한달이들 주급 총합 계산
            total_salary += expectedSalary;
        }

        //3. 최종 반환형으로 반환
        return new HandaliDTO.GetWeekSalaryApiResponseDTO(responses,total_salary,handalis.size());
    }
}
