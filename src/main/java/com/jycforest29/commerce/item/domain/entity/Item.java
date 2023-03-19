package com.jycforest29.commerce.item.domain.entity;

import com.jycforest29.commerce.review.domain.entity.Review;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor // 테스트
@Entity
public class Item {
    /*
    --------------------
    id(pk) : Long
    name : String
    price : int
    number : int
    --------------------
    */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 기본 키 생성을 DB에 위임하여 auto increment 수행.
    private Long id;

    @Column(nullable = false)
    private String name;

    private int price;

    private int number;

    @OneToMany(mappedBy = "item")
    private List<Review> reviewList = new ArrayList<>();

    @Builder
    public Item(String name, int price, int number){
        this.name = name;
        this.price = price;
        this.number = number;
    }

    public void increaseItemNumber(int increaseNumber){
        this.number += increaseNumber;
    }

    public void decreaseItemNumber(int decreaseNumber){
        this.number -= decreaseNumber;
    }

    public void addReview(Review review){
        review.setItem(this);
        this.reviewList.add(review);
    }

    public void deleteReview(Review review){
        review.setItem(null);
        this.reviewList.remove(review);
    }
}
