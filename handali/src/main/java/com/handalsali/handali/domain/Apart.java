package com.handalsali.handali.domain;

import com.handalsali.handali.enums_multyKey.ApartId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Setter
public class Apart {

    @EmbeddedId
    private ApartId apartId;  // 복합 키

    @Column(name = "floor", nullable = false)
    private Integer floor;

    @OneToOne
    @JoinColumn(name="handali_id", referencedColumnName = "handali_id", insertable=false, updatable=false)
    private Handali handali;

    @Column(name = "nickname")
    private String nickname;

    @ManyToOne
    @MapsId("apartId") // 복합 키의 일부로 설정
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(
            foreignKeyDefinition = "FOREIGN KEY (user_id) REFERENCES user(user_id) ON DELETE CASCADE ON UPDATE CASCADE",
            name = "fk_user"))
    private User user;

    public Apart(User user, Handali handali, String nickname, int floor, Long apartId) {
        this.apartId = new ApartId(apartId, handali.getHandaliId()); // 복합 키 설정
        this.user = user;
        this.handali = handali;
        this.nickname = nickname;
        this.floor = floor;
    }
}
