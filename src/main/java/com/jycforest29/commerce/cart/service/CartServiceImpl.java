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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class CartServiceImpl implements CartService{
    private final CartUnitRepository cartUnitRepository;
    private final AuthUserRepository authUserRepository;
    private final ItemRepository itemRepository;

    // 장바구니에 아이템을 담을 때, 아이템 품절되었으면 담을 수 없음. -> getValidateItemByNumber() 통해 반영함
    // 하지만 만약 A 사용자가 아이템을 장바구니에 담는것과 1ms차로 B 사용자가 아이템의 수량만큼 주문해 품절됐다면?
    // 주문은 락을 걸어서 수행하므로 재고에 문제는 없겠지만 따로 처리하는 로직이 필요할 듯.
    // -> 아이템의 주문 가능 여부를 나타내는 available 필드를 추가해 getCartUnitList()를 호출시 마다 갱신하도록 함.
    @CachePut(value = "cart", key = "#username", cacheManager = "ehCacheManager")
    @Transactional
    @Override
    public CartResponseDto addCartUnitToCart(Long itemId, int number, String username) {
        // 유효성 검증을 통해 검증 후, 엔티티 가져옴
        Cart cart = getAuthUser(username).getCart();
        Item item = getValidateItemByNumber(itemId, number);

        // CartUnit 생성
        // 처음 카트에 담을 때는 항상 주문이 가능한 상태여야 담을 수 있음
        CartUnit cartUnit = CartUnit.builder()
                .item(item)
                .number(number)
                .build();

        // 다대일 양방향 연관관계 매핑
        cart.addCartUnitToCart(cartUnit, item.getPrice());

        // CartUnit 생성(DB에 반영)
        cartUnitRepository.saveAndFlush(cartUnit);
        return CartResponseDto.from(cart);
    }

    // 카트 내 아이템들이 주문 가능한지 확인하는 available 변수의 상태를 바꾸기 위해서는
    // 1. 카트를 가져오는 해당 메서드에서 현재 로그인한 유저의 모든 카트 아이템에 대해 확인하거나(이 방식 사용함)
    // 장점: - 요청이 들어오는 스레드들의 개수만큼 메서드 실행됨. 시간복잡도는 대략 아이템 종류 * 유저 인원수
    //      - 어차피 장바구니에서 주문 페이지로 넘어갈 때 다시 api가 호출되므로 락을 걸 필요는 없을듯.(주문시에는 락으로 확인)
    // 2. order 패키지에서 아이템의 주문이 수행되어 수량이 0이 되면 해당 아이템이 담긴 카트 유닛의 available을 변경 할 수 있음.
    // 장점: - 일종의 pub/sub 방식으로 이해가 직관적임
    // 단점: - cartUnit과 item은 다대일 단방향 매핑으로 설계했으므로 이를 위해서는 다대일 양방향으로 수정 필요.
    //      - cartUnit에 담긴 아이템의 개수가 0일때만 체크가 됨. 0보다 클때도 체크를 하기 위해선 백그라운드로 지속적으로
    //          동작을 수행해야 할 것 같은데 크게 비효율적임. 이를 위해 배치를 사용해도 접근하는 모든 스레드들에 대해
    //          수행되므로 오버헤드가 클 것 같음.
    @Cacheable(value = "cart", key = "#username", cacheManager = "ehCacheManager")
    @Transactional(readOnly = true)
    @Override
    public CartResponseDto getCartUnitList(String username) {
        // 엔티티 가져옴
        Cart cart = getAuthUser(username).getCart();

        cart.getCartUnitList().forEach(s -> {
            s.setAvailable(s.getItem().getNumber() >= s.getNumber() ? true : false);
        });
        return CartResponseDto.from(cart);
    }

    @CacheEvict(value = "cart", key = "#username", cacheManager = "ehCacheManager")
    @Transactional
    @Override
    public CartResponseDto deleteCartAll(String username) {
        // 엔티티 가져옴
        Cart cart = getAuthUser(username).getCart();

        // Cart와 CartUnit의 다대일 양방향 연관관계 제거
        List<Long> cartUnitIdListToDelete = cart.removeAllCartUnit();

        // List<CartUnit> 삭제(DB에 반영)
        cartUnitRepository.deleteAllByCartUnitIdList(cartUnitIdListToDelete);

        return CartResponseDto.from(cart);
    }

    @CachePut(value = "cart", key = "#username", cacheManager = "ehCacheManager")
    @Transactional
    @Override
    public CartResponseDto deleteCartUnit(Long cartUnitId, String username){
        // 엔티티 가져옴
        Cart cart = getAuthUser(username).getCart();
        CartUnit cartUnit = getCartUnit(cartUnitId);

        // CartUnit과 Item은 다대일 단방향 관계이므로 아래와 같이 접근 가능함
        int price = cartUnit.getItem().getPrice();

        // Cart와 CartUnit의 다대일 양방향 연관관계 제거
        cart.removeCartUnitFromCart(cartUnit, price);

        // CartUnit 삭제(DB에 반영)
        cartUnitRepository.deleteById(cartUnitId);
        return CartResponseDto.from(cart);
    }

    private Item getValidateItemByNumber(Long itemId, int number){
        // 유효성 검증을 통해 검증 후, 엔티티 가져옴
        Item item = getItem(itemId);

        if(item.getNumber() >= number){
            return item;
        }
        throw new CustomException(ExceptionCode.ITEM_OVER_LIMIT);
    }

    private Item getItem(Long itemId){
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new CustomException(ExceptionCode.ENTITY_NOT_FOUND));
    }

    private AuthUser getAuthUser(String username){
        return authUserRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(ExceptionCode.UNAUTHORIZED));
    }

    private CartUnit getCartUnit(Long cartUnitId){
        return cartUnitRepository.findById(cartUnitId)
                .orElseThrow(() -> new CustomException(ExceptionCode.ENTITY_NOT_FOUND));
    }
}
