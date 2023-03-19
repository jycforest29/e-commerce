package com.jycforest29.commerce.review.domain.entity;

import com.jycforest29.commerce.user.domain.entity.AuthUser;
import lombok.*;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
public class ReviewLikeUnit {
    /*
    --------------------
    id(pk) : Long
    review_id : Long
    authUser_id : Long
    --------------------
    */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    private Review review;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "authUser_id")
    private AuthUser authUser;
}
