package com.squad2.Squad2_cobol.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
public class BlackListService {
    private final Set<String> blacklist = new HashSet<>();

    public void BlacklistToken (String token) {
        blacklist.add(token);
        log.info("Token blacklisted: {}", token);
    }
    public boolean isTokenBlacklisted(String token) {
        return blacklist.contains(token);
    }
}
