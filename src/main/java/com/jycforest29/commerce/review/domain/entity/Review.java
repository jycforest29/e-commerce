package com.jycforest29.commerce.review.domain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jycforest29.commerce.item.domain.entity.Item;
import com.jycforest29.commerce.review.domain.dto.AddReviewRequestDTO;
import com.jycforest29.commerce.user.domain.entity.AuthUser;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Entity
public class Review {
    @Id
    @Setter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Lob
    @Column(nullable = false)
    private String contents;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "authUser_id")
    private AuthUser authUser;

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReviewLikeUnit> reviewLikeUnitList = new ArrayList<>();

    @Builder
    public Review(String title, String contents, Item item, AuthUser authUser){
        this.title = title;
        this.contents = contents;
        this.item = item;
        this.authUser = authUser;
    }

    public void update(AddReviewRequestDTO addReviewRequestDTO){
        this.title = addReviewRequestDTO.getTitle();
        this.contents = addReviewRequestDTO.getContents();
    }
    public static Review of(AddReviewRequestDTO addReviewRequestDTO, Item item, AuthUser authUser){
        return Review.builder()
                .title(addReviewRequestDTO.getTitle())
                .contents(addReviewRequestDTO.getContents())
                .item(item)
                .authUser(authUser)
                .build();
    }

    public void addReviewLikeUnit(ReviewLikeUnit reviewLikeUnit) {
        this.reviewLikeUnitList.add(reviewLikeUnit);
    }

    public void deleteReviewLikeUnit(ReviewLikeUnit reviewLikeUnit) {
        this.reviewLikeUnitList.remove(reviewLikeUnit);
    }
}
