package com.handalsali.handali.domain;

import com.handalsali.handali.enums.ItemType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
public class UserItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userItemId;

    @JoinColumn(name="user_id",
            foreignKey = @ForeignKey (foreignKeyDefinition = "FOREIGN KEY (user_id) REFERENCES user(user_id) ON DELETE CASCADE ON UPDATE CASCADE",
                    name="fk_item_user"))
    @ManyToOne
    private User user;

    @JoinColumn(name="store_id",
            foreignKey = @ForeignKey (foreignKeyDefinition = "FOREIGN KEY (store_id) REFERENCES store(store_id) ON DELETE CASCADE ON UPDATE CASCADE",
                    name="fk_item_store"))
    @ManyToOne
    private Store store;

    private boolean isAvailable;

    public UserItem(User user, Store store) {
        this.user = user;
        this.store = store;
        this.isAvailable = true;
    }

    //비지니스 로직
    /**
     * 아이템 적용 취소
     */
    public void cancelItem() {
        this.isAvailable=false;
    }

    /**
     * 아이템 적용
     */
    public void setItem(){
        this.isAvailable=true;
    }
}
