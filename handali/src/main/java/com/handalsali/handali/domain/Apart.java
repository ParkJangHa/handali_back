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

    @Id
    @Column(name="apart_total_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long apartTotalId;

//    @Embedded
//    private ApartId apartId;  // 복합 키

    private int apartId;
    private int floor;

    @OneToOne
    @JoinColumn(name="handali_id", referencedColumnName = "handali_id", nullable = false)
    private Handali handali;

    @Column(name = "nickname")
    private String nickname;

//    public int getFloor() {
//        return apartId.getFloor();
//    }

    @ManyToOne
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(
            foreignKeyDefinition = "FOREIGN KEY (user_id) REFERENCES user(user_id) ON DELETE CASCADE ON UPDATE CASCADE",
            name = "fk_user"))
    private User user;

    public Apart(User user, Handali handali, String nickname, int floor, int apartId) {
        this.apartId = apartId;
        this.floor = floor;
        this.user = user;
        this.handali = handali;
        this.nickname = nickname;
    }
}
