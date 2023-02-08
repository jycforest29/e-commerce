package com.jycforest29.commerce.review.domain.entity;

import com.jycforest29.commerce.user.domain.entity.AuthUser;
import lombok.*;

import javax.persistence.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
public class ReviewLikeUnit {
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    private Review review;

    // Review의 AuthUser와 달라야 함.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "authUser_id")
    private AuthUser authUser;

    @Builder
    public ReviewLikeUnit(Review review, AuthUser authUser){
        this.review = review;
        this.authUser = authUser;
    }
}
