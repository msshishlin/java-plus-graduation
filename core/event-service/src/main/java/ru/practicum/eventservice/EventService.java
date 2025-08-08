package ru.practicum.eventservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * Основной класс сервиса для работы с событиями, содержащий точку входа в приложение.
 */
@ComponentScan(basePackages = {"ewm.client", "ru.practicum.eventservice"})
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "ru.practicum.interactionapi.openfeign")
@SpringBootApplication
public class EventService {
    /**
     * Точка входа в приложение.
     *
     * @param args набор аргументов, с которыми запускается приложение.
     */
    public static void main(String[] args) {
        SpringApplication.run(EventService.class, args);
    }
}