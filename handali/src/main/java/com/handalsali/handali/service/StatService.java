package com.handalsali.handali.service;

import com.handalsali.handali.domain.Handali;
import com.handalsali.handali.domain.HandaliStat;
import com.handalsali.handali.domain.Stat;
import com.handalsali.handali.enums_multyKey.TypeName;
import com.handalsali.handali.repository.HandaliStatRepository;
import com.handalsali.handali.repository.StatRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class StatService {
    private final StatRepository statRepository;
    private final HandaliStatRepository handaliStatRepository;
    public StatService(StatRepository statRepository, HandaliStatRepository handaliStatRepository) {
        this.statRepository = statRepository;
        this.handaliStatRepository = handaliStatRepository;
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
}
