package com.jycforest29.commerce.common.cache.global;

import com.jycforest29.commerce.item.domain.entity.Item;
import com.jycforest29.commerce.item.domain.repository.ItemRepository;
import com.jycforest29.commerce.item.proxy.ItemCacheProxy;
import com.jycforest29.commerce.testcontainers.DockerComposeTestContainer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;
import java.util.stream.IntStream;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.verify;

@SpringBootTest
public class ItemCacheTest extends DockerComposeTestContainer {
    @MockBean
    private ItemRepository itemRepository;

    @Autowired
    private ItemCacheProxy itemCacheProxy;
    Long itemId = 1L;
    Item item = Item.builder()
            .name("name")
            .price(1000)
            .number(1)
            .build();

    @Nested
    class GlobalCacheTest {
        @Nested
        class ItemCache {
            @Test
            void itemId를_통해_item을_가져올때_전역_캐싱을_사용한다() {
                //given
                given(itemRepository.findById(itemId)).willReturn(Optional.ofNullable(item));
                //when
                IntStream.range(0, 10)
                        .forEach(i -> itemCacheProxy.findById(itemId));
                //then
                verify(itemRepository, atMostOnce()).findById(itemId);
            }
        }
    }
}
