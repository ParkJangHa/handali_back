package com.handalsali.handali.service;

import com.handalsali.handali.domain.Handali;
import com.handalsali.handali.domain.HandaliStat;
import com.handalsali.handali.domain.Stat;
import com.handalsali.handali.domain.User;
import com.handalsali.handali.enums_multyKey.Categoryname;
import com.handalsali.handali.enums_multyKey.TypeName;
import com.handalsali.handali.exception.HandaliNotFoundException;
import com.handalsali.handali.exception.HandaliStatNotFoundException;
import com.handalsali.handali.repository.HandaliStatRepository;
import com.handalsali.handali.repository.StatRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@Transactional
public class StatService {
    private final StatRepository statRepository;
    private final HandaliStatRepository handaliStatRepository;
    private final HandaliService handaliService;
    public StatService(StatRepository statRepository, HandaliStatRepository handaliStatRepository, HandaliService handaliService) {
        this.statRepository = statRepository;
        this.handaliStatRepository = handaliStatRepository;
        this.handaliService = handaliService;
    }

    //한달이 생성후, 스탯 초기화
    public void statInit(Handali handali){
        float initValue=0;

        //스탯 초기화
        Stat activityStat=new Stat(TypeName.ACTIVITY_SKILL,initValue);
        Stat intelligentStat=new Stat(TypeName.INTELLIGENT_SKILL,initValue);
        Stat artStat=new Stat(TypeName.ART_SKILL,initValue);

        statRepository.save(activityStat);
        statRepository.save(intelligentStat);
        statRepository.save(artStat);

        //한달이-스탯 관계 설정
        HandaliStat activityHandaliStat=new HandaliStat(handali,activityStat);
        HandaliStat intelligentHandaliStat=new HandaliStat(handali,intelligentStat);
        HandaliStat artHandaliStat=new HandaliStat(handali,artStat);

        handaliStatRepository.save(activityHandaliStat);
        handaliStatRepository.save(intelligentHandaliStat);
        handaliStatRepository.save(artHandaliStat);
    }

    //[스탯 업데이트]
    public void statUpdate(User user, Categoryname categoryname, float time, int satisfaction){
        //1. 한달이 찾기
        Handali handali=handaliService.findHandaliByCurrentDateAndUser(user);
        if(handali==null) throw new HandaliNotFoundException("한달이를 찾을 수 없습니다.");

        //2. 한달이의 어떤 스탯을 올려야 하는지 찾기
        TypeName currentStatType = switch (categoryname) {
            case ACTIVITY -> TypeName.ACTIVITY_SKILL;
            case ART -> TypeName.ART_SKILL;
            case INTELLIGENT -> TypeName.INTELLIGENT_SKILL;
        };

        //3. 한달이의 스탯 타입에 따른 한달이-스탯 찾기
        HandaliStat handaliStat=handaliStatRepository.findByHandaliAndType(handali,currentStatType)
                .orElseThrow(() -> new HandaliStatNotFoundException("스탯을 찾을 수 없습니다."));

        //4. 스탯 값 업데이트
        float incrementValue = calculateStatValue(time, satisfaction);
        handaliStat.getStat().setValue(handaliStat.getStat().getValue()+incrementValue);
        // 데이터는 트랜잭션 종료 시 자동으로 저장됨 (save 불필요)
    }

    // 스탯 증가 계산
    private float calculateStatValue(float time, int satisfaction) {
        // 기본 배율과 보너스 배율 설정
        final float baseMultiplier = 1.0f; // 기본 배율
        final float bonusMultiplier = 1.0f; // 추가 배율 (성취도에 따라 증가)

        // 배율 계산
        float multiplier = baseMultiplier + (satisfaction / 100.0f) * bonusMultiplier;

        // 스탯 증가 값 계산
        return time * multiplier;
    }
}
