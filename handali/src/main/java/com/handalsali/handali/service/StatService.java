package com.handalsali.handali.service;

import com.handalsali.handali.domain.Handali;
import com.handalsali.handali.domain.HandaliStat;
import com.handalsali.handali.domain.Stat;
import com.handalsali.handali.enums_multyKey.Categoryname;
import com.handalsali.handali.enums_multyKey.TypeName;
import com.handalsali.handali.exception.HandaliStatNotFoundException;
import com.handalsali.handali.repository.HandaliRepository;
import com.handalsali.handali.repository.HandaliStatRepository;
import com.handalsali.handali.repository.StatRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
@Transactional
public class StatService {
    private final StatRepository statRepository;
    private final HandaliStatRepository handaliStatRepository;
    private final HandaliRepository handaliRepository;

    public StatService(StatRepository statRepository, HandaliStatRepository handaliStatRepository, HandaliRepository handaliRepository) {
        this.statRepository = statRepository;
        this.handaliStatRepository = handaliStatRepository;
        this.handaliRepository = handaliRepository;
    }

    /**한달이 생성후, 스탯 초기화*/
    public void statInit(Handali handali){

        //1. 스탯 초기화, 0
        Stat activityStat=new Stat(TypeName.ACTIVITY_SKILL);
        Stat intelligentStat=new Stat(TypeName.INTELLIGENT_SKILL);
        Stat artStat=new Stat(TypeName.ART_SKILL);

        //2. 지난달 스탯 반영
        setLastMonthStat(activityStat, intelligentStat, artStat);

        //3. 스탯 데이터베이스에 저장
        statRepository.save(activityStat);
        statRepository.save(intelligentStat);
        statRepository.save(artStat);

        //4. 한달이-스탯 관계 설정
        HandaliStat activityHandaliStat=new HandaliStat(handali,activityStat);
        HandaliStat intelligentHandaliStat=new HandaliStat(handali,intelligentStat);
        HandaliStat artHandaliStat=new HandaliStat(handali,artStat);

        handaliStatRepository.save(activityHandaliStat);
        handaliStatRepository.save(intelligentHandaliStat);
        handaliStatRepository.save(artHandaliStat);
    }

    private void setLastMonthStat(Stat activityStat, Stat intelligentStat, Stat artStat) {
        LocalDate now = LocalDate.now();
        YearMonth yearMonth = YearMonth.from(now).minusMonths(1);
        LocalDate startDate=yearMonth.atDay(1);
        LocalDate endDate=yearMonth.atEndOfMonth();

        //1. 지난달 한달이 찾기
        Handali lastMonthHandali = handaliRepository.findLastMonthHandali(startDate, endDate);

        //2. 지난달 한달이의 스탯 찾아서 현재 한달이의 스탯에 반영하기
        if (lastMonthHandali != null) {
            List<TypeName> typeNames=List.of(TypeName.ACTIVITY_SKILL, TypeName.INTELLIGENT_SKILL,TypeName.ART_SKILL);

            List<HandaliStat> handaliStats = handaliStatRepository.findByHandaliAndStatType(lastMonthHandali, typeNames);
                for (HandaliStat handaliStat : handaliStats) {
                    float value=handaliStat.getStat().getValue();
                    switch(handaliStat.getStat().getTypeName()){
                        case ACTIVITY_SKILL -> activityStat.setLastMonthValue(value);
                        case INTELLIGENT_SKILL -> intelligentStat.setLastMonthValue(value);
                        case ART_SKILL -> artStat.setLastMonthValue(value);
                    }
                }
        }
    }

    /**[스탯 업데이트] 및 한달이 상태 변화 여부 체크*/
    public boolean statUpdateAndCheckHandaliStat(Handali handali, Categoryname categoryname, float time, int satisfaction){

        //3. 한달이의 어떤 스탯을 올려야 하는지 찾기
        TypeName currentStatType = switch (categoryname) {
            case ACTIVITY -> TypeName.ACTIVITY_SKILL;
            case ART -> TypeName.ART_SKILL;
            case INTELLIGENT -> TypeName.INTELLIGENT_SKILL;
        };

        //4. 한달이의 스탯 타입에 따른 한달이-스탯 찾기
        HandaliStat handaliStat=handaliStatRepository.findByHandaliAndType(handali,currentStatType)
                .orElseThrow(() -> new HandaliStatNotFoundException("스탯을 찾을 수 없습니다."));

        //5. 스탯 업데이트 전의 한달이 레벨 확인
        int previousLevel=checkHandaliStat(handaliStat.getStat().getValue());

        //6. 스탯 값 업데이트
        float incrementValue = calculateStatValue(time, satisfaction);
        handaliStat.getStat().setValue(handaliStat.getStat().getValue()+incrementValue);
        handaliStatRepository.save(handaliStat);

        //7. 한달이 상태 변화 검사
        int nowLevel=checkHandaliStat(handaliStat.getStat().getValue());

        return previousLevel!=nowLevel;
    }

    /** 스탯 증가 계산*/
    private float calculateStatValue(float time, int satisfaction) {
        // 기본 배율과 보너스 배율 설정
        final float baseMultiplier = 1.0f; // 기본 배율
        final float bonusMultiplier = 1.0f; // 추가 배율 (성취도에 따라 증가)

        // 배율 계산
        float multiplier = baseMultiplier + (satisfaction / 100.0f) * bonusMultiplier;

        // 스탯 증가 값 계산
        return time * multiplier;
    }

    /**스탯에 따른 레벨 반환*/
    public int checkHandaliStat(float statValue){
        int[] threshold={20,40,70}; //순서대로 1,2,3단계 조건
        int level=0;
        for(int limit:threshold){
            if(statValue>=limit){
                level++;
            }
        }
        return level;
    }

}
