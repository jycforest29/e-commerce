package com.jycforest29.commerce.user.domain.entity;

import com.jycforest29.commerce.cart.domain.entity.Cart;
import com.jycforest29.commerce.order.domain.entity.MadeOrder;
import com.jycforest29.commerce.review.domain.entity.Review;
import com.jycforest29.commerce.review.domain.entity.ReviewLikeUnit;
import com.jycforest29.commerce.user.domain.enums.Grade;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class AuthUser implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    private List<String> roleList = new ArrayList<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = roleList.stream()
                .map(role -> new SimpleGrantedAuthority(username))
                .collect(Collectors.toList());

        return authorities;
    }

    @Column(unique = true, nullable = false)
    private String nickname;

    @Enumerated(EnumType.STRING)
    private Grade grade = Grade.ORANGE;

    private int reserves = 0;

    // AuthUser를 주테이블로 한 일대일 단방향.
    // @JoinColumn : name 속성에는 맵핑할 외래 키 이름을 설정해주고 이 어노테이션은 생략 가능함.
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "authuser")
    @JoinColumn(name = "cart_id")
    private Cart cart = new Cart();

    @OneToMany(mappedBy = "authUser")
    private List<Review> reviewList = new ArrayList<>();

    // 다대일 양방향.
    @OneToMany(mappedBy = "authUser")
    private List<MadeOrder> madeOrderList = new ArrayList<>();

    @OneToMany(mappedBy = "review")
    private List<ReviewLikeUnit> reviewLikeUnitList = new ArrayList<>();

    @Builder
    public AuthUser(String username, String password, String nickname){
        this.username = username;
        this.password = password;
        this.nickname = nickname;
    }

    public void addReview(Review review){
        this.reviewList.add(review);
        review.setAuthUser(this);
    }

    public void deleteReview(Review review) {
        this.reviewList.remove(review);
    }

    public void addReviewLikeUnit(ReviewLikeUnit reviewLikeUnit) {
        this.reviewLikeUnitList.add(reviewLikeUnit);
        reviewLikeUnit.setAuthUser(this);
    }

    public void deleteReviewLikeUnit(ReviewLikeUnit reviewLikeUnit) {
        this.reviewLikeUnitList.remove(reviewLikeUnit);
        reviewLikeUnit.setAuthUser(null);
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

}
