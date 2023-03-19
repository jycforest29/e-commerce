package com.jycforest29.commerce.review.domain.repository;

import com.jycforest29.commerce.review.domain.entity.Review;
import com.jycforest29.commerce.review.domain.entity.ReviewLikeUnit;
import com.jycforest29.commerce.user.domain.entity.AuthUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewLikeUnitRepository extends JpaRepository<ReviewLikeUnit, Long> {
    List<ReviewLikeUnit> findAllByAuthUser(@Param("authUser") AuthUser authUser);
    Optional<ReviewLikeUnit> findByReviewAndAuthUser(@Param("review") Review review,
                                                     @Param("authUser") AuthUser authUser);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from ReviewLikeUnit r where r.review.id = :review_id")
    void deleteAllByReviewId(@Param("review_id") Long review_id);

    List<ReviewLikeUnit> findAllByReview(@Param("review") Review review);
}
