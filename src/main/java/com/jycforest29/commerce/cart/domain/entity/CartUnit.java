package com.jycforest29.commerce.cart.domain.entity;

import com.jycforest29.commerce.item.domain.entity.Item;
import lombok.*;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class CartUnit {
    /*
    --------------------
    id(pk) : Long
    item_id(fk) : Long
    cart_id(fk) : Long
    number : int
    available : Boolean
    --------------------
    */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    private Cart cart;

    private int number;

    @Setter
    private Boolean available = true;

    @Builder
    public CartUnit(Item item, int number){
        this.item = item;
        this.number = number;
    }
}
