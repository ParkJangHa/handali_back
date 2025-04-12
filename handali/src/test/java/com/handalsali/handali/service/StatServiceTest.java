package com.handalsali.handali.service;

import com.handalsali.handali.domain.Handali;
import com.handalsali.handali.domain.HandaliStat;
import com.handalsali.handali.domain.Stat;
import com.handalsali.handali.domain.User;
import com.handalsali.handali.enums_multyKey.Categoryname;
import com.handalsali.handali.enums_multyKey.TypeName;
import com.handalsali.handali.repository.HandaliRepository;
import com.handalsali.handali.repository.HandaliStatRepository;
import com.handalsali.handali.repository.StatRepository;
import com.handalsali.handali.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.handalsali.handali.domain.Record;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class StatServiceTest {
    private static final Logger logger = LoggerFactory.getLogger(StatServiceTest.class);

    @Autowired
    private StatService statService;
    @Autowired
    private HandaliRepository handaliRepository;
    @Autowired
    private StatRepository statRepository;
    @Autowired
    private HandaliStatRepository handaliStatRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    public void testStatInit_지난달스탯반영(){

        User user=new User("aaa@gmail.com","name","1234","010-1234-5678", LocalDate.now());
        userRepository.save(user);

        // 1. 지난달 한달이 생성 및 스탯 설정
        Handali lastMonthHandali = new Handali("last",LocalDate.from(LocalDate.now().minusMonths(1).atStartOfDay()),user);
        handaliRepository.save(lastMonthHandali);

        Stat lastActivity=new Stat(TypeName.ACTIVITY_SKILL);
        lastActivity.setValue(50);
        Stat lastIntelligent=new Stat(TypeName.INTELLIGENT_SKILL);
        lastIntelligent.setValue(60);
        Stat lastArt=new Stat(TypeName.ART_SKILL);
        lastArt.setValue(70);
        statRepository.saveAll(List.of(lastActivity,lastIntelligent,lastArt));

        HandaliStat handaliStat1=new HandaliStat(lastMonthHandali,lastActivity);
        HandaliStat handaliStat2=new HandaliStat(lastMonthHandali,lastArt);
        HandaliStat handaliStat3=new HandaliStat(lastMonthHandali,lastIntelligent);
        handaliStatRepository.saveAll(List.of(handaliStat1,handaliStat2,handaliStat3));

        // 2. 새 한달이 생성 및 statInit()실행
        Handali newHandali = new Handali("now",LocalDate.now(),user);
        handaliRepository.save(newHandali);

        statService.statInit(user,newHandali);

        // 3. 새로운 스탯이 잘 생성 됐는 지 확인
        List<HandaliStat> stats=handaliStatRepository.findByHandali(newHandali);
        assertEquals(3,stats.size());

        // 4. 지날달 스탯 값이 잘 반영 되었는지 확인
        for (HandaliStat stat : stats) {
            switch(stat.getStat().getTypeName()){
                case ACTIVITY_SKILL -> assertEquals(50,stat.getStat().getLastMonthValue());
                case INTELLIGENT_SKILL -> assertEquals(60,stat.getStat().getLastMonthValue());
                case ART_SKILL -> assertEquals(70,stat.getStat().getLastMonthValue());
            }
        }
    }

    @Test
    public void testStatInit_지난달스탯반영_지난한달이가없을때(){

        User user=new User("aaa@gmail.com","name","1234","010-1234-5678", LocalDate.now());
        userRepository.save(user);

        // 1. 새 한달이 생성 및 statInit()실행
        Handali newHandali = new Handali("now",LocalDate.now(),user);
        handaliRepository.save(newHandali);

        statService.statInit(user,newHandali);

        // 3. 새로운 스탯이 잘 생성 됐는 지 확인
        List<HandaliStat> stats=handaliStatRepository.findByHandali(newHandali);
        assertEquals(3,stats.size());

        // 4. 지날달 스탯 값이 잘 반영 되었는지 확인
        for (HandaliStat stat : stats) {
            switch(stat.getStat().getTypeName()){
                case ACTIVITY_SKILL -> assertEquals(0,stat.getStat().getLastMonthValue());
                case INTELLIGENT_SKILL -> assertEquals(0,stat.getStat().getLastMonthValue());
                case ART_SKILL -> assertEquals(0,stat.getStat().getLastMonthValue());
            }
        }
    }

    @Test
    public void testStatUpdateAndCheckHandaliStat_levelUp_false(){

        //1. 사용자, 한달이 설정
        Handali handali = setHandaliAndUser(90,0);

        //2. 함수 실행
        boolean levelUp = statService.statUpdateAndCheckHandaliStat(
                handali,
                Categoryname.ACTIVITY,
                15,
                2.0f,
                3.0f,
                50
        );

        //3. 성장하지 않음
        assertFalse(levelUp);
    }

    @Test
    public void testStatUpdateAndCheckHandaliStat_levelUp_true(){

        //1. 사용자, 한달이 설정
        Handali handali = setHandaliAndUser(90,0);

        //2. 함수 실행
        boolean levelUp = statService.statUpdateAndCheckHandaliStat(
                handali,
                Categoryname.ACTIVITY,
                15,
                2.0f,
                3.0f,
                100
        );

        //3. 성장 했어야 함
        assertFalse(levelUp);
    }

    @Test
    public void testStatUpdateAndCheckHandaliStat_levelUp_false_스탯이1000일때(){

        //1. 사용자, 한달이 설정
        Handali handali = setHandaliAndUser(1000,0);

        //2. 함수 실행
        boolean levelUp = statService.statUpdateAndCheckHandaliStat(
                handali,
                Categoryname.ACTIVITY,
                15,
                2.0f,
                3.0f,
                50
        );

        //3. 성장하지 않음
        assertFalse(levelUp);
    }

    private Handali setHandaliAndUser(float value,float lastMonthValue) {
        User user=new User("aaa@gmail.com","name","1234","010-1234-5678", LocalDate.now());
        userRepository.save(user);

        Handali handali = new Handali("now",LocalDate.now(),user);
        handaliRepository.save(handali);

        Stat activityStat=new Stat(TypeName.ACTIVITY_SKILL);
        activityStat.setValue(value); //100 -> 1단계
        activityStat.setLastMonthValue(lastMonthValue);
        statRepository.save(activityStat);

        HandaliStat activityHandaliStat=new HandaliStat(handali,activityStat);
        handaliStatRepository.save(activityHandaliStat);
        return handali;
    }

    @Test
    public void testCalculateStatValue(){
        User user=new User("aaa@gmail.com","name","1234","010-1234-5678", LocalDate.now());
        userRepository.save(user);

        Handali handali = new Handali("now",LocalDate.now(),user);
        handaliRepository.save(handali);

        Stat activityStat=new Stat(TypeName.ACTIVITY_SKILL);
        activityStat.setValue(0); //100 -> 1단계
        activityStat.setLastMonthValue(100);
        statRepository.save(activityStat);

        HandaliStat activityHandaliStat=new HandaliStat(handali,activityStat);
        handaliStatRepository.save(activityHandaliStat);

        float result = statService.calculateStatValue(0, 0, activityHandaliStat, 2.0f, 50);

        logger.info("[calculateStatValue 테스트 결과] 스탯 증가량: "+result);

    }


    @Test
    public void testCheckHandaliStat(){
        assertEquals(1,statService.checkHandaliStat(100));
        assertEquals(2,statService.checkHandaliStat(250));
        assertEquals(3,statService.checkHandaliStat(450));
        assertEquals(4,statService.checkHandaliStat(700));
        assertEquals(5,statService.checkHandaliStat(1100));
    }
}
