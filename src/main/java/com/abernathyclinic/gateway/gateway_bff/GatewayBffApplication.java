package com.abernathyclinic.gateway.gateway_bff;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@SpringBootApplication
@EnableConfigServer
public class GatewayBffApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayBffApplication.class, args);
	}

}
