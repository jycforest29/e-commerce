package com.jycforest29.commerce.order.domain.entity;

import com.jycforest29.commerce.user.domain.entity.AuthUser;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class MadeOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "authuser_id")
    private AuthUser authUser;

    @Setter
    @OneToMany(mappedBy = "madeOrder")
    private List<OrderUnit> orderUnitList = new ArrayList<>();

    @Setter
    private int totalPrice = 0;

    @CreatedDate
    private LocalDateTime createdAt;

    @Builder
    public MadeOrder(AuthUser authUser){
        this.authUser = authUser;
    }

    public static MadeOrder addOrderUnit(AuthUser authUser, List<OrderUnit> orderUnitList) {
        // MadeOrder의 연관관계 설정 완료
        MadeOrder madeOrder = MadeOrder.builder()
                .authUser(authUser)
                .build();
        authUser.getMadeOrderList().add(madeOrder);

        // OrderUnit의 연관관계 설정 완료
        for(OrderUnit o : orderUnitList){
            madeOrder.orderUnitList.add(o);
            madeOrder.totalPrice += o.getNumber() * o.getItem().getPrice();
            o.setMadeOrder(madeOrder);
        }
        return madeOrder;
    }

    public void deleteOrder(AuthUser authUser, List<OrderUnit> orderUnitList){
        // AuthUser와 MadeOrder
        authUser.getMadeOrderList().remove(this);
        // MadeOrder와 OrderUnit
        for(OrderUnit o : orderUnitList){
            o.setMadeOrder(null);
        }
        this.orderUnitList.removeAll(orderUnitList);
        this.totalPrice = 0;
    }
}