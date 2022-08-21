package com.example.gateway;

import java.time.ZoneId;
import java.time.ZonedDateTime;

public class ZoneDateTimeDemo {
    public static void main(String[] args) {
        ZonedDateTime now = ZonedDateTime.now();
        System.out.println(now);

        ZonedDateTime now01 = now.plusSeconds(100);
        System.out.println(now01);

        ZonedDateTime now02 = ZonedDateTime.now(ZoneId.of("Asia/Shanghai"));
        System.out.println(now02);
    }
}
