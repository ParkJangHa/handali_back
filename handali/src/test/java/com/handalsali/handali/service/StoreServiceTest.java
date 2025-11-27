package com.handalsali.handali.service;

import com.handalsali.handali.DTO.StoreDTO;
import com.handalsali.handali.domain.Store;
import com.handalsali.handali.domain.User;
import com.handalsali.handali.domain.UserStore;
import com.handalsali.handali.enums.ItemType;
import com.handalsali.handali.exception.InsufficientCoinException;
import com.handalsali.handali.exception.StoreItemAlreadyBoughtException;
import com.handalsali.handali.exception.StoreItemNotExistsException;
import com.handalsali.handali.repository.StoreRepository;
import com.handalsali.handali.repository.UserRepository;
import com.handalsali.handali.repository.UserStoreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("StoreService 테스트")
public class StoreServiceTest {

    @InjectMocks
    private StoreService storeService;

    @Mock
    private UserService userService;

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserStoreRepository userStoreRepository;

    private User user;
    private Store store1;
    private Store store2;
    private Store store3;
    private String token;

    @BeforeEach
    void setUp() {
        token = "test-token-12345";
        user = new User("test@gmail.com", "테스트유저", "password123", "010-1234-5678", LocalDate.now());
        user.setTotal_coin(1000);

        // 배경 아이템
        store1 = new Store();
        store1.setItemType(ItemType.BACKGROUND);
        store1.setName("숲 배경");
        store1.setPrice(100);

        store2 = new Store();
        store2.setItemType(ItemType.BACKGROUND);
        store2.setName("바다 배경");
        store2.setPrice(200);

        // 소파 아이템
        store3 = new Store();
        store3.setItemType(ItemType.SOFA);
        store3.setName("가죽 소파");
        store3.setPrice(300);
    }

    /**
     * storeViewByCategory 테스트
     */
    @Test
    @DisplayName("카테고리별 상점 물품 조회 - 구매한 아이템 포함")
    public void testStoreViewByCategory_WithPurchasedItems() {
        // given
        List<Store> backgroundStores = List.of(store1, store2);
        UserStore userStore1 = new UserStore(user, store1); // 이미 구매한 아이템

        when(userService.tokenToUser(token)).thenReturn(user);
        when(storeRepository.findByItemType(ItemType.BACKGROUND)).thenReturn(backgroundStores);
        when(userStoreRepository.findByUserAndStore(user, store1)).thenReturn(userStore1);
        when(userStoreRepository.findByUserAndStore(user, store2)).thenReturn(null);

        // when
        List<StoreDTO.StoreViewResponse> responses = storeService.storeViewByCategory(token, ItemType.BACKGROUND);

        // then
        assertEquals(2, responses.size());

        // 첫 번째 아이템 검증 (구매한 아이템)
        StoreDTO.StoreViewResponse response1 = responses.get(0);
        assertEquals("숲 배경", response1.getName());
        assertEquals(100, response1.getPrice());
        assertTrue(response1.isBuy(), "구매한 아이템은 is_buy가 true여야 함");

        // 두 번째 아이템 검증 (구매하지 않은 아이템)
        StoreDTO.StoreViewResponse response2 = responses.get(1);
        assertEquals("바다 배경", response2.getName());
        assertEquals(200, response2.getPrice());
        assertFalse(response2.isBuy(), "구매하지 않은 아이템은 is_buy가 false여야 함");

        verify(userService, times(1)).tokenToUser(token);
        verify(storeRepository, times(1)).findByItemType(ItemType.BACKGROUND);
        verify(userStoreRepository, times(2)).findByUserAndStore(eq(user), any(Store.class));
    }

    @Test
    @DisplayName("카테고리별 상점 물품 조회 - 모두 미구매")
    public void testStoreViewByCategory_AllNotPurchased() {
        // given
        List<Store> sofaStores = List.of(store3);

        when(userService.tokenToUser(token)).thenReturn(user);
        when(storeRepository.findByItemType(ItemType.SOFA)).thenReturn(sofaStores);
        when(userStoreRepository.findByUserAndStore(user, store3)).thenReturn(null);

        // when
        List<StoreDTO.StoreViewResponse> responses = storeService.storeViewByCategory(token, ItemType.SOFA);

        // then
        assertEquals(1, responses.size());

        StoreDTO.StoreViewResponse response = responses.get(0);
        assertEquals("가죽 소파", response.getName());
        assertEquals(300, response.getPrice());
        assertFalse(response.isBuy(), "구매하지 않은 아이템은 is_buy가 false여야 함");
    }

