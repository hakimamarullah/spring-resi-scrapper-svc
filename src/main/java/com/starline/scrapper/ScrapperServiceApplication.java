package com.starline.scrapper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ScrapperServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ScrapperServiceApplication.class, args);
	}

}
