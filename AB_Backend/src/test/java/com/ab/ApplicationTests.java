package com.ab;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@EnableAutoConfiguration(exclude = {HibernateJpaAutoConfiguration.class})
class ApplicationTests {

    @Test
    void contextLoads() {
        // Simple context load check without DB
    }
}
