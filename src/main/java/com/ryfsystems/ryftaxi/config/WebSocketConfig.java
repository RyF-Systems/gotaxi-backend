package com.ryfsystems.ryftaxi.config;

import com.ryfsystems.ryftaxi.controller.ChatWebSocketHandler;
import com.ryfsystems.ryftaxi.service.MessageService;
import com.ryfsystems.ryftaxi.service.TaxiRideService;
import com.ryfsystems.ryftaxi.service.UserRoleService;
import com.ryfsystems.ryftaxi.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final MessageService messageService;
    private final TaxiRideService taxiRideService;
    private final UserService userService;
    private final UserRoleService userRoleService;

    public WebSocketConfig(MessageService messageService, TaxiRideService taxiRideService, UserService userService,
                           UserRoleService userRoleService) {
        this.messageService = messageService;
        this.taxiRideService = taxiRideService;
        this.userService = userService;
        this.userRoleService = userRoleService;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(chatWebSocketHandler(), "/ws/chat")
                .setAllowedOrigins("*");
    }

    @Bean
    public ChatWebSocketHandler chatWebSocketHandler() {
        return new ChatWebSocketHandler(messageService, taxiRideService, userService, userRoleService);
    }

    @Bean
    public ServletServerContainerFactoryBean createWebSocketContainer() {
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        container.setMaxTextMessageBufferSize(8192);
        container.setMaxBinaryMessageBufferSize(8192);
        return container;
    }

}
