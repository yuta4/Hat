package com.yuta4.hat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootTest
class HatApplicationTests {

    @Test
    void contextLoads() {
        BCryptPasswordEncoder b = new BCryptPasswordEncoder();
        System.out.println(b.encode("password"));
    }

}
