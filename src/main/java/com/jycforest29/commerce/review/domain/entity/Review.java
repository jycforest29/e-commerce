package com.jycforest29.commerce.review.domain.entity;

import com.jycforest29.commerce.item.domain.entity.Item;
import com.jycforest29.commerce.review.dto.AddReviewRequestDto;
import com.jycforest29.commerce.user.domain.entity.AuthUser;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@EntityListeners(value = {AuditingEntityListener.class})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
public class Review{
    /*
    --------------------
    id(pk) : Long
    title : String
    contents : int
    created_at : LocalDateTime
    updated_at : LocalDateTime
    item_id(fk) : Long
    authUser_id(fk) : Long
    --------------------
    */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String contents;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "authUser_id")
    private AuthUser authUser;

    @OneToMany(mappedBy = "review")
    private List<ReviewLikeUnit> reviewLikeUnitList = new ArrayList<>();

    @Builder
    public Review(String title, String contents){
        this.title = title;
        this.contents = contents;
    }

    public static Review from(AddReviewRequestDto addReviewRequestDTO){
        return Review.builder()
                .title(addReviewRequestDTO.getTitle())
                .contents(addReviewRequestDTO.getContents())
                .build();
    }

    public void update(AddReviewRequestDto addReviewRequestDTO){
        this.title = addReviewRequestDTO.getTitle();
        this.contents = addReviewRequestDTO.getContents();
    }

    public void addReviewLikeUnit(ReviewLikeUnit reviewLikeUnit) {
        this.reviewLikeUnitList.add(reviewLikeUnit);
        reviewLikeUnit.setReview(this);
    }

    public void deleteReviewLikeUnit(ReviewLikeUnit reviewLikeUnit) {
        this.reviewLikeUnitList.remove(reviewLikeUnit);
        reviewLikeUnit.setReview(null);
    }

    public void deleteAllReviewLikeUnit() {
        for(ReviewLikeUnit reviewLikeUnit : this.reviewLikeUnitList){
            reviewLikeUnit.setReview(null);
        }
        this.reviewLikeUnitList = new ArrayList<>();
    }
}