    @Test
    @DisplayName("카테고리별 상점 물품 조회 - 모두 구매함")
    public void testStoreViewByCategory_AllPurchased() {
        // given
        List<Store> backgroundStores = List.of(store1, store2);
        UserStore userStore1 = new UserStore(user, store1);
        UserStore userStore2 = new UserStore(user, store2);

        when(userService.tokenToUser(token)).thenReturn(user);
        when(storeRepository.findByItemType(ItemType.BACKGROUND)).thenReturn(backgroundStores);
        when(userStoreRepository.findByUserAndStore(user, store1)).thenReturn(userStore1);
        when(userStoreRepository.findByUserAndStore(user, store2)).thenReturn(userStore2);

        // when
        List<StoreDTO.StoreViewResponse> responses = storeService.storeViewByCategory(token, ItemType.BACKGROUND);

        // then
        assertEquals(2, responses.size());
        assertTrue(responses.get(0).isBuy(), "모든 아이템이 구매됨");
        assertTrue(responses.get(1).isBuy(), "모든 아이템이 구매됨");
    }

    @Test
    @DisplayName("카테고리별 상점 물품 조회 - 해당 카테고리 물품 없음 예외")
    public void testStoreViewByCategory_NoItemsInCategory() {
        // given
        when(userService.tokenToUser(token)).thenReturn(user);
        when(storeRepository.findByItemType(ItemType.BACKGROUND)).thenReturn(List.of());

        // when & then
        assertThrows(StoreItemNotExistsException.class, () -> {
            storeService.storeViewByCategory(token, ItemType.BACKGROUND);
        });

        verify(userService, times(1)).tokenToUser(token);
        verify(storeRepository, times(1)).findByItemType(ItemType.BACKGROUND);
    }

    /**
     * storeBuyItem 테스트
     */
    @Test
    @DisplayName("상점 물품 구매 - 정상 구매")
    public void testStoreBuyItem_Success() {
        // given
        StoreDTO.StoreBuyRequest request = new StoreDTO.StoreBuyRequest();
        request.setItem_type(ItemType.BACKGROUND);
        request.setName("숲 배경");

        when(userService.tokenToUser(token)).thenReturn(user);
        when(storeRepository.findByItemTypeAndName(ItemType.BACKGROUND, "숲 배경"))
                .thenReturn(Optional.of(store1));
        when(userStoreRepository.findByUserAndStore(user, store1)).thenReturn(null);

        int initialCoin = user.getTotal_coin();

        // when
        String result = storeService.storeBuyItem(token, request);

        // then
        assertEquals("아이템을 구매했습니다.", result);
        assertEquals(initialCoin - store1.getPrice(), user.getTotal_coin(), "코인이 차감되어야 함");

        ArgumentCaptor<UserStore> userStoreCaptor = ArgumentCaptor.forClass(UserStore.class);
        verify(userStoreRepository, times(1)).save(userStoreCaptor.capture());

        UserStore savedUserStore = userStoreCaptor.getValue();
        assertEquals(user, savedUserStore.getUser());
        assertEquals(store1, savedUserStore.getStore());

        verify(userRepository, times(1)).save(user);
    }

