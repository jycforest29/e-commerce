package com.jycforest29.commerce.order.domain.entity;

import com.jycforest29.commerce.cart.domain.entity.CartUnit;
import com.jycforest29.commerce.item.domain.entity.Item;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
public class OrderUnit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "madeOrder_id")
    private MadeOrder madeOrder;

    private int number;

    @Builder
    public OrderUnit(Item item, Integer number){
        this.item = item;
        this.number = number;
    }

    public static OrderUnit mapToOrderUnit(CartUnit cartUnit){
        return OrderUnit.builder()
                .item(cartUnit.getItem())
                .number(cartUnit.getNumber())
                .build();
    }
}
