package com.jycforest29.commerce.cart.service;

import com.jycforest29.commerce.cart.domain.dto.CartResponseDto;
import com.jycforest29.commerce.cart.domain.entity.CartUnit;
import com.jycforest29.commerce.cart.domain.repository.CartUnitRepository;
import com.jycforest29.commerce.common.exception.CustomException;
import com.jycforest29.commerce.item.domain.entity.Item;
import com.jycforest29.commerce.item.domain.repository.ItemRepository;
import com.jycforest29.commerce.user.domain.entity.AuthUser;
import com.jycforest29.commerce.user.domain.repository.AuthUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CartServiceTest{
    @Mock
    private CartUnitRepository cartUnitRepository;
    @Mock
    private AuthUserRepository authUserRepository;
    @Mock
    private ItemRepository itemRepository;
    @InjectMocks
    private CartServiceImpl cartService;
    private AuthUser authUser;
    private Long authUserId;

    @BeforeEach
    void init(){
        authUser = AuthUser.builder()
                .username("test_username")
                .password("test_password")
                .nickname("test_nickname")
                .build();
        authUserId = 1L;
    }

    @Nested
    class AddToCart{
        Item item;
        Long itemId;
        @BeforeEach
        void init(){
            item = Item.builder()
                    .name("test_item")
                    .price(10000)
                    .number(100)
                    .build();
            itemId = 1L;
        }

        @Test
        void 내가_특정_아이템_1개를_장바구니에_담는다() {
            //given
            given(authUserRepository.findByUsername(authUser.getUsername())).willReturn(Optional.of(authUser));
            given(itemRepository.findById(itemId)).willReturn(Optional.of(item));
            //when
            CartResponseDto cartResponseDto = cartService.addCartUnitToCart(itemId, 1, authUser.getUsername());
            //then
            assertThat(cartResponseDto.getCartUnitResponseDtoList().size()).isEqualTo(1);
            assertThat(cartResponseDto.getTotalPrice()).isEqualTo(item.getPrice());
        }

        @Test
        void 내가_장바구니에_담으려는_아이템의_재고가_부족해_커스텀예외가_발생한다(){
            //given
            given(authUserRepository.findByUsername(authUser.getUsername())).willReturn(Optional.of(authUser));
            given(itemRepository.findById(itemId)).willReturn(Optional.of(item));
            //when, then
            assertThatThrownBy(() -> {
                cartService.addCartUnitToCart(itemId, 1000, authUser.getUsername());
            }).isInstanceOf(CustomException.class);
        }
    }

    @Nested
    class GetFromCart{
        Item item;
        Long itemId;
        CartUnit cartUnit;
        Long cartUnitId;

        @BeforeEach
        void init(){
            item = Item.builder()
                    .name("test_item")
                    .price(10000)
                    .number(1)
                    .build();
            itemId = 1L;
            cartUnitId = 1L;
        }

        @Nested
        class OrderAvailable{
            @BeforeEach
            void init(){
                cartUnit = CartUnit.builder()
                        .item(item)
                        .number(1)
                        .build();
            }
            @Test
            void 내_장바구니에_담긴_모든_목록을_가져온다(){
                //given
                given(authUserRepository.findByUsername(authUser.getUsername())).willReturn(Optional.of(authUser));
                authUser.getCart().addCartUnitToCart(cartUnit, item.getPrice());
                //when
                CartResponseDto cartResponseDto = cartService.getCartUnitList(authUser.getUsername());
                //then
                assertThat(cartResponseDto.getCartUnitResponseDtoList().size()).isEqualTo(1);
                assertThat(cartResponseDto.getCartUnitResponseDtoList().get(0).getAvailable()).isEqualTo(true);
            }
        }


        @Nested
        class OrderNotAvailable{
            @BeforeEach
            void init(){
                cartUnit = CartUnit.builder()
                        .item(item)
                        .number(2)
                        .build();
            }
            @Test
            void 내_장바구니에_담긴_모든_목록을_가져온다(){
                //given
                given(authUserRepository.findByUsername(authUser.getUsername())).willReturn(Optional.of(authUser));
                authUser.getCart().addCartUnitToCart(cartUnit, item.getPrice());
                //when
                CartResponseDto cartResponseDto = cartService.getCartUnitList(authUser.getUsername());
                //then
                assertThat(cartResponseDto.getCartUnitResponseDtoList().size()).isEqualTo(1);
                assertThat(cartResponseDto.getCartUnitResponseDtoList().get(0).getAvailable()).isEqualTo(false);
            }
        }
    }

    @Nested
    class DeleteFromCart{
        Item item;
        Long itemId;
        CartUnit cartUnit;
        Long cartUnitId;
        @BeforeEach
        void init(){
            item = Item.builder()
                    .name("test_item")
                    .price(10000)
                    .number(100)
                    .build();
            itemId = 1L;
            cartUnit  = CartUnit.builder()
                    .item(item)
                    .number(1)
                    .build();
            cartUnitId = 1L;
        }

        @Test
        void 내_장바구니에_담긴_모든_목록을_삭제한다(){
            //given
            given(authUserRepository.findByUsername(authUser.getUsername())).willReturn(Optional.of(authUser));
            authUser.getCart().addCartUnitToCart(cartUnit, item.getPrice());
            //when
            CartResponseDto cartResponseDto = cartService.deleteCartAll(authUser.getUsername());
            //then
            assertThat(cartResponseDto.getCartUnitResponseDtoList().size()).isEqualTo(0);
            assertThat(cartResponseDto.getTotalPrice()).isEqualTo(0);
        }

        @Test
        void 내_장바구니에_담긴_특정_아이템을_모두_삭제한다(){
            //given
            given(authUserRepository.findByUsername(authUser.getUsername())).willReturn(Optional.of(authUser));
            authUser.getCart().addCartUnitToCart(cartUnit, item.getPrice());
            given(cartUnitRepository.findById(cartUnitId)).willReturn(Optional.of(cartUnit));
            //when
            CartResponseDto cartResponseDto = cartService.deleteCartUnit(cartUnitId, authUser.getUsername());
            //then
            assertThat(cartResponseDto.getCartUnitResponseDtoList().size()).isEqualTo(0);
            assertThat(cartResponseDto.getTotalPrice()).isEqualTo(0);
        }
    }
}