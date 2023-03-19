package com.jycforest29.commerce.cart.domain.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@Entity
public class Cart { // 유저 -> 카트로만 접근이 가능한 일대일 단방향.
    /*
    --------------------
    id(pk) : Long
    total_price : int
    --------------------
    */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "cart")
    private List<CartUnit> cartUnitList = new ArrayList<>();

    private int totalPrice = 0;

    public void addCartUnitToCart(CartUnit cartUnit, int price){
        cartUnit.setCart(this);
        if(!cartUnitList.contains(cartUnit)){
            this.cartUnitList.add(cartUnit);
            this.totalPrice += cartUnit.getNumber() * price;
        }
    }

    public List<Long> removeAllCartUnit(){
        List<Long> cartUnitIdListToDelete = this.cartUnitList.stream()
                .map(s -> s.getId())
                .collect(Collectors.toList());
        for(CartUnit cartUnit : this.cartUnitList){
            cartUnit.setCart(null);
        }
        this.cartUnitList.clear();
        this.totalPrice = 0;

        return cartUnitIdListToDelete;
    }

    public void removeCartUnitFromCart(CartUnit cartUnit, int price){
        cartUnit.setCart(null);
        if(cartUnitList.contains(cartUnit)){
            this.cartUnitList.remove(cartUnit);
            this.totalPrice -= cartUnit.getNumber() * price;
        }
    }
}
