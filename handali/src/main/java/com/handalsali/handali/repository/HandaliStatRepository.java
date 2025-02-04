package com.handalsali.handali.repository;

import com.handalsali.handali.domain.Handali;
import com.handalsali.handali.domain.HandaliStat;
import com.handalsali.handali.domain.Stat;
import com.handalsali.handali.enums_multyKey.TypeName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface HandaliStatRepository extends JpaRepository<HandaliStat, Long> {
    //어떤 한달이의 어떤 스탯 타입인지에 따른 한달이-스탯 관계 찾기
    @Query("select hs from HandaliStat hs " +
            "join Stat s on hs.stat=s" +
            " where hs.handali=:handali and s.typeName=:typeName")
    Optional<HandaliStat> findByHandaliAndType(@Param("handali") Handali handali, @Param("typeName") TypeName typeName);

    /**어떤 한달이에 따른 한달이-스탯 관계 찾기, 활동, 지능, 예술 순으로 정렬하여 가져옴*/
    @Query("select hs from HandaliStat hs " +
            "join Stat s on hs.stat=s" +
            " where hs.handali=:handali " +
            "order by s.statId")
    List<HandaliStat> findByHandali(@Param("handali") Handali handali);

    //해당 한달이의 스탯중 가장 높은 스탯 찾기, 가장 높은 값이 중복일 경우 먼저 매핑된 스탯 반환
    @Query("""
        SELECT hs FROM HandaliStat hs
        WHERE hs.handali.handaliId = :handaliId
          AND hs.stat.value = (
              SELECT MAX(hsInner.stat.value)
              FROM HandaliStat hsInner
              WHERE hsInner.handali.handaliId = :handaliId
          )
    """)
    List<HandaliStat> findMaxStatByHandaliId(@Param("handaliId") Long handaliId);
}
