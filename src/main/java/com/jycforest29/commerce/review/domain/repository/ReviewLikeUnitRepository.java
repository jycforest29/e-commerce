package com.jycforest29.commerce.review.domain.repository;

import com.jycforest29.commerce.review.domain.entity.ReviewLikeUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ReviewLikeUnitRepository extends JpaRepository<ReviewLikeUnit, Long> {
    @Query("select r from ReviewLikeUnit r where r.authUser.id = :authUserId")
    List<ReviewLikeUnit> findAllByAuthUserId(Long authUserId);

    @Query("select r from ReviewLikeUnit r where r.authUser.id = :authUserId and r.review.id = :reviewId")
    Optional<ReviewLikeUnit> findByAuthUserIdAndReviewId(Long authUserId, Long reviewId);
}