    @Test
    @DisplayName("상점 물품 구매 - 여러 아이템 연속 구매")
    public void testStoreBuyItem_MultiplePurchases() {
        // given
        StoreDTO.StoreBuyRequest request1 = new StoreDTO.StoreBuyRequest();
        request1.setItem_type(ItemType.BACKGROUND);
        request1.setName("숲 배경");

        StoreDTO.StoreBuyRequest request2 = new StoreDTO.StoreBuyRequest();
        request2.setItem_type(ItemType.BACKGROUND);
        request2.setName("바다 배경");

        when(userService.tokenToUser(token)).thenReturn(user);

        // 첫 번째 구매
        when(storeRepository.findByItemTypeAndName(ItemType.BACKGROUND, "숲 배경"))
                .thenReturn(Optional.of(store1));
        when(userStoreRepository.findByUserAndStore(user, store1)).thenReturn(null);

        // 두 번째 구매
        when(storeRepository.findByItemTypeAndName(ItemType.BACKGROUND, "바다 배경"))
                .thenReturn(Optional.of(store2));
        when(userStoreRepository.findByUserAndStore(user, store2)).thenReturn(null);

        int initialCoin = user.getTotal_coin();

        // when
        String result1 = storeService.storeBuyItem(token, request1);
        String result2 = storeService.storeBuyItem(token, request2);

        // then
        assertEquals("아이템을 구매했습니다.", result1);
        assertEquals("아이템을 구매했습니다.", result2);
        assertEquals(initialCoin - store1.getPrice() - store2.getPrice(), user.getTotal_coin(),
                "두 아이템의 가격이 모두 차감되어야 함");

        verify(userStoreRepository, times(2)).save(any(UserStore.class));
        verify(userRepository, times(2)).save(user);
    }

