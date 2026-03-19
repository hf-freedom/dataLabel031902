package com.example.todo.config;

import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class RandomPortConfig implements WebServerFactoryCustomizer<ConfigurableWebServerFactory> {

    @Override
    public void customize(ConfigurableWebServerFactory factory) {
        int minPort = 11000;
        int maxPort = 12000;
        int port = minPort + new Random().nextInt(maxPort - minPort + 1);
        factory.setPort(8081);
        System.out.println("应用启动在端口: " + port);
    }
}
