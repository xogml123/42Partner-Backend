package com.seoul.openproject.partner.utils;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class RedisKeyFactory {
    public static final String RANDOM_MATCHES = "randomMatches";
    public static final String RANDOM_MATCH_PARTICIPANTS = "randomMatchParticipants";
    private static final String DELIMITER=":";

    @Getter
    @RequiredArgsConstructor
    public enum Key {
        MEMBER("member");

        private final String value;

    }

    public static String toKey(Key keyDomain, String id){
        return keyDomain.getValue() + DELIMITER + id;
    }

}
