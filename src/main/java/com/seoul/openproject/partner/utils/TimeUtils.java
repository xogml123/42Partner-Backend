package com.seoul.openproject.partner.utils;

import java.time.LocalDateTime;

public class TimeUtils {

    public static LocalDateTime nowWithoutNano() {
        return LocalDateTime.parse(LocalDateTime.now().toString().split("\\.")[0]);
    }
}
