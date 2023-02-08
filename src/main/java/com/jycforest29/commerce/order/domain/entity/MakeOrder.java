package com.jycforest29.commerce.order.domain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jycforest29.commerce.cart.domain.entity.CartUnit;
import com.jycforest29.commerce.user.domain.entity.AuthUser;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class MakeOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "authuser_id")
    private AuthUser authUser;

    @Setter
    @OneToMany(mappedBy = "makeOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderUnit> orderUnitList = new ArrayList<>();

    @Setter
    private int totalPrice = 0;

    @CreationTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @Builder
    public MakeOrder(AuthUser authUser){
        this.authUser = authUser;
    }

    public void addOrderUnit(OrderUnit orderUnit) {
        this.orderUnitList.add(orderUnit);
        this.totalPrice += orderUnit.getNumber()*orderUnit.getItem().getPrice();
    }

    public void addOrderUnitForCart(List<CartUnit> cartUnitList) {
        this.orderUnitList.addAll(cartUnitList.stream()
                .map(s -> OrderUnit.builder()
                .makeOrder(this)
                .item(s.getItem())
                .number(s.getNumber())
                .build())
                .collect(Collectors.toList())
        );
        this.totalPrice += orderUnitList.stream()
                .mapToInt(s -> s.getNumber()*s.getItem().getPrice())
                .sum();
    }
}