package ru.practicum.categoryservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Основной класс сервиса для работы с категориями событий, содержащий точку входа в приложение.
 */
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "ru.practicum.interactionapi.openfeign")
@SpringBootApplication
public class CategoryService {
    /**
     * Точка входа в приложение.
     *
     * @param args набор аргументов, с которыми запускается приложение.
     */
    public static void main(String[] args) {
        SpringApplication.run(CategoryService.class, args);
    }
}