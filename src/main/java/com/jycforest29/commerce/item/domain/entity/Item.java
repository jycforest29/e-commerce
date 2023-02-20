package com.jycforest29.commerce.item.domain.entity;

import com.jycforest29.commerce.review.domain.entity.Review;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Item {
    // 기본 키 생성을 DB에 위임하여 auto increment 수행.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private int price;

    private int number;

    @OneToMany(mappedBy = "item")
    private List<Review> reviewList = new ArrayList<>();

    @Builder
    public Item(String name, Integer price, Integer number){
        this.name = name;
        this.price = price;
        this.number = number;
    }

    public void increaseItemNumber(Integer increaseNumber){
        this.number += increaseNumber.intValue();
    }

    public void decreaseItemNumber(int decreaseNumber){
        this.number -= decreaseNumber;
    }


    public void addReview(Review review){
        this.reviewList.add(review);
        review.setItem(this);
    }

    public void deleteReview(Review review){
        this.reviewList.remove(review);
    }

}
