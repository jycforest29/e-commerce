package com.jycforest29.commerce.cart.domain.repository;

import com.jycforest29.commerce.cart.domain.entity.CartUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface CartUnitRepository extends JpaRepository<CartUnit, Long> {
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from CartUnit o where o.id in :cartUnitIdList")
    void deleteAllByCartUnitIdList(@Param("cartUnitIdList") List<Long> cartUnitIdList);
}
