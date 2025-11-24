package com.handalsali.handali.service;

import com.handalsali.handali.domain.Store;
import com.handalsali.handali.domain.User;
import com.handalsali.handali.domain.UserItem;
import com.handalsali.handali.domain.UserStore;
import com.handalsali.handali.enums.ItemType;
import com.handalsali.handali.exception.EmailAlreadyExistsException;
import com.handalsali.handali.exception.UserNotFoundException;
import com.handalsali.handali.repository.StoreRepository;
import com.handalsali.handali.repository.UserItemRepository;
import com.handalsali.handali.repository.UserRepository;
import com.handalsali.handali.repository.UserStoreRepository;
import com.handalsali.handali.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private UserStoreRepository userStoreRepository;

    @Mock
    private UserItemRepository userItemRepository;

    @Mock
    private HandbookService handbookService;

    private String testEmail;
    private String testName;
    private String testPassword;
    private String testPhone;
    private LocalDate testBirthday;

    @BeforeEach
    void setUp() {
        testEmail = "test@example.com";
        testName = "홍길동";
        testPassword = "password123";
        testPhone = "010-1234-5678";
        testBirthday = LocalDate.of(1990, 1, 1);
    }

    /**
     * [회원가입 테스트]
     */
    @Test
    @DisplayName("회원가입 성공 - 모든 정보가 올바른 경우")
    void signUp_Success_WhenAllDataValid() {
        // given
        String encryptedPassword = "encrypted_password";

        Store backgroundStore = new Store();
        backgroundStore.setStoreId(1L);
        backgroundStore.setItemType(ItemType.BACKGROUND);
        backgroundStore.setName("기본 배경");
        backgroundStore.setPrice(0);

        Store sofaStore = new Store();
        sofaStore.setStoreId(2L);
        sofaStore.setItemType(ItemType.SOFA);
        sofaStore.setName("기본 소파");
        sofaStore.setPrice(0);

        List<Store> freeStores = List.of(backgroundStore, sofaStore);

        when(userRepository.existsByEmail(testEmail)).thenReturn(false);
        when(passwordEncoder.encode(testPassword)).thenReturn(encryptedPassword);
        when(storeRepository.findByPrice(0)).thenReturn(freeStores);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setUserId(1L);
            return user;
        });
        when(userStoreRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));
        when(userItemRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        User result = userService.signUp(testName, testEmail, testPassword, testPhone, testBirthday);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo(testEmail);
        assertThat(result.getName()).isEqualTo(testName);
        assertThat(result.getPassword()).isEqualTo(encryptedPassword);
        assertThat(result.getPhone()).isEqualTo(testPhone);
        assertThat(result.getBirthday()).isEqualTo(testBirthday);

        // 비밀번호 암호화 확인
        verify(passwordEncoder, times(1)).encode(testPassword);

        // 사용자 저장 확인
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertThat(savedUser.getEmail()).isEqualTo(testEmail);
        assertThat(savedUser.getName()).isEqualTo(testName);

        // 무료 아이템 조회 확인
        verify(storeRepository, times(1)).findByPrice(0);

        // UserStore 저장 확인 (구매 내역)
        ArgumentCaptor<List<UserStore>> userStoreCaptor = ArgumentCaptor.forClass(List.class);
        verify(userStoreRepository, times(1)).saveAll(userStoreCaptor.capture());
        List<UserStore> savedUserStores = userStoreCaptor.getValue();
        assertThat(savedUserStores).hasSize(2);

        // UserItem 저장 확인 (보유 아이템)
        ArgumentCaptor<List<UserItem>> userItemCaptor = ArgumentCaptor.forClass(List.class);
        verify(userItemRepository, times(1)).saveAll(userItemCaptor.capture());
        List<UserItem> savedUserItems = userItemCaptor.getValue();
        assertThat(savedUserItems).hasSize(2);

        // 기본 도감 추가 확인
        verify(handbookService, times(1)).addHandbook(result, "image_0_0_0.png");
    }

    @Test
    @DisplayName("회원가입 실패 - 이메일 중복")
    void signUp_ThrowsException_WhenEmailAlreadyExists() {
        // given
        when(userRepository.existsByEmail(testEmail)).thenReturn(true);

        // when & then
        assertThrows(EmailAlreadyExistsException.class, () -> {
            userService.signUp(testName, testEmail, testPassword, testPhone, testBirthday);
        });

        // 이메일 중복 체크 후 더 이상 진행되지 않음을 확인
        verify(userRepository, times(1)).existsByEmail(testEmail);
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    /**
     * [토큰으로 사용자 찾기 테스트]
     */
    @Test
    @DisplayName("토큰으로 사용자 찾기 성공")
    void tokenToUser_Success_WhenValidToken() {
        // given
        String accessToken = "valid-access-token";
        long userId = 1L;
        User mockUser = new User(testEmail, testName, "encrypted", testPhone, testBirthday);
        mockUser.setUserId(userId);

        when(jwtUtil.extractUserId(accessToken)).thenReturn(userId);
        when(userRepository.findByUserId(userId)).thenReturn(Optional.of(mockUser));

        // when
        User result = userService.tokenToUser(accessToken);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getEmail()).isEqualTo(testEmail);
        assertThat(result.getName()).isEqualTo(testName);

        verify(jwtUtil, times(1)).extractUserId(accessToken);
        verify(userRepository, times(1)).findByUserId(userId);
    }

    @Test
    @DisplayName("토큰으로 사용자 찾기 실패 - 사용자가 존재하지 않음")
    void tokenToUser_ThrowsException_WhenUserNotFound() {
        // given
        String accessToken = "invalid-access-token";
        long userId = 999L;

        when(jwtUtil.extractUserId(accessToken)).thenReturn(userId);
        when(userRepository.findByUserId(userId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(UserNotFoundException.class, () -> {
            userService.tokenToUser(accessToken);
        });

        verify(jwtUtil, times(1)).extractUserId(accessToken);
        verify(userRepository, times(1)).findByUserId(userId);
    }

    /**
     * [퀘스트 보상 지급 테스트]
     */
    @Test
    @DisplayName("퀘스트 보상 지급 성공 - 코인 증가")
    void giveQuestReward_Success_IncreasesUserCoin() {
        // given
        User mockUser = new User(testEmail, testName, "encrypted", testPhone, testBirthday);
        mockUser.setUserId(1L);
        mockUser.setTotal_coin(100);

        int rewardCoin = 50;

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        userService.giveQuestReward(mockUser, rewardCoin);

        // then
        assertThat(mockUser.getTotal_coin()).isEqualTo(150);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertThat(savedUser.getTotal_coin()).isEqualTo(150);
    }

    @Test
    @DisplayName("퀘스트 보상 지급 성공 - 초기 코인 0에서 시작")
    void giveQuestReward_Success_FromZeroCoin() {
        // given
        User mockUser = new User(testEmail, testName, "encrypted", testPhone, testBirthday);
        mockUser.setUserId(1L);
        mockUser.setTotal_coin(0);

        int rewardCoin = 100;

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        userService.giveQuestReward(mockUser, rewardCoin);

        // then
        assertThat(mockUser.getTotal_coin()).isEqualTo(100);
        verify(userRepository, times(1)).save(mockUser);
    }

    @Test
    @DisplayName("퀘스트 보상 지급 성공 - 여러 번 지급")
    void giveQuestReward_Success_MultipleRewards() {
        // given
        User mockUser = new User(testEmail, testName, "encrypted", testPhone, testBirthday);
        mockUser.setUserId(1L);
        mockUser.setTotal_coin(50);

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        userService.giveQuestReward(mockUser, 30);
        userService.giveQuestReward(mockUser, 20);
        userService.giveQuestReward(mockUser, 50);

        // then
        assertThat(mockUser.getTotal_coin()).isEqualTo(150); // 50 + 30 + 20 + 50
        verify(userRepository, times(3)).save(mockUser);
    }
}