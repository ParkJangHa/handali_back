package com.handalsali.handali.domain;

import com.handalsali.handali.Enums_MultyKey.ApartId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
public class Apart {
    //복합키
    @EmbeddedId
    ApartId apart_id;

    @ManyToOne
    @JoinColumn(name="user_id",
            foreignKey = @ForeignKey(foreignKeyDefinition = "FOREIGN KEY (user_id) REFERENCES user(user_id) ON DELETE CASCADE ON UPDATE CASCADE", name="fk_user"))
    private User user;
}
