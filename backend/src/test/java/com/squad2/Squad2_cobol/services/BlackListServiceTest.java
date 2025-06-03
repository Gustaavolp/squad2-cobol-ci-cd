package com.squad2.Squad2_cobol.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BlackListServiceTest {

    private BlackListService blackListService;

    @BeforeEach
    void setUp() {
        blackListService = new BlackListService();
    }

    @Test
    void testBlacklistToken_AddsTokenToBlacklist() {
        String token = "fake-token-123";
        blackListService.BlacklistToken(token);

        assertTrue(blackListService.isTokenBlacklisted(token), "Token should be in the blacklist");
    }

    @Test
    void testIsTokenBlacklisted_ReturnsFalseIfNotBlacklisted() {
        String token = "not-blacklisted-token";

        assertFalse(blackListService.isTokenBlacklisted(token), "Token should not be in the blacklist");
    }

    @Test
    void testBlacklistToken_DoesNotAddDuplicates() {
        String token = "duplicate-token";

        blackListService.BlacklistToken(token);
        blackListService.BlacklistToken(token);

        assertTrue(blackListService.isTokenBlacklisted(token), "Token should still be blacklisted");

    }
}
