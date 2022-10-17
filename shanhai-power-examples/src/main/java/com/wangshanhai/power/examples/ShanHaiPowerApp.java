package com.wangshanhai.power.examples;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author Shmily
 */
@SpringBootApplication
public class ShanHaiPowerApp {
    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext context= SpringApplication.run(ShanHaiPowerApp.class, args);
    }
}
