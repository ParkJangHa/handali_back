package com.handalsali.handali.domain;

import com.handalsali.handali.enums.ItemType;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@Entity
@Table(
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "itemType"}) // 핵심!
        }
)
@NoArgsConstructor
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

    @Enumerated(EnumType.STRING)
    private ItemType itemType;

    private boolean isAvailable;

    public UserItem(User user, Store store, ItemType itemType, boolean isAvailable) {
        this.user = user;
        this.store = store;
        this.itemType = itemType;
        this.isAvailable = isAvailable;
    }
}
