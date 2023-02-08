package com.jycforest29.commerce.item.domain.entity;

import com.jycforest29.commerce.cart.domain.entity.CartUnit;
import com.jycforest29.commerce.order.domain.entity.OrderUnit;
import com.jycforest29.commerce.review.domain.entity.Review;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
public class Item {
    // 기본 키 생성을 DB에 위임하여 auto increment 수행.
    @Id
    @Setter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int price;

    private int number;

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviewList = new ArrayList<>();

    public Item addReview(Review review){
        this.reviewList.add(review);
        return this;
    }

    public void deleteReview(Review review){
        this.reviewList.remove(review);
    }

    @Builder
    public Item(String name, Integer price, Integer number){
        this.name = name;
        this.price = price;
        this.number = number;
    }

}
