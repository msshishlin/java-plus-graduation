package ru.practicum.collector;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Основной класс сервиса для работы с информацией о действиях пользователей, содержащий точку входа в приложение.
 */
@ConfigurationPropertiesScan
@EnableDiscoveryClient
@SpringBootApplication
public class Collector {
    public static void main(String[] args) {
        SpringApplication.run(Collector.class, args);
    }
}