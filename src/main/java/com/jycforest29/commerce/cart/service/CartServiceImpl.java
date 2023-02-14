package com.jycforest29.commerce.cart.service;

import com.jycforest29.commerce.cart.domain.dto.CartResponseDto;
import com.jycforest29.commerce.cart.domain.entity.Cart;
import com.jycforest29.commerce.cart.domain.entity.CartUnit;
import com.jycforest29.commerce.cart.domain.repository.CartUnitRepository;
import com.jycforest29.commerce.common.exception.CustomException;
import com.jycforest29.commerce.common.exception.ExceptionCode;
import com.jycforest29.commerce.common.redis.RedisLockRepository;
import com.jycforest29.commerce.item.domain.entity.Item;
import com.jycforest29.commerce.item.domain.repository.ItemRepository;
import com.jycforest29.commerce.user.domain.entity.AuthUser;
import com.jycforest29.commerce.user.domain.repository.AuthUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// @RequiredArgsConstructor : 초기화되지 않은 final 필드나 @NonNull에 대해 생성(@NotNull은 롬복의 어노테이션이 아님)
@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService{
    private final CartUnitRepository cartUnitRepository;
    private final AuthUserRepository authUserRepository;
    private final ItemRepository itemRepository;

    @Transactional
    @Override
    public CartResponseDto addCartUnitToCart(Long itemId, Integer number, Long authUserId) throws InterruptedException {
        Cart cart = getAuthUser(authUserId).getCart();
        Item item = getValidateItemByNumber(itemId, number); // number 까지 확인함.
        CartUnit cartUnit = CartUnit.builder()
                .cart(cart)
                .item(item)
                .number(number)
                .build();
        cart.addCartUnitToCart(cartUnit, item.getPrice());
        return CartResponseDto.from(cart); // UPDATE
    }

    @Transactional(readOnly = true)
    @Override
    public CartResponseDto getCartUnitList(Long authUserId) {
        return CartResponseDto.from(getAuthUser(authUserId).getCart());
    }

    @Transactional
    @Override
    public CartResponseDto deleteCartAll(Long authUserId) {
        Cart cart = getAuthUser(authUserId).getCart();
        cart.removeAllCartUnit();
        return CartResponseDto.from(cart); // UPDATE
    }

    @Transactional
    @Override
    public CartResponseDto deleteCartUnit(Long cartUnitId, Long authUserId) throws InterruptedException {
        Cart cart = getAuthUser(authUserId).getCart();
        CartUnit cartUnit = cartUnitRepository.findById(cartUnitId)
                .orElseThrow(() -> new CustomException(ExceptionCode.ENTITY_NOT_FOUND));
        Long itemId = cartUnit.getItem().getId();
        cart.removeCartUnitFromCart(cartUnit, cartUnit.getItem().getPrice());
        return CartResponseDto.from(cart);
    }

    // item 존재 여부와 수량 한번에 확인
    @Transactional
    public Item getValidateItemByNumber(Long itemId, Integer number){
        Item item = getItem(itemId);
        if(item.getNumber() >= number){
            return item;
        }
        throw new CustomException(ExceptionCode.ITEM_OVER_LIMIT);
    }

    public Item getItem(Long itemId){
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new CustomException(ExceptionCode.ENTITY_NOT_FOUND));
        return item;
    }

    public AuthUser getAuthUser(Long authUserId){
        AuthUser authUser = authUserRepository.findById(authUserId)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND));
        return authUser;
    }
}
