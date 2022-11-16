package com.seoul.openproject.partner.service;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class RandomMatchServiceTest {

    @Test
    void createRandomMatch() {
        System.out.println(LocalDateTime.now());

        System.out.println(LocalDateTime.parse(LocalDateTime.now().toString().split("\\.")[0]));
    }

    @Test
    void deleteRandomMatch() {
    }
}