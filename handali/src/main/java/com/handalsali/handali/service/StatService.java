package com.handalsali.handali.service;

import com.handalsali.handali.domain.Handali;
import com.handalsali.handali.domain.HandaliStat;
import com.handalsali.handali.domain.Stat;
import com.handalsali.handali.domain.User;
import com.handalsali.handali.enums_multyKey.Categoryname;
import com.handalsali.handali.enums_multyKey.TypeName;
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
    public void statUpdate(User user, Categoryname categoryname, float time, int satisfaction, LocalDate date){
        //1. 한달이 찾기
        Handali handali=handaliService.findHandaliByCurrentDateAndUser(user);

        //2. 한달이의 어떤 스탯을 올려야 하는지 찾기
        

        //3. 스탯 계산
        //4. 저장
    }
}
