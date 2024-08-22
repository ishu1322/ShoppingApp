package com.shoppingapp.swagger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;



@Configuration
public class OpenApiConfig {
	@Bean
     OpenAPI springShopOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("Shopping App API")
                .description("Shopping App API documentation")
                .version("v1.0.0")
                .contact(new Contact().name("Ishu")
               		 .email("ishudagar1322@gmail.com")
               		 ));
                
    }
}
