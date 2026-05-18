package com.taxidispatcher.services.account;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Account Service 메인 애플리케이션
 */
@SpringBootApplication
@ComponentScan(basePackages = {
        "com.taxidispatcher.services.account",
        "com.taxidispatcher.shared.common"
})
public class AccountServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AccountServiceApplication.class, args);
    }
}
