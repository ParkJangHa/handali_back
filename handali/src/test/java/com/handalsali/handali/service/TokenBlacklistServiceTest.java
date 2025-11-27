package com.handalsali.handali.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
@DisplayName("TokenBlacklistService 테스트")
public class TokenBlacklistServiceTest {

    @InjectMocks
    private TokenBlacklistService tokenBlacklistService;

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    private String token;
    private long expirationMillis;

    @BeforeEach
    void setUp() {
        token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test.token";
        expirationMillis = 3600000L; // 1시간

        // redisTemplate.opsForValue() 호출 시 valueOperations 반환하도록 설정
        // 필요한 테스트에서만 사용되도록 lenient로 설정
        lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    /**
     * blacklistToken 테스트
     */
    @Test
    @DisplayName("토큰 블랙리스트 저장 - 정상 저장")
    public void testBlacklistToken_Success() {
        // given
        // valueOperations.set() 호출 시 아무 동작 안 함
        doNothing().when(valueOperations)
                .set(eq(token), eq("blacklisted"), eq(expirationMillis), eq(TimeUnit.MILLISECONDS));

        // when
        tokenBlacklistService.blacklistToken(token, expirationMillis);

        // then
        verify(redisTemplate, times(1)).opsForValue();
        verify(valueOperations, times(1))
                .set(token, "blacklisted", expirationMillis, TimeUnit.MILLISECONDS);
    }

    @Test
    @DisplayName("토큰 블랙리스트 저장 - 다양한 만료 시간")
    public void testBlacklistToken_VariousExpirationTimes() {
        // given
        long shortExpiration = 60000L; // 1분
        long longExpiration = 86400000L; // 24시간

        doNothing().when(valueOperations)
                .set(anyString(), eq("blacklisted"), anyLong(), eq(TimeUnit.MILLISECONDS));

        // when
        tokenBlacklistService.blacklistToken(token, shortExpiration);
        tokenBlacklistService.blacklistToken(token, longExpiration);

        // then
        verify(valueOperations, times(1))
                .set(token, "blacklisted", shortExpiration, TimeUnit.MILLISECONDS);
        verify(valueOperations, times(1))
                .set(token, "blacklisted", longExpiration, TimeUnit.MILLISECONDS);
    }

    @Test
    @DisplayName("토큰 블랙리스트 저장 - 여러 토큰 저장")
    public void testBlacklistToken_MultipleTokens() {
        // given
        String token1 = "token1.test.value";
        String token2 = "token2.test.value";
        String token3 = "token3.test.value";

        doNothing().when(valueOperations)
                .set(anyString(), eq("blacklisted"), eq(expirationMillis), eq(TimeUnit.MILLISECONDS));

        // when
        tokenBlacklistService.blacklistToken(token1, expirationMillis);
        tokenBlacklistService.blacklistToken(token2, expirationMillis);
        tokenBlacklistService.blacklistToken(token3, expirationMillis);

        // then
        verify(valueOperations, times(1))
                .set(token1, "blacklisted", expirationMillis, TimeUnit.MILLISECONDS);
        verify(valueOperations, times(1))
                .set(token2, "blacklisted", expirationMillis, TimeUnit.MILLISECONDS);
        verify(valueOperations, times(1))
                .set(token3, "blacklisted", expirationMillis, TimeUnit.MILLISECONDS);
        verify(valueOperations, times(3))
                .set(anyString(), eq("blacklisted"), eq(expirationMillis), eq(TimeUnit.MILLISECONDS));
    }

    @Test
    @DisplayName("토큰 블랙리스트 저장 - 동일 토큰 재저장")
    public void testBlacklistToken_SameTokenTwice() {
        // given
        doNothing().when(valueOperations)
                .set(eq(token), eq("blacklisted"), anyLong(), eq(TimeUnit.MILLISECONDS));

        // when
        tokenBlacklistService.blacklistToken(token, 1000L);
        tokenBlacklistService.blacklistToken(token, 2000L); // 덮어쓰기

        // then
        verify(valueOperations, times(1))
                .set(token, "blacklisted", 1000L, TimeUnit.MILLISECONDS);
        verify(valueOperations, times(1))
                .set(token, "blacklisted", 2000L, TimeUnit.MILLISECONDS);
    }

    /**
     * isTokenBlacklisted 테스트
     */
    @Test
    @DisplayName("토큰 블랙리스트 확인 - 블랙리스트에 있는 토큰")
    public void testIsTokenBlacklisted_TokenExists() {
        // given
        when(redisTemplate.hasKey(token)).thenReturn(true);

        // when
        boolean result = tokenBlacklistService.isTokenBlacklisted(token);

        // then
        assertTrue(result, "블랙리스트에 있는 토큰은 true를 반환해야 함");
        verify(redisTemplate, times(1)).hasKey(token);
    }

    @Test
    @DisplayName("토큰 블랙리스트 확인 - 블랙리스트에 없는 토큰")
    public void testIsTokenBlacklisted_TokenNotExists() {
        // given
        when(redisTemplate.hasKey(token)).thenReturn(false);

        // when
        boolean result = tokenBlacklistService.isTokenBlacklisted(token);

        // then
        assertFalse(result, "블랙리스트에 없는 토큰은 false를 반환해야 함");
        verify(redisTemplate, times(1)).hasKey(token);
    }

    @Test
    @DisplayName("토큰 블랙리스트 확인 - null 반환 시 처리")
    public void testIsTokenBlacklisted_NullReturn() {
        // given
        when(redisTemplate.hasKey(token)).thenReturn(null);

        // when
        boolean result = tokenBlacklistService.isTokenBlacklisted(token);

        // then
        assertFalse(result, "null 반환 시 false로 처리되어야 함 (NPE 발생 안 함)");
        verify(redisTemplate, times(1)).hasKey(token);
    }

    @Test
    @DisplayName("토큰 블랙리스트 확인 - 여러 토큰 확인")
    public void testIsTokenBlacklisted_MultipleTokens() {
        // given
        String blacklistedToken = "blacklisted.token";
        String validToken = "valid.token";

        when(redisTemplate.hasKey(blacklistedToken)).thenReturn(true);
        when(redisTemplate.hasKey(validToken)).thenReturn(false);

        // when
        boolean blacklistedResult = tokenBlacklistService.isTokenBlacklisted(blacklistedToken);
        boolean validResult = tokenBlacklistService.isTokenBlacklisted(validToken);

        // then
        assertTrue(blacklistedResult, "블랙리스트 토큰은 true");
        assertFalse(validResult, "정상 토큰은 false");
        verify(redisTemplate, times(1)).hasKey(blacklistedToken);
        verify(redisTemplate, times(1)).hasKey(validToken);
    }

    /**
     * 통합 시나리오 테스트
     */
    @Test
    @DisplayName("시나리오 테스트 - 토큰 저장 후 확인")
    public void testScenario_BlacklistAndCheck() {
        // given
        doNothing().when(valueOperations)
                .set(eq(token), eq("blacklisted"), eq(expirationMillis), eq(TimeUnit.MILLISECONDS));

        // when - 1단계: 토큰 블랙리스트 저장 전 확인
        when(redisTemplate.hasKey(token)).thenReturn(false);
        boolean beforeBlacklist = tokenBlacklistService.isTokenBlacklisted(token);

        // when - 2단계: 토큰 블랙리스트에 저장
        tokenBlacklistService.blacklistToken(token, expirationMillis);

        // when - 3단계: 토큰 블랙리스트 저장 후 확인
        when(redisTemplate.hasKey(token)).thenReturn(true);
        boolean afterBlacklist = tokenBlacklistService.isTokenBlacklisted(token);

        // then
        assertFalse(beforeBlacklist, "저장 전에는 블랙리스트에 없어야 함");
        assertTrue(afterBlacklist, "저장 후에는 블랙리스트에 있어야 함");

        verify(redisTemplate, times(2)).hasKey(token);
        verify(valueOperations, times(1))
                .set(token, "blacklisted", expirationMillis, TimeUnit.MILLISECONDS);

        System.out.println("시나리오 테스트 완료: 저장 전 = " + beforeBlacklist + ", 저장 후 = " + afterBlacklist);
    }

    @Test
    @DisplayName("시나리오 테스트 - 로그아웃 플로우")
    public void testScenario_LogoutFlow() {
        // given - 사용자 로그아웃 시나리오
        String userToken = "user.jwt.token";
        long tokenExpiration = 7200000L; // 2시간

        doNothing().when(valueOperations)
                .set(eq(userToken), eq("blacklisted"), eq(tokenExpiration), eq(TimeUnit.MILLISECONDS));
        when(redisTemplate.hasKey(userToken)).thenReturn(false, true);

        // when - 1단계: 로그아웃 전 토큰 유효성 확인
        boolean beforeLogout = tokenBlacklistService.isTokenBlacklisted(userToken);

        // when - 2단계: 로그아웃 처리 (토큰 블랙리스트 등록)
        tokenBlacklistService.blacklistToken(userToken, tokenExpiration);

        // when - 3단계: 로그아웃 후 토큰 재사용 시도
        boolean afterLogout = tokenBlacklistService.isTokenBlacklisted(userToken);

        // then
        assertFalse(beforeLogout, "로그아웃 전에는 토큰이 유효해야 함");
        assertTrue(afterLogout, "로그아웃 후에는 토큰이 무효화되어야 함");

        verify(valueOperations, times(1))
                .set(userToken, "blacklisted", tokenExpiration, TimeUnit.MILLISECONDS);
        verify(redisTemplate, times(2)).hasKey(userToken);

        System.out.println("로그아웃 플로우 테스트 완료");
    }

    @Test
    @DisplayName("시나리오 테스트 - 여러 사용자 동시 로그아웃")
    public void testScenario_MultipleUsersLogout() {
        // given
        String token1 = "user1.token";
        String token2 = "user2.token";
        String token3 = "user3.token";

        doNothing().when(valueOperations)
                .set(anyString(), eq("blacklisted"), eq(expirationMillis), eq(TimeUnit.MILLISECONDS));

        // when - 여러 사용자 동시 로그아웃
        tokenBlacklistService.blacklistToken(token1, expirationMillis);
        tokenBlacklistService.blacklistToken(token2, expirationMillis);
        tokenBlacklistService.blacklistToken(token3, expirationMillis);

        // when - 각 토큰 블랙리스트 확인
        when(redisTemplate.hasKey(token1)).thenReturn(true);
        when(redisTemplate.hasKey(token2)).thenReturn(true);
        when(redisTemplate.hasKey(token3)).thenReturn(true);

        boolean isToken1Blacklisted = tokenBlacklistService.isTokenBlacklisted(token1);
        boolean isToken2Blacklisted = tokenBlacklistService.isTokenBlacklisted(token2);
        boolean isToken3Blacklisted = tokenBlacklistService.isTokenBlacklisted(token3);

        // then
        assertTrue(isToken1Blacklisted, "사용자1 토큰 블랙리스트 등록 확인");
        assertTrue(isToken2Blacklisted, "사용자2 토큰 블랙리스트 등록 확인");
        assertTrue(isToken3Blacklisted, "사용자3 토큰 블랙리스트 등록 확인");

        verify(valueOperations, times(3))
                .set(anyString(), eq("blacklisted"), eq(expirationMillis), eq(TimeUnit.MILLISECONDS));
        verify(redisTemplate, times(3)).hasKey(anyString());

        System.out.println("여러 사용자 동시 로그아웃 테스트 완료");
    }

    @Test
    @DisplayName("엣지 케이스 - 빈 문자열 토큰")
    public void testEdgeCase_EmptyStringToken() {
        // given
        String emptyToken = "";

        doNothing().when(valueOperations)
                .set(eq(emptyToken), eq("blacklisted"), eq(expirationMillis), eq(TimeUnit.MILLISECONDS));
        when(redisTemplate.hasKey(emptyToken)).thenReturn(true);

        // when
        tokenBlacklistService.blacklistToken(emptyToken, expirationMillis);
        boolean result = tokenBlacklistService.isTokenBlacklisted(emptyToken);

        // then
        assertTrue(result);
        verify(valueOperations, times(1))
                .set(emptyToken, "blacklisted", expirationMillis, TimeUnit.MILLISECONDS);
        verify(redisTemplate, times(1)).hasKey(emptyToken);
    }

    @Test
    @DisplayName("엣지 케이스 - 매우 짧은 만료 시간")
    public void testEdgeCase_VeryShortExpiration() {
        // given
        long veryShortExpiration = 1L; // 1ms

        doNothing().when(valueOperations)
                .set(eq(token), eq("blacklisted"), eq(veryShortExpiration), eq(TimeUnit.MILLISECONDS));

        // when
        tokenBlacklistService.blacklistToken(token, veryShortExpiration);

        // then
        verify(valueOperations, times(1))
                .set(token, "blacklisted", veryShortExpiration, TimeUnit.MILLISECONDS);
    }

    @Test
    @DisplayName("엣지 케이스 - 매우 긴 만료 시간")
    public void testEdgeCase_VeryLongExpiration() {
        // given
        long veryLongExpiration = Long.MAX_VALUE;

        doNothing().when(valueOperations)
                .set(eq(token), eq("blacklisted"), eq(veryLongExpiration), eq(TimeUnit.MILLISECONDS));

        // when
        tokenBlacklistService.blacklistToken(token, veryLongExpiration);

        // then
        verify(valueOperations, times(1))
                .set(token, "blacklisted", veryLongExpiration, TimeUnit.MILLISECONDS);
    }
}