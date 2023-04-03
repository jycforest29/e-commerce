package com.jycforest29.commerce.user.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jycforest29.commerce.cart.domain.entity.Cart;
import com.jycforest29.commerce.order.domain.entity.MadeOrder;
import com.jycforest29.commerce.review.domain.entity.Review;
import com.jycforest29.commerce.review.domain.entity.ReviewLikeUnit;
import com.jycforest29.commerce.user.domain.enums.Grade;
import com.jycforest29.commerce.user.domain.enums.Role;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.*;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class AuthUser implements UserDetails {
    /*
    --------------------
    id(pk) : Long
    username : String
    password : String
    role : Role
    nickname : String
    grade : String
    reserves : int
    cart_id : Long
    --------------------
    */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    // 스프링 시큐리티에서 Authorities와 Role은 인가된 사용자들에게 부여된 권한을 표현하는데 사용됨
    // Authorities는 작명 규칙은 따로 존재하지 않음
    // Role은 여러 Authorities를 포함할 수 있고 prefix에 반드시 'ROLE_'이 필요한 작명 규칙을 가짐
    @Column(nullable = false)
    private Role role = Role.USER;

    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> roles = new HashSet<>();
        roles.add(new SimpleGrantedAuthority(role.getValue()));
        return roles;
    }

    @Column(unique = true, nullable = false)
    private String nickname;

    @Enumerated(EnumType.STRING)
    private Grade grade = Grade.ORANGE;

    private int reserves = 0;

    // AuthUser를 주테이블로 한 일대일 단방향.
    // @JoinColumn : name 속성에는 맵핑할 외래 키 이름을 설정해주고 이 어노테이션은 생략 가능함.
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
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
