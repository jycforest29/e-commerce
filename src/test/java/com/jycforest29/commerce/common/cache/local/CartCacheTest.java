//package com.jycforest29.commerce.common.cache.local;
//
//import com.jycforest29.commerce.cart.controller.CartController;
//import com.jycforest29.commerce.cart.domain.dto.CartResponseDto;
//import com.jycforest29.commerce.cart.domain.repository.CartUnitRepository;
//import com.jycforest29.commerce.cart.service.CartServiceImpl;
//import com.jycforest29.commerce.item.domain.entity.Item;
//import com.jycforest29.commerce.item.domain.repository.ItemRepository;
//import com.jycforest29.commerce.testcontainers.DockerComposeTestContainer;
//import com.jycforest29.commerce.user.domain.entity.AuthUser;
//import com.jycforest29.commerce.user.domain.repository.AuthUserRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Nested;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//
//import java.util.Optional;
//import java.util.stream.IntStream;
//
//import static org.mockito.BDDMockito.given;
//import static org.mockito.Mockito.atMostOnce;
//import static org.mockito.Mockito.verify;
//
//@SpringBootTest
//public class CartCacheTest extends DockerComposeTestContainer {
//    @MockBean
//    private CartServiceImpl cartService;
//    @Autowired
//    private CartController cartController;
//
//    @Nested
//    class LocalCacheTest{
//        @Test
//        void username을_통해_cart을_가져올때_로컬_캐싱을_사용한다(){
//            //given
//            given(authUserRepository.findByUsername(authUser.getUsername()))
//                    .willReturn(Optional.ofNullable(authUser));
//            given(itemRepository.findById(itemId)).willReturn(Optional.ofNullable(item));
//            given(cartService.addCartUnitToCart(itemId, 1, authUser.getUsername()))
//                    .willReturn(cartResponseDto);
//            //when
//            IntStream.range(0, 10)
//                    .forEach(i -> cartController.getCartUnitList(authUser.getUsername());
//            //then
//            verify(CartResponseDto, atMostOnce()).findById(itemId);
//        }
//    }
//}
