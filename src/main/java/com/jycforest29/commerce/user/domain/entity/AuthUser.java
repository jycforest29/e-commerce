package com.jycforest29.commerce.user.domain.entity;

import com.jycforest29.commerce.cart.domain.entity.Cart;
import com.jycforest29.commerce.order.domain.entity.MakeOrder;
import com.jycforest29.commerce.review.domain.entity.Review;
import com.jycforest29.commerce.review.domain.entity.ReviewLikeUnit;
import com.jycforest29.commerce.security.dto.register.AuthUserRequestDto;
import com.jycforest29.commerce.user.domain.enums.Grade;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
public class AuthUser {
    @Id
    @Setter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(unique = true, nullable = false)
    private String nickname;

    @Setter
    @Enumerated(EnumType.STRING)
    private Grade grade = Grade.ORANGE;

    @Setter
    private int reserves = 0;

    // AuthUser를 주테이블로 한 일대일 단방향.
    // @JoinColumn : name 속성에는 맵핑할 외래 키 이름을 설정해주고 이 어노테이션은 생략 가능함.
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    private Cart cart = new Cart();

    @OneToMany(mappedBy = "authUser", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviewList = new ArrayList<>();

    // 다대일 양방향.
    @OneToMany(mappedBy = "authUser", cascade = CascadeType.ALL)
    private List<MakeOrder> makeOrderList = new ArrayList<>();

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReviewLikeUnit> reviewLikeUnitList = new ArrayList<>();

    public AuthUser addReview(Review review){
        this.reviewList.add(review);
        return this;
    }

    @Builder
    public AuthUser(String username, String password, String nickname){
        this.username = username;
        this.password = password;
        this.nickname = nickname;
    }

    public static AuthUser from(AuthUserRequestDto authUserRequest){
        return AuthUser.builder()
                .username(authUserRequest.getUsername())
                .password(authUserRequest.getPassword())
                .nickname(authUserRequest.getNickname())
                .build();
    }

    public void deleteReview(Review review) {
        this.reviewList.remove(review);
    }

    public void addReviewLikeUnit(ReviewLikeUnit reviewLikeUnit) {
        this.reviewLikeUnitList.add(reviewLikeUnit);
    }

    public void deleteReviewLikeUnit(ReviewLikeUnit reviewLikeUnit) {
        this.reviewLikeUnitList.remove(reviewLikeUnit);
    }
}
