package com.alienworkspace.cdr.cdredgegatewayserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;

import static org.springframework.cloud.gateway.route.builder.RouteDslKt.filters;

@SpringBootApplication
public class CdrEdgeGatewayServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(CdrEdgeGatewayServerApplication.class, args);
	}


	@Bean
	public RouteLocator cdrRouteConfig(RouteLocatorBuilder builder) {
		return builder.routes()
				.route(r -> r
						.path("/cdr/metadata/**")
						.filters(f -> f
								.rewritePath("/cdr/metadata/(?<segment>.*)", "/${segment}")
								.addResponseHeader("X-Response-Time", LocalDateTime.now().toString())
						)
						.uri("lb://METADATA"))
				.route(r -> r
						.path("/cdr/demographic/**")
						.filters(f -> f.rewritePath("/cdr/demographic/(?<segment>.*)", "/${segment}"))
						.uri("lb://DEMOGRAPHIC"))
				.route(r -> r
						.path("/cdr/patient/**")
						.filters(f -> f.rewritePath("/cdr/patient/(?<segment>.*)", "/${segment}"))
						.uri("lb://PATIENT"))
				.build();
	}

}
