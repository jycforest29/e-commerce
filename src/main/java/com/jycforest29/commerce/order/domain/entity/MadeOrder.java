package com.jycforest29.commerce.order.domain.entity;

import com.jycforest29.commerce.user.domain.entity.AuthUser;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@EntityListeners(value = {AuditingEntityListener.class})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class MadeOrder {
    /*
    --------------------
    id(pk) : Long
    authuser_id(fk) : Long
    total_price : int
    created_at : LocalDateTime
    --------------------
    */
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
        // MadeOrder과 AuthUser의 연관관계 설정
        MadeOrder madeOrder = MadeOrder.builder()
                .authUser(authUser)
                .build();
        authUser.getMadeOrderList().add(madeOrder);

        // MadeOrder과 OrderUnit의 연관관계 설정
        for(OrderUnit o : orderUnitList){
            o.setMadeOrder(madeOrder);
            madeOrder.orderUnitList.add(o);
            madeOrder.totalPrice += o.getNumber() * o.getItem().getPrice();
        }
        return madeOrder;
    }

    public List<Long> deleteMadeOrder(AuthUser authUser, List<OrderUnit> orderUnitList){
        // MadeOrder와 AuthUser의 연관관계 해제
        this.authUser = null;
        authUser.getMadeOrderList().remove(this);

        List<Long> orderUnitIdListToDelete = this.orderUnitList.stream()
                .map(s -> s.getId())
                .collect(Collectors.toList());

        // MadeOrder와 OrderUnit의 연관관계 해제
        for(OrderUnit o : orderUnitList){
            o.setMadeOrder(null);
        }
        this.orderUnitList.removeAll(orderUnitList);
        this.totalPrice = 0;

        return orderUnitIdListToDelete;
    }
}