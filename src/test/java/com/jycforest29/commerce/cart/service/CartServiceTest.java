//package com.jycforest29.commerce.cart.service;
//
//import com.jycforest29.commerce.cart.domain.dto.CartResponseDto;
//import com.jycforest29.commerce.cart.domain.entity.CartUnit;
//import com.jycforest29.commerce.cart.domain.repository.CartUnitRepository;
//import com.jycforest29.commerce.common.exception.CustomException;
//import com.jycforest29.commerce.item.domain.entity.Item;
//import com.jycforest29.commerce.item.domain.repository.ItemRepository;
//import com.jycforest29.commerce.user.domain.repository.AuthUserRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Nested;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.assertj.core.api.Assertions.assertThatThrownBy;
//import static org.mockito.BDDMockito.given;
//
//
//class CartServiceTest {
//    @Mock
//    private CartUnitRepository cartUnitRepository;
//    @Mock
//    private AuthUserRepository authUserRepository;
//    @Mock
//    private ItemRepository itemRepository;
//    @InjectMocks
//    private CartServiceImpl cartService;
//
//    @Nested
//    class AddToCart{
//        private Item item;
//        private Long itemId;
//        @BeforeEach
//        void init(){
//            item = Item.builder()
//                    .name("test_item")
//                    .price(10000)
//                    .number(100)
//                    .build();
//            itemId = 1L;
//        }
//
//        @Test
//        void 내가_특정_아이템_1개를_장바구니에_담는다() {
//            //given
//            given(authUserRepository.findById(authUserId)).willReturn(Optional.of(authUser));
//            given(itemRepository.findById(itemId)).willReturn(Optional.of(item));
//            //when
//            CartResponseDto cartResponseDto = cartService.addCartUnitToCart(itemId, 1, authUserId);
//            //then
//            assertThat(cartResponseDto.getCartUnitResponseDtoList().size()).isEqualTo(1);
//            assertThat(cartResponseDto.getTotalPrice()).isEqualTo(item.getPrice());
//        }
//
//        @Test
//        void 내가_장바구니에_담으려는_아이템의_재고가_부족해_커스텀예외가_발생한다(){
//            //given
//            given(authUserRepository.findById(authUserId)).willReturn(Optional.of(authUser));
//            given(itemRepository.findById(itemId)).willReturn(Optional.of(item));
//            //when, then
//            assertThatThrownBy(() -> {
//                cartService.addCartUnitToCart(itemId, 1000, authUserId);
//            }).isInstanceOf(CustomException.class);
//        }
//    }
//
//    @Nested
//    class getFromCart{
//        @Test
//        void 내_장바구니에_담긴_모든_목록을_가져온다(){
//            //given
//            given(authUserRepository.findById(authUserId)).willReturn(Optional.of(authUser));
//            //when
//            CartResponseDto cartResponseDto = cartService.getCartUnitList(authUserId);
//            //then
//            assertThat(cartResponseDto.getCartUnitResponseDtoList().size()).isEqualTo(0);
//        }
//    }
//
//    @Nested
//    class deleteFromCart{
//        private Item item;
//        private Long itemId;
//        @BeforeEach
//        void init(){
//            item = Item.builder()
//                    .name("test_item")
//                    .price(10000)
//                    .number(100)
//                    .build();
//            itemId = 1L;
//        }
//        @Test
//        void 내_장바구니에_담긴_모든_목록을_삭제한다(){
//            //given
//            given(authUserRepository.findById(authUserId)).willReturn(Optional.of(authUser));
//            given(itemRepository.findById(itemId)).willReturn(Optional.of(item));
//            //given-주문하기
//            cartService.addCartUnitToCart(itemId, 1, authUserId);
//            //when
//            CartResponseDto cartResponseDto = cartService.deleteCartAll(authUserId);
//            //then
//            assertThat(cartResponseDto.getCartUnitResponseDtoList().size()).isEqualTo(0);
//            assertThat(cartResponseDto.getTotalPrice()).isEqualTo(0);
//        }
//
//        @Test
//        void 내_장바구니에_담긴_특정_아이템을_모두_삭제한다(){
//            //given
//            given(authUserRepository.findById(authUserId)).willReturn(Optional.of(authUser));
//            given(itemRepository.findById(itemId)).willReturn(Optional.of(item));
//            //given-주문하기
//            cartService.addCartUnitToCart(itemId, 1, authUserId);
//            Long cartUnitId = 1L;
//            given(cartUnitRepository.findById(cartUnitId)).willReturn(Optional.of(
//                    CartUnit.builder()
//                            .item(item)
//                            .number(1)
//                            .build()
//            ));
//            //when
//            CartResponseDto cartResponseDto = cartService.deleteCartUnit(cartUnitId, authUserId);
//            //then
//            assertThat(cartResponseDto.getCartUnitResponseDtoList().size()).isEqualTo(0);
//            assertThat(cartResponseDto.getTotalPrice()).isEqualTo(0);
//        }
//    }
//}