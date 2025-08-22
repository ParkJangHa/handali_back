package com.handalsali.handali.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
@Table(name="user_handbook")
public class UserHandbook {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="user_handbook_id")
    private Long userHandbookId;

    @ManyToOne
    @JoinColumn(name="user_id", nullable=false,
            foreignKey = @ForeignKey(foreignKeyDefinition = "FOREIGN KEY (user_id) REFERENCES user(user_id) ON DELETE CASCADE ON UPDATE CASCADE"))
    private User user;

    @ManyToOne
    @JoinColumn(name = "handbook_id", nullable = false,
            foreignKey = @ForeignKey(foreignKeyDefinition = "FOREIGN KEY (handbook_id) REFERENCES handbook(handbook_id) ON DELETE CASCADE ON UPDATE CASCADE"))
    private Handbook handbook;

    private LocalDateTime createdAt = LocalDateTime.now();

    public UserHandbook(User user, Handbook handbook) {
        this.user = user;
        this.handbook = handbook;
    }
}

