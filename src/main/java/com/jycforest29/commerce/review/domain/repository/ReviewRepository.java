package com.jycforest29.commerce.review.domain.repository;

import com.jycforest29.commerce.item.domain.entity.Item;
import com.jycforest29.commerce.review.domain.entity.Review;
import com.jycforest29.commerce.user.domain.entity.AuthUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
// dao VS repository : dao는 sql 수준에서 db와 맵핑, repository는 객체 수준에서 db와 맵핑.
public interface ReviewRepository extends JpaRepository<Review, Long> {
    @Override
    Optional<Review> findById(@Param("id") Long reviewId);

    List<Review> findAllByItem(@Param("item") Item item);

    @Query("select r from Review r where r.authUser = :authUser and timestampdiff(hour, r.createdAt, :now) <= 48")
    List<Review> findAllByAuthUserAndCreatedWithin48Hours(
            @Param("authUser") AuthUser authUser, @Param("now") LocalDateTime now
    );
}
