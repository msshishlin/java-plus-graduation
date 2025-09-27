package ru.practicum.aggregator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Основной класс сервиса для анализа информации о действиях пользователей, содержащий точку входа в приложение.
 */
@ConfigurationPropertiesScan
@EnableDiscoveryClient
@SpringBootApplication
public class Aggregator {
    public static void main(String[] args) {
        SpringApplication.run(Aggregator.class, args);
    }
}
