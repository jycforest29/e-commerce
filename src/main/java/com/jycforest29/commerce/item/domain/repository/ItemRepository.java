package com.jycforest29.commerce.item.domain.repository;

import com.jycforest29.commerce.item.domain.entity.Item;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    @Cacheable(value = "item", key = "#itemId", cacheManager = "redisCacheManager")
    @Override
    Optional<Item> findById(@Param("id") Long id);

    Optional<Item> findByName(@Param("name") String name);
}
