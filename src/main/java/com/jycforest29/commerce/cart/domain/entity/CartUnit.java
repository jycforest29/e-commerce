package com.jycforest29.commerce.cart.domain.entity;

import com.jycforest29.commerce.item.domain.entity.Item;
import lombok.*;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class CartUnit {
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

    @Builder
    public CartUnit(Item item, int number){
        this.item = item;
        this.number = number;
    }
}
