package com.jycforest29.commerce.cart.domain.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@Entity
public class Cart { // 유저 -> 카트로만 접근이 가능한 일대일 단방향.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartUnit> cartUnitList = new ArrayList<>();

    private int totalPrice = 0;

    public void addCartUnitToCart(CartUnit cartUnit, int price){
        this.cartUnitList.add(cartUnit);
        this.totalPrice += cartUnit.getNumber() * price;
    }

    public void removeAllCartUnit(){
        this.cartUnitList.clear();
        this.totalPrice = 0;
    }

    public void removeCartUnitFromCart(CartUnit cartUnit, int price){
        this.cartUnitList.remove(cartUnit);
        this.totalPrice -= cartUnit.getNumber() * price;
    }
}
