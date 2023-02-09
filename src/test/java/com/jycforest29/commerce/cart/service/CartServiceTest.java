package com.jycforest29.commerce.cart.service;

import com.jycforest29.commerce.cart.domain.dto.CartResponseDto;
import com.jycforest29.commerce.cart.domain.entity.CartUnit;
import com.jycforest29.commerce.cart.domain.repository.CartRepository;
import com.jycforest29.commerce.cart.domain.repository.CartUnitRepository;
import com.jycforest29.commerce.common.exception.CustomException;
import com.jycforest29.commerce.item.domain.entity.Item;
import com.jycforest29.commerce.item.domain.repository.ItemRepository;
import com.jycforest29.commerce.user.domain.entity.AuthUser;
import com.jycforest29.commerce.user.domain.repository.AuthUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
@ExtendWith(MockitoExtension.class)
class CartServiceTest {
    @Mock
    private CartRepository cartRepository;
    @Mock
    private CartUnitRepository cartUnitRepository;
    @Mock
    private AuthUserRepository authUserRepository;
    @Mock
    private ItemRepository itemRepository;
    @InjectMocks
    private CartServiceImpl cartService;
    private Item item;
    private AuthUser authUser;

    @BeforeEach
    void init(){
        item = Item.builder()
                .name("test_item")
                .price(10000)
                .number(1)
                .build();
        item.setId(1L);

        authUser = AuthUser.builder()
                .username("test_username")
                .password("test_password")
                .nickname("test_nickname")
                .build();
        authUser.setId(1L);
    }

    @Test
    void 내가_특정_아이템_1개를_장바구니에_담는다() throws InterruptedException {
        //given
        given(authUserRepository.findById(authUser.getId())).willReturn(Optional.of(authUser));
        given(itemRepository.findById(item.getId())).willReturn(Optional.of(item));
        //when
        CartResponseDto cartResponseDto = cartService.addCartUnitToCart(item.getId(), 1, authUser.getId());
        //then
        assertThat(cartResponseDto.getCartUnit().size()).isEqualTo(1);
        assertThat(cartResponseDto.getTotalPrice()).isEqualTo(item.getPrice());
    }

    @Test
    void 내가_장바구니에_담으려는_아이템의_재고가_부족해_커스텀예외가_발생한다(){
        //given
        given(authUserRepository.findById(authUser.getId())).willReturn(Optional.of(authUser));
        given(itemRepository.findById(item.getId())).willReturn(Optional.of(item));
        //when, then
        assertThatThrownBy(() -> {
            cartService.addCartUnitToCart(item.getId(), 10, authUser.getId());
        }).isInstanceOf(CustomException.class);
    }

    @Test
    void 동시에_100명이_재고가_100개인_아이템을_각자_1개씩_장바구니에_담는다() throws InterruptedException {
        //given
        given(authUserRepository.findById(authUser.getId())).willReturn(Optional.of(authUser));
        given(itemRepository.findById(item.getId())).willReturn(Optional.of(item));
        //when
        int threadCnt = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch countDownLatch = new CountDownLatch(threadCnt);
        for(int i = 0; i < threadCnt; i++){
            executorService.submit(() -> {
                try{
                    cartService.addCartUnitToCart(item.getId(), 1, authUser.getId());
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();
        //then
    }

    @Test
    void 내_장바구니에_담긴_모든_목록을_가져온다(){
        //given
        given(authUserRepository.findById(authUser.getId())).willReturn(Optional.of(authUser));
        //when
        CartResponseDto cartResponseDto = cartService.getCartUnitList(authUser.getId());
        //then
        assertThat(cartResponseDto.getCartUnit().size()).isEqualTo(0);
    }

    @Test
    void 내_장바구니에_담긴_모든_목록을_삭제한다(){
        //given
        given(authUserRepository.findById(authUser.getId())).willReturn(Optional.of(authUser));
        //when
        CartResponseDto cartResponseDto = cartService.deleteCartAll(authUser.getId());
        //then
        assertThat(cartResponseDto.getCartUnit().size()).isEqualTo(0);
        assertThat(cartResponseDto.getTotalPrice()).isEqualTo(0);
    }

    @Test
    void 내_장바구니에_담긴_특정_아이템을_모두_삭제한다() throws InterruptedException {
        //given
        given(authUserRepository.findById(authUser.getId())).willReturn(Optional.of(authUser));
        given(itemRepository.findById(item.getId())).willReturn(Optional.of(item));
        CartUnit cartUnit = cartService.addCartUnitToCart(item.getId(), 1, authUser.getId())
                .getCartUnit().get(0);
        given(cartUnitRepository.findById(cartUnit.getId())).willReturn(Optional.of(cartUnit));
        //when
        CartResponseDto cartResponseDto = cartService.deleteCartUnit(cartUnit.getId(), authUser.getId());
        //then
        assertThat(cartResponseDto.getCartUnit().size()).isEqualTo(0);
        assertThat(cartResponseDto.getTotalPrice()).isEqualTo(0);
    }

    @Test
    void 동시에_100명이_재고가_100개인_아이템을_각자_1개씩_장바구니에서_삭제한다(){

    }
}