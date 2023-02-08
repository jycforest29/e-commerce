package com.jycforest29.commerce.order.domain.entity;

import com.jycforest29.commerce.item.domain.entity.Item;
import lombok.Builder;
import lombok.Getter;

import javax.persistence.*;

@Getter
@Entity
public class OrderUnit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "makeOrder_id")
    private MakeOrder makeOrder;

    @Column(nullable = false)
    private Integer number;

    @Builder
    public OrderUnit(Item item, MakeOrder makeOrder, Integer number){
        this.item = item;
        this.makeOrder = makeOrder;
        this.number = number;
    }
}
