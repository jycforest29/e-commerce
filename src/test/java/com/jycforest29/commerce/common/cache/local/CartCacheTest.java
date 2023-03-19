//package com.jycforest29.commerce.common.cache.local;
//
//import com.jycforest29.commerce.cart.domain.dto.CartResponseDto;
//import com.jycforest29.commerce.cart.service.CartServiceImpl;
//import com.jycforest29.commerce.testcontainers.DockerComposeTestContainer;
//import com.jycforest29.commerce.user.domain.entity.AuthUser;
//import com.jycforest29.commerce.user.domain.repository.AuthUserRepository;
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
//    private AuthUserRepository authUserRepository;
//    @Autowired
//    private CartServiceImpl cartService;
//
//    AuthUser authUser = AuthUser.builder()
//            .username("test_username")
//            .password("test_password")
//            .nickname("test_nickname")
//            .build();
//
//    @Nested
//    class LocalCacheTest{
//        @Test
//        void username을_통해_CartResponseDto객체를_가져올때_로컬_캐싱을_사용한다(){
//            //given
//            given(authUserRepository.findByUsername(authUser.getUsername()))
//                    .willReturn(Optional.ofNullable(authUser));
//            //when
//            IntStream.range(0, 10)
//                    .forEach(i -> cartService.getCartUnitList(authUser.getUsername()));
//            //then
//            verify(CartResponseDto, atMostOnce()).from(cart);
//        }
//    }
//}
