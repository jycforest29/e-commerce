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
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// @RequiredArgsConstructor : 초기화되지 않은 final 필드나 @NonNull에 대해 생성(@NotNull은 롬복의 어노테이션이 아님)
@Slf4j
@RequiredArgsConstructor
@Service
public class CartServiceImpl implements CartService{
    Logger logger = LoggerFactory.getLogger(CartServiceImpl.class);
    private final CartUnitRepository cartUnitRepository;
    private final AuthUserRepository authUserRepository;
    private final ItemRepository itemRepository;

    // 장바구니에 아이템을 담을 때, 아이템 품절되었으면 담을 수 없음. -> getValidateItemByNumber() 통해 반영함
    // A 사용자가 아이템을 장바구니에 담는것과 1ms차로 B 사용자가 아이템의 수량만큼 주문해 품절됐다면?
        // 일단 담고, order 패키지에서 품절시 pub/sub 방식으로 모든 cart의 해당 item 삭제
        // 즉 여기서 이 클래스에서 더 해줄일은 없음.

//    @CachePut(value = "cart", key = "#authUserId", cacheManager = "ehCacheManager")
    @Transactional
    @Override
    public CartResponseDto addCartUnitToCart(Long itemId, int number, Long authUserId) {
        // 유효성 검증을 통해 검증 후, 엔티티 가져옴
        Cart cart = getAuthUser(authUserId).getCart();
        Item item = getValidateItemByNumber(itemId, number);

        // cartUnit 생성
        // 처음 카트에 담을 때는 항상 주문이 가능한 상태여야 담을 수 있음
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

//    @Cacheable(value = "cart", key = "#authUserId", cacheManager = "ehCacheManager")
    @Transactional(readOnly = true)
    @Override
    public CartResponseDto getCartUnitList(Long authUserId) {
        // 유효성 검증을 통해 검증 후, 엔티티 가져옴
        Cart cart = getAuthUser(authUserId).getCart();

        // 카트 내 아이템들이 주문 가능한지 확인하는 available 변수의 상태를 바꾸기 위해서는
            // 카트를 가져오는 해당 메서드에서 현재 로그인한 유저의 모든 카트 아이템에 대해 확인하거나(이 방식 사용함)
                // 장점: - 요청이 들어오는 스레드들의 개수만큼 메서드 실행됨. 시간복잡도는 대략 아이템 종류 * 유저 인원수
                //      - 어차피 장바구니에서 주문 페이지로 넘어갈 때 다시 api가 호출되므로 락을 걸 필요는 없을듯.(주문시에는 락으로 확인)
            // order 패키지에서 아이템의 주문이 수행되어 수량이 0이 되면 해당 아이템이 담긴 카트 유닛의 available을 변경 할 수 있음.
                // 장점: - 일종의 pub/sub 방식으로 이해가 직관적임
                // 단점: - cartUnit과 item은 다대일 단방향 매핑으로 설계했으므로 이를 위해서는 다대일 양방향으로 수정 필요.
                //      - cartUnit에 담긴 아이템의 개수가 0일때만 체크가 됨. 0보다 클때도 체크를 하기 위해선 백그라운드로 지속적으로
                //          동작을 수행해야 할 것 같은데 크게 비효율적임. 이를 위해 배치를 사용해도 접근하는 모든 스레드들에 대해
                //          수행되므로 오버헤드가 클 것 같음.

        // stream의 forEach는 thread-safe 하지 않으므로 내부에서 객체를 다루지 않음.
        // 따라서 일반 forLoop 사용함
        cart.getCartUnitList().forEach(s -> {
            s.setAvailable(s.getItem().getNumber() > s.getNumber() ? true : false);
        });
        return CartResponseDto.from(cart);
    }

    // 장바구니에서 아이템을 삭제하는 것은 아이템에 아무런 영향을 주지 않음
//    @CacheEvict(value = "cart", key = "#authUserId", cacheManager = "ehCacheManager")
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
//    @CachePut(value = "cart", key = "#authUserId", cacheManager = "ehCacheManager")
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