    @Test
    @DisplayName("상점 물품 구매 - 상품이 존재하지 않음 예외")
    public void testStoreBuyItem_ItemNotExists() {
        // given
        StoreDTO.StoreBuyRequest request = new StoreDTO.StoreBuyRequest();
        request.setItem_type(ItemType.BACKGROUND);
        request.setName("존재하지않는배경");

        when(userService.tokenToUser(token)).thenReturn(user);
        when(storeRepository.findByItemTypeAndName(ItemType.BACKGROUND, "존재하지않는배경"))
                .thenReturn(Optional.empty());

        // when & then
        assertThrows(StoreItemNotExistsException.class, () -> {
            storeService.storeBuyItem(token, request);
        });

        verify(userStoreRepository, never()).save(any(UserStore.class));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("상점 물품 구매 - 이미 구매한 아이템 예외")
    public void testStoreBuyItem_AlreadyPurchased() {
        // given
        StoreDTO.StoreBuyRequest request = new StoreDTO.StoreBuyRequest();
        request.setItem_type(ItemType.BACKGROUND);
        request.setName("숲 배경");

        UserStore existingUserStore = new UserStore(user, store1);

        when(userService.tokenToUser(token)).thenReturn(user);
        when(storeRepository.findByItemTypeAndName(ItemType.BACKGROUND, "숲 배경"))
                .thenReturn(Optional.of(store1));
        when(userStoreRepository.findByUserAndStore(user, store1)).thenReturn(existingUserStore);

        // when & then
        assertThrows(StoreItemAlreadyBoughtException.class, () -> {
            storeService.storeBuyItem(token, request);
        });

        verify(userStoreRepository, never()).save(any(UserStore.class));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("상점 물품 구매 - 코인 부족 예외")
    public void testStoreBuyItem_InsufficientCoins() {
        // given
        user.setTotal_coin(50); // 부족한 코인 설정
        StoreDTO.StoreBuyRequest request = new StoreDTO.StoreBuyRequest();
        request.setItem_type(ItemType.BACKGROUND);
        request.setName("숲 배경");

        when(userService.tokenToUser(token)).thenReturn(user);
        when(storeRepository.findByItemTypeAndName(ItemType.BACKGROUND, "숲 배경"))
                .thenReturn(Optional.of(store1)); // 가격 100
        when(userStoreRepository.findByUserAndStore(user, store1)).thenReturn(null);

        // when & then
        assertThrows(InsufficientCoinException.class, () -> {
            storeService.storeBuyItem(token, request);
        });

        assertEquals(50, user.getTotal_coin(), "코인이 차감되지 않아야 함");
        verify(userStoreRepository, never()).save(any(UserStore.class));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("상점 물품 구매 - 딱 맞는 코인으로 구매")
    public void testStoreBuyItem_ExactCoins() {
        // given
        user.setTotal_coin(100); // 정확히 아이템 가격만큼
        StoreDTO.StoreBuyRequest request = new StoreDTO.StoreBuyRequest();
        request.setItem_type(ItemType.BACKGROUND);
        request.setName("숲 배경");

        when(userService.tokenToUser(token)).thenReturn(user);
        when(storeRepository.findByItemTypeAndName(ItemType.BACKGROUND, "숲 배경"))
                .thenReturn(Optional.of(store1)); // 가격 100
        when(userStoreRepository.findByUserAndStore(user, store1)).thenReturn(null);

        // when
        String result = storeService.storeBuyItem(token, request);

        // then
        assertEquals("아이템을 구매했습니다.", result);
        assertEquals(0, user.getTotal_coin(), "코인이 0이 되어야 함");
        verify(userStoreRepository, times(1)).save(any(UserStore.class));
        verify(userRepository, times(1)).save(user);
    }

    @Test
    @DisplayName("상점 물품 구매 - 고가 아이템 구매")
    public void testStoreBuyItem_ExpensiveItem() {
        // given
        user.setTotal_coin(5000);
        Store expensiveStore = new Store();
        expensiveStore.setItemType(ItemType.WALL);
        expensiveStore.setName("황금 벽지");
        expensiveStore.setPrice(3000);

        StoreDTO.StoreBuyRequest request = new StoreDTO.StoreBuyRequest();
        request.setItem_type(ItemType.WALL);
        request.setName("황금 벽지");

        when(userService.tokenToUser(token)).thenReturn(user);
        when(storeRepository.findByItemTypeAndName(ItemType.WALL, "황금 벽지"))
                .thenReturn(Optional.of(expensiveStore));
        when(userStoreRepository.findByUserAndStore(user, expensiveStore)).thenReturn(null);

        // when
        String result = storeService.storeBuyItem(token, request);

        // then
        assertEquals("아이템을 구매했습니다.", result);
        assertEquals(2000, user.getTotal_coin(), "고가 아이템 구매 후 남은 코인 확인");
        verify(userStoreRepository, times(1)).save(any(UserStore.class));
        verify(userRepository, times(1)).save(user);
    }

    /**
     * 통합 시나리오 테스트
     */
    @Test
    @DisplayName("시나리오 테스트 - 상점 조회 후 구매")
    public void testScenario_ViewAndPurchase() {
        // given
        List<Store> backgroundStores = List.of(store1, store2);

        when(userService.tokenToUser(token)).thenReturn(user);

        // 1단계: 상점 조회
        when(storeRepository.findByItemType(ItemType.BACKGROUND)).thenReturn(backgroundStores);
        when(userStoreRepository.findByUserAndStore(user, store1)).thenReturn(null);
        when(userStoreRepository.findByUserAndStore(user, store2)).thenReturn(null);

        // when - 1단계: 상점 조회
        List<StoreDTO.StoreViewResponse> viewResponses = storeService.storeViewByCategory(token, ItemType.BACKGROUND);

        // then - 1단계: 구매하지 않은 상태 확인
        assertEquals(2, viewResponses.size());
        assertFalse(viewResponses.get(0).isBuy());
        assertFalse(viewResponses.get(1).isBuy());

        // given - 2단계: 구매 준비
        StoreDTO.StoreBuyRequest request = new StoreDTO.StoreBuyRequest();
        request.setItem_type(ItemType.BACKGROUND);
        request.setName("숲 배경");

        when(storeRepository.findByItemTypeAndName(ItemType.BACKGROUND, "숲 배경"))
                .thenReturn(Optional.of(store1));

        int initialCoin = user.getTotal_coin();

        // when - 2단계: 구매
        String buyResult = storeService.storeBuyItem(token, request);

        // then - 2단계: 구매 완료 확인
        assertEquals("아이템을 구매했습니다.", buyResult);
        assertEquals(initialCoin - store1.getPrice(), user.getTotal_coin());

        verify(userStoreRepository, times(1)).save(any(UserStore.class));
        verify(userRepository, times(1)).save(user);

        System.out.println("시나리오 테스트 완료: 초기 코인 = " + initialCoin
                + ", 구매 후 코인 = " + user.getTotal_coin());
    }
}