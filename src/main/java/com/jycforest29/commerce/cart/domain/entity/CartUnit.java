package com.jycforest29.commerce.cart.domain.entity;

import com.jycforest29.commerce.item.domain.entity.Item;
import com.jycforest29.commerce.order.domain.entity.OrderUnit;
import lombok.*;

import javax.persistence.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class CartUnit {
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item; // CartUnit(다) -> Item(일)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @Setter
    @Getter
    @Column(nullable = false)
    private Integer number;

    @Builder
    public CartUnit(Item item, Cart cart, Integer number){
        this.item = item;
        this.cart = cart;
        this.number = number;
    }
}
