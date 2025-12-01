package com.ryfsystems.ryftaxi.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenAPIConfig {

    @Value("${ryftaxi.openapi.dev-url}")
    private String devUrl;

    @Value("${ryftaxi.openapi.prod-url}")
    private String prodUrl;

    @Bean
    public OpenAPI openAPI() {
        Server devServer = new Server();
        devServer.setUrl(devUrl);
        devServer.setDescription("Development API");

        Server prodServer = new Server();
        prodServer.setUrl(prodUrl);
        prodServer.setDescription("Production API");

        Contact contact = new Contact();
        contact.setName("RyFTaxi");
        contact.setUrl("https://www.ryftaxi.com");
        contact.setEmail("raynitoflores@gmail.com");

        License mitLicense = new License().name("MIT License").url("https://choosealicense.com/licenses/mit/");

        Info info = new Info()
                .title("RyFTaxi")
                .description("Esta API expone los endpoints para la aplicación RyFTaxi, incluyendo WebSocket para chat en tiempo real y gestión de servicios de taxi.")
                .version("1.0")
                .contact(contact)
                .license(mitLicense);

        return  new OpenAPI()
                .info(info)
                .servers(List.of(devServer, prodServer));
    }
}
