/*
 * ============================================================================
 * FILE: WebConfig.java
 * LOCATION: src/main/java/org/hartford/iqsure/config/
 * PURPOSE: Configures CORS (Cross-Origin Resource Sharing) for the backend.
 *
 * WHAT IS CORS?
 *   - The Angular frontend runs on http://localhost:4200
 *   - The Spring backend runs on http://localhost:8080
 *   - By default, browsers BLOCK requests between different origins (ports)
 *   - This config tells the backend: "Allow requests from localhost on any port"
 *
 * WITHOUT THIS FILE:
 *   - The frontend would get "CORS error" in the browser console
 *   - No API calls would work from Angular to Spring
 *
 * SETTINGS EXPLAINED:
 *   - addMapping("/**") → Apply CORS to ALL backend URLs
 *   - allowedOriginPatterns("http://localhost:*") → Allow any localhost port
 *   - allowedMethods(...) → Allow GET, POST, PUT, DELETE, PATCH, OPTIONS
 *   - allowedHeaders("*") → Allow any HTTP headers
 *   - allowCredentials(true) → Allow cookies/auth tokens to be sent
 *
 * CONNECTS TO:
 *   - Every Angular HTTP request in api.service.ts (frontend/src/app/services/)
 *     goes through this CORS filter before reaching the controllers
 * ============================================================================
 */
package org.hartford.iqsure.config;

import org.h2.server.web.JakartaWebServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOriginPatterns("http://localhost:*")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }

    // H2 Console — registers the servlet so /h2-console works
    @Bean
    public ServletRegistrationBean<JakartaWebServlet> h2Console() {
        return new ServletRegistrationBean<>(new JakartaWebServlet(), "/h2-console/*");
    }
}
