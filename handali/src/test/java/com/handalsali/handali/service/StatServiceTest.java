package com.handalsali.handali.service;

import com.handalsali.handali.domain.Handali;
import com.handalsali.handali.domain.HandaliStat;
import com.handalsali.handali.domain.Stat;
import com.handalsali.handali.domain.User;
import com.handalsali.handali.enums_multyKey.TypeName;
import com.handalsali.handali.repository.HandaliRepository;
import com.handalsali.handali.repository.HandaliStatRepository;
import com.handalsali.handali.repository.StatRepository;
import com.handalsali.handali.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
public class StatServiceTest {
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
    public void testStatInit_reflectLastMonthStats_isLastMonthHandali(){

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
    public void testStatInit_reflectLastMonthStats_isNotLastMonthHandali(){

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
}
