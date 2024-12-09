package com.abernathyclinic.gateway.gateway_bff;

import com.abernathyclinic.gateway.gateway_bff.service.JwtService;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

@Configuration
public class GatewayGlobalFilter {

    private final JwtService jwtService;

    public GatewayGlobalFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Bean
    public GlobalFilter jwtValidationFilter() {
        return (exchange, chain) -> {
            HttpHeaders headers = exchange.getRequest().getHeaders();
            String authHeader = headers.getFirst(HttpHeaders.AUTHORIZATION);

            // Debug log for the Authorization header
            System.out.println("Received Authorization Header: " + authHeader);

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            String token = authHeader.substring(7);

            if (!jwtService.isTokenValid(token)) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            return chain.filter(exchange);
        };
    }

    @Bean
    public GlobalFilter modifyRequestHeaders() {
        return (exchange, chain) -> {
            String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (authHeader != null) {
                System.out.println("Authorization Header: " + authHeader); // Debug
                exchange = exchange.mutate()
                        .request(exchange.getRequest().mutate()
                                .header(HttpHeaders.AUTHORIZATION, authHeader)
                                .build())
                        .build();
            }
            return chain.filter(exchange);
        };
    }

}
