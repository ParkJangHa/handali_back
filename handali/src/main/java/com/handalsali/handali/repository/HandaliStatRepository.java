package com.handalsali.handali.repository;

import com.handalsali.handali.domain.Handali;
import com.handalsali.handali.domain.HandaliStat;
import com.handalsali.handali.domain.Stat;
import com.handalsali.handali.enums_multyKey.TypeName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface HandaliStatRepository extends JpaRepository<HandaliStat, Long> {
    //어떤 한달이의 어떤 스탯 타입인지에 따른 한달이-스탯 관계 찾기
    @Query("select hs from HandaliStat hs " +
            "join Stat s on hs.stat=s" +
            " where hs.handali=:handali and s.typeName=:typeName")
    Optional<HandaliStat> findByHandaliAndType(@Param("handali") Handali handali, @Param("typeName") TypeName typeName);
}
