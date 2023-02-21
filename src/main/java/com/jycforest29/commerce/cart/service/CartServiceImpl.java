package com.jycforest29.commerce.cart.service;

import com.jycforest29.commerce.cart.domain.dto.CartResponseDto;
import com.jycforest29.commerce.cart.domain.entity.Cart;
import com.jycforest29.commerce.cart.domain.entity.CartUnit;
import com.jycforest29.commerce.cart.domain.repository.CartUnitRepository;
import com.jycforest29.commerce.common.exception.CustomException;
import com.jycforest29.commerce.common.exception.ExceptionCode;
import com.jycforest29.commerce.item.domain.entity.Item;
import com.jycforest29.commerce.item.domain.repository.ItemRepository;
import com.jycforest29.commerce.user.domain.entity.AuthUser;
import com.jycforest29.commerce.user.domain.repository.AuthUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// @RequiredArgsConstructor : 초기화되지 않은 final 필드나 @NonNull에 대해 생성(@NotNull은 롬복의 어노테이션이 아님)
@RequiredArgsConstructor
@Service
public class CartServiceImpl implements CartService{
    private final CartUnitRepository cartUnitRepository;
    private final AuthUserRepository authUserRepository;
    private final ItemRepository itemRepository;

    // 장바구니에 아이템을 담을 때, 아이템 품절되었으면 담을 수 없음. -> getValidateItemByNumber() 통해 반영함
    // A 사용자가 아이템을 장바구니에 담는것과 1ms차로 B 사용자가 아이템의 수량만큼 주문해 품절됐다면?
        // 일단 담고, order 패키지에서 품절시 pub/sub 방식으로 모든 cart의 해당 item 삭제
        // 즉 여기서 이 클래스에서 더 해줄일은 없음.

    @CachePut(value = "cart", key = "#authUserId", cacheManager = "ehCacheManager")
    @Transactional
    @Override
    public CartResponseDto addCartUnitToCart(Long itemId, int number, Long authUserId) {
        // 유효성 검증을 통해 검증 후, 엔티티 가져옴
        Cart cart = getAuthUser(authUserId).getCart();
        Item item = getValidateItemByNumber(itemId, number);

        // cartUnit 생성
        CartUnit cartUnit = CartUnit.builder()
                .item(item)
                .number(number)
                .build();

        // 다대일 양방향 연관관계 매핑
        cart.addCartUnitToCart(cartUnit, item.getPrice());

        // DB에 반영
        cartUnitRepository.save(cartUnit);
        return CartResponseDto.from(cart);
    }

    @Cacheable(value = "cart", key = "#authUserId", cacheManager = "ehCacheManager")
    @Transactional(readOnly = true)
    @Override
    public CartResponseDto getCartUnitList(Long authUserId) {
        // 유효성 검증을 통해 검증 후, 엔티티 가져옴
        Cart cart = getAuthUser(authUserId).getCart();
        return CartResponseDto.from(cart);
    }

    // 장바구니에서 아이템을 삭제하는 것은 아이템에 아무런 영향을 주지 않음
    @CacheEvict(value = "cart", key = "#authUserId", cacheManager = "ehCacheManager")
    @Transactional
    @Override
    public CartResponseDto deleteCartAll(Long authUserId) {
        // 유효성 검증을 통해 검증 후, 엔티티 가져옴
        Cart cart = getAuthUser(authUserId).getCart();

        // cart와 다대일 양방향 연관관계 제거
        List<CartUnit> cartUnitList = cart.removeAllCartUnit();

        // cartUnit 리스트 삭제 후 DB에 반영
        for (CartUnit cartUnit : cartUnitList){
            cartUnitRepository.deleteById(cartUnit.getId());
        }
        return CartResponseDto.from(cart); // UPDATE
    }

    // 장바구니에서 아이템을 삭제하는 것은 아이템에 아무런 영향을 주지 않음
    @CachePut(value = "cart", key = "#authUserId", cacheManager = "ehCacheManager")
    @Transactional
    @Override
    public CartResponseDto deleteCartUnit(Long cartUnitId, Long authUserId){
        // 유효성 검증을 통해 검증 후, 엔티티 가져옴
        Cart cart = getAuthUser(authUserId).getCart();
        CartUnit cartUnit = cartUnitRepository.findById(cartUnitId)
                .orElseThrow(() -> new CustomException(ExceptionCode.ENTITY_NOT_FOUND));

        // cartUnit과 Item은 다대일 단방향 관계이므로 아래와 같이 접근 가능함
        int price = cartUnit.getItem().getPrice();

        // cart와 다대일 양방향 연관관계 제거
        cart.removeCartUnitFromCart(cartUnit, price);

        // DB에 반영
        cartUnitRepository.deleteById(cartUnitId);
        return CartResponseDto.from(cart);
    }

    @Transactional
    public Item getValidateItemByNumber(Long itemId, int number){
        // 유효성 검증을 통해 검증 후, 엔티티 가져옴
        Item item = getItem(itemId);
        // 수량 검증
        if(item.getNumber() >= number){
            return item;
        }
        throw new CustomException(ExceptionCode.ITEM_OVER_LIMIT);
    }

    // review 패키지와 달리 이 클래스 내부의 메서드가 item 멤버 변수들에 직접적으로 영향을 줌
    // 현재 사용하는 것이 로컬 캐싱인데, 이 경우 메인 서버의 db와의 동기화 속도 차이때문에
    // 품절인 상품이 장바구니에 담길 수 있을 수도 있음.
    // 즉, getValidateItemByNumber()이 원하는 대로 작동하지 않을 수도 있음.
    // 따라서 getValidateItemByNumber() 내부에서 동작하는 getItem()에 캐싱을 적용하지 않음
        // 전역 캐싱으로 전환 시 수정
    @Cacheable(value = "item", key = "#itemId", cacheManager = "redisCacheManager")
    public Item getItem(Long itemId){
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new CustomException(ExceptionCode.ENTITY_NOT_FOUND));
        return item;
    }

    @Cacheable(value = "authUser", key = "#authUserId", cacheManager = "ehCacheManager")
    public AuthUser getAuthUser(Long authUserId){
        AuthUser authUser = authUserRepository.findById(authUserId)
                .orElseThrow(() -> new CustomException(ExceptionCode.UNAUTHORIZED));
        return authUser;
    }
}
