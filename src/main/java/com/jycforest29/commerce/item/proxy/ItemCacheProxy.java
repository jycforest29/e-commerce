//package com.jycforest29.commerce.item.proxy;
//
//import com.jycforest29.commerce.item.domain.entity.Item;
//import com.jycforest29.commerce.item.domain.repository.ItemRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.cache.annotation.Cacheable;
//import org.springframework.stereotype.Component;
//
//import java.util.Optional;
//
//@Component
//@RequiredArgsConstructor
//public class ItemCacheProxy {
//    private final ItemRepository itemRepository;
//
//    @Cacheable(value = "item", key = "#itemId", cacheManager = "redisCacheManager")
//    public Optional<Item> findById(Long itemId){
//        return itemRepository.findById(itemId);
//    }
//}
