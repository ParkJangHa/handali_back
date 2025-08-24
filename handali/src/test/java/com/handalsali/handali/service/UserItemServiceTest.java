package com.handalsali.handali.service;

import com.handalsali.handali.DTO.UserItemDTO;
import com.handalsali.handali.domain.*;
import com.handalsali.handali.enums.ItemType;
import com.handalsali.handali.exception.StoreItemNotExistsException;
import com.handalsali.handali.repository.HandaliRepository;
import com.handalsali.handali.repository.StoreRepository;
import com.handalsali.handali.repository.UserItemRepository;
import com.handalsali.handali.repository.UserStoreRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserItemServiceTest {

    @InjectMocks
    private UserItemService userItemService;

    @Mock
    private UserItemRepository userItemRepository;

    @Mock
    private UserService userService;

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private UserStoreRepository userStoreRepository;

    @Mock
    private HandaliRepository handaliRepository;

    @Test
    @DisplayName("아이템 적용 성공 - 처음 적용하는 아이템")
    void setUserItem_NewItem_SuccessfullySetsItem() {
        // given
        String token = "test-token";
        UserItemDTO.SetUserItemRequest request = new UserItemDTO.SetUserItemRequest(ItemType.BACKGROUND, "Blue Sky");

        User mockUser = new User();
        mockUser.setUserId(1L);

        Store mockStoreItem = new Store();
        mockStoreItem.setItemType(ItemType.BACKGROUND);
        mockStoreItem.setName("Blue Sky");
        mockStoreItem.setPrice(100);

        UserStore mockUserStore = new UserStore(mockUser, mockStoreItem);
        Handali mockHandali = new Handali("handali", LocalDate.now(), mockUser);

        when(userService.tokenToUser(anyString())).thenReturn(mockUser);
        when(userItemRepository.findByUserAndItemType(mockUser, request.getItem_type())).thenReturn(Optional.empty());
        when(storeRepository.findByItemTypeAndName(request.getItem_type(), request.getName())).thenReturn(Optional.of(mockStoreItem));
        when(userStoreRepository.findByUserAndStore(mockUser, mockStoreItem)).thenReturn(mockUserStore);
        when(userItemRepository.findByUserAndItemTypeAndName(mockUser, request.getItem_type(), request.getName())).thenReturn(Optional.empty());
        when(handaliRepository.findLatestHandaliByCurrentMonth(mockUser.getUserId())).thenReturn(mockHandali);

        // when
        userItemService.setUserItem(token, request);

        // then
        ArgumentCaptor<UserItem> userItemCaptor = ArgumentCaptor.forClass(UserItem.class);
        verify(userItemRepository, times(1)).save(userItemCaptor.capture());
        UserItem savedUserItem = userItemCaptor.getValue();
        assertThat(savedUserItem.getUser()).isEqualTo(mockUser);
        assertThat(savedUserItem.getStore().getName()).isEqualTo("Blue Sky");
        assertThat(savedUserItem.isAvailable()).isTrue();

        ArgumentCaptor<Handali> handaliCaptor = ArgumentCaptor.forClass(Handali.class);
        verify(handaliRepository, times(1)).save(handaliCaptor.capture());
        Handali savedHandali = handaliCaptor.getValue();
        assertThat(savedHandali.getBackground()).isEqualTo("Blue Sky");
    }

    @Test
    @DisplayName("아이템 적용 성공 - 기존에 적용했던 아이템 재적용")
    void setUserItem_ReapplyingExistingItem_SuccessfullySetsItem() {
        // given
        String token = "test-token";
        UserItemDTO.SetUserItemRequest request = new UserItemDTO.SetUserItemRequest(ItemType.SOFA, "Comfy Sofa");

        User mockUser = new User();
        mockUser.setUserId(1L);

        Store mockStoreItem = new Store();
        mockStoreItem.setItemType(ItemType.SOFA);
        mockStoreItem.setName("Comfy Sofa");
        mockStoreItem.setPrice(200);

        UserStore mockUserStore = new UserStore(mockUser, mockStoreItem);
        UserItem existingUserItem = spy(new UserItem(mockUser, mockStoreItem));
        Handali mockHandali = new Handali("handali", LocalDate.now(), mockUser);

        when(userService.tokenToUser(anyString())).thenReturn(mockUser);
        when(userItemRepository.findByUserAndItemType(mockUser, request.getItem_type())).thenReturn(Optional.empty());
        when(storeRepository.findByItemTypeAndName(request.getItem_type(), request.getName())).thenReturn(Optional.of(mockStoreItem));
        when(userStoreRepository.findByUserAndStore(mockUser, mockStoreItem)).thenReturn(mockUserStore);
        when(userItemRepository.findByUserAndItemTypeAndName(mockUser, request.getItem_type(), request.getName())).thenReturn(Optional.of(existingUserItem));
        when(handaliRepository.findLatestHandaliByCurrentMonth(mockUser.getUserId())).thenReturn(mockHandali);

        // when
        userItemService.setUserItem(token, request);

        // then
        verify(existingUserItem, times(1)).setItem();
        verify(userItemRepository, times(1)).save(existingUserItem);
        verify(userItemRepository, never()).save(argThat(item -> item != existingUserItem));

        verify(handaliRepository, times(1)).save(any(Handali.class));
        assertThat(mockHandali.getSofa()).isEqualTo("Comfy Sofa");
    }

    @Test
    @DisplayName("아이템 적용 성공 - 동일 카테고리의 다른 아이템으로 교체")
    void setUserItem_ReplacingAnotherItem_SuccessfullySetsAndCancels() {
        // given
        String token = "test-token";
        UserItemDTO.SetUserItemRequest request = new UserItemDTO.SetUserItemRequest(ItemType.WALL, "Brick Wall");

        User mockUser = new User();
        mockUser.setUserId(1L);

        Store oldStoreItem = new Store();
        oldStoreItem.setItemType(ItemType.WALL);
        oldStoreItem.setName("Plain Wall");
        oldStoreItem.setPrice(50);
        UserItem oldUserItem = spy(new UserItem(mockUser, oldStoreItem));

        Store newStoreItem = new Store();
        newStoreItem.setItemType(ItemType.WALL);
        newStoreItem.setName("Brick Wall");
        newStoreItem.setPrice(150);
        UserStore mockUserStore = new UserStore(mockUser, newStoreItem);
        Handali mockHandali = new Handali("handali", LocalDate.now(), mockUser);
        mockHandali.setWall("Plain Wall");

        when(userService.tokenToUser(anyString())).thenReturn(mockUser);
        when(userItemRepository.findByUserAndItemType(mockUser, request.getItem_type())).thenReturn(Optional.of(oldUserItem));
        when(storeRepository.findByItemTypeAndName(request.getItem_type(), request.getName())).thenReturn(Optional.of(newStoreItem));
        when(userStoreRepository.findByUserAndStore(mockUser, newStoreItem)).thenReturn(mockUserStore);
        when(userItemRepository.findByUserAndItemTypeAndName(mockUser, request.getItem_type(), request.getName())).thenReturn(Optional.empty());
        when(handaliRepository.findLatestHandaliByCurrentMonth(mockUser.getUserId())).thenReturn(mockHandali);

        // when
        userItemService.setUserItem(token, request);

        // then
        verify(oldUserItem, times(1)).cancelItem();

        ArgumentCaptor<UserItem> userItemCaptor = ArgumentCaptor.forClass(UserItem.class);
        verify(userItemRepository).save(userItemCaptor.capture());
        assertThat(userItemCaptor.getValue().getStore().getName()).isEqualTo("Brick Wall");
        assertThat(userItemCaptor.getValue().isAvailable()).isTrue();

        verify(handaliRepository).save(any(Handali.class));
        assertThat(mockHandali.getWall()).isEqualTo("Brick Wall");
    }

    @ParameterizedTest
    @EnumSource(ItemType.class)
    @DisplayName("Handali 업데이트 로직 검증 - 모든 아이템 타입")
    void setUserItem_AllItemTypes_UpdatesCorrectHandaliField(ItemType itemType) {
        // given
        String token = "test-token";
        String itemName = "Test " + itemType.name();
        UserItemDTO.SetUserItemRequest request = new UserItemDTO.SetUserItemRequest(itemType, itemName);

        User mockUser = new User();
        mockUser.setUserId(1L);

        Store mockStoreItem = new Store();
        mockStoreItem.setItemType(itemType);
        mockStoreItem.setName(itemName);
        mockStoreItem.setPrice(100);

        UserStore mockUserStore = new UserStore(mockUser, mockStoreItem);
        Handali mockHandali = new Handali("handali", LocalDate.now(), mockUser);

        when(userService.tokenToUser(anyString())).thenReturn(mockUser);
        when(storeRepository.findByItemTypeAndName(any(), any())).thenReturn(Optional.of(mockStoreItem));
        when(userStoreRepository.findByUserAndStore(any(), any())).thenReturn(mockUserStore);
        when(handaliRepository.findLatestHandaliByCurrentMonth(anyLong())).thenReturn(mockHandali);

        // when
        userItemService.setUserItem(token, request);

        // then
        ArgumentCaptor<Handali> handaliCaptor = ArgumentCaptor.forClass(Handali.class);
        verify(handaliRepository, times(1)).save(handaliCaptor.capture());
        Handali savedHandali = handaliCaptor.getValue();

        switch (itemType) {
            case BACKGROUND -> assertThat(savedHandali.getBackground()).isEqualTo(itemName);
            case FLOOR -> assertThat(savedHandali.getFloor()).isEqualTo(itemName);
            case SOFA -> assertThat(savedHandali.getSofa()).isEqualTo(itemName);
            case WALL -> assertThat(savedHandali.getWall()).isEqualTo(itemName);
        }
    }


    @Test
    @DisplayName("아이템 적용 실패 - 상점에 존재하지 않는 아이템")
    void setUserItem_ThrowsException_WhenStoreItemNotFound() {
        // given
        String token = "test-token";
        UserItemDTO.SetUserItemRequest request = new UserItemDTO.SetUserItemRequest(ItemType.BACKGROUND, "NonExistentItem");

        User mockUser = new User();
        mockUser.setUserId(1L);

        when(userService.tokenToUser(anyString())).thenReturn(mockUser);
        when(userItemRepository.findByUserAndItemType(any(), any())).thenReturn(Optional.empty());
        when(storeRepository.findByItemTypeAndName(request.getItem_type(), request.getName())).thenReturn(Optional.empty());

        // when & then
        StoreItemNotExistsException exception = assertThrows(StoreItemNotExistsException.class, () -> {
            userItemService.setUserItem(token, request);
        });
        assertThat(exception.getMessage()).isEqualTo("상점에 카테고리, 이름에 해당하는 상품이 존재하지 않습니다.");
    }

    @Test
    @DisplayName("아이템 적용 실패 - 유저가 구매하지 않은 아이템")
    void setUserItem_ThrowsException_WhenItemNotPurchasedByUser() {
        // given
        String token = "test-token";
        UserItemDTO.SetUserItemRequest request = new UserItemDTO.SetUserItemRequest(ItemType.FLOOR, "Wooden Floor");

        User mockUser = new User();
        mockUser.setUserId(1L);

        Store mockStoreItem = new Store();
        mockStoreItem.setItemType(ItemType.FLOOR);
        mockStoreItem.setName("Wooden Floor");
        mockStoreItem.setPrice(100);

        when(userService.tokenToUser(anyString())).thenReturn(mockUser);
        when(userItemRepository.findByUserAndItemType(any(), any())).thenReturn(Optional.empty());
        when(storeRepository.findByItemTypeAndName(request.getItem_type(), request.getName())).thenReturn(Optional.of(mockStoreItem));
        when(userStoreRepository.findByUserAndStore(mockUser, mockStoreItem)).thenReturn(null);

        // when & then
        StoreItemNotExistsException exception = assertThrows(StoreItemNotExistsException.class, () -> {
            userItemService.setUserItem(token, request);
        });
        assertThat(exception.getMessage()).isEqualTo("상점에서 아이템을 구매한 적이 없습니다.");
    }
}