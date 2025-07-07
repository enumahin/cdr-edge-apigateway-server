package com.alienworkspace.cdr.cdredgegatewayserver;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import java.time.Duration;
import java.time.LocalDateTime;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import reactor.core.publisher.Mono;


@SpringBootApplication
public class CdrEdgeGatewayServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(CdrEdgeGatewayServerApplication.class, args);
	}


	@Bean
	public RouteLocator cdrRouteConfig(RouteLocatorBuilder builder) {
		return builder.routes()
				.route(r -> r
						.path("/cdr/patient/**")
						.filters(f ->
								f.rewritePath("/cdr/patient/(?<segment>.*)", "/${segment}")
										.circuitBreaker(config ->
												config.setName("patientCircuitBreaker")
														.setFallbackUri("forward:/contact-support")))
						.uri("lb://PATIENT"))
				.route(r -> r
						.path("/cdr/demographic/**")
						.filters(f -> f.rewritePath("/cdr/demographic/(?<segment>.*)",
								"/${segment}")
								.circuitBreaker(config -> config
										.setName("demographicCircuitBreaker")
										.setFallbackUri("forward:/contact-support"))
						.retry(config -> config
								.setRetries(3)
								.setMethods(HttpMethod.GET) // Set retry for idempotent requests only (my choice)
								.setBackoff(Duration.ofMillis(100), Duration.ofMillis(1000), 2, true)
						).requestRateLimiter(config -> config
								.setRateLimiter(redisRateLimiter())
								.setKeyResolver(keyResolver()))
						)
						.uri("lb://DEMOGRAPHIC"))
				.route(r -> r
						.path("/cdr/metadata/**")
						.filters(f -> f
								.rewritePath("/cdr/metadata/(?<segment>.*)", "/${segment}")
								.addResponseHeader("X-Response-Time", LocalDateTime.now().toString())
								.circuitBreaker(config -> config
										.setName("metadataCircuitBreaker")
										.setFallbackUri("forward:/contact-support"))
						)
						.uri("lb://METADATA"))
				.build();
	}

	@Bean
	public Customizer<ReactiveResilience4JCircuitBreakerFactory> defaultCustomizer() {
		return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
				.circuitBreakerConfig(CircuitBreakerConfig.ofDefaults())
				.timeLimiterConfig(TimeLimiterConfig.custom().timeoutDuration(Duration.ofSeconds(5)).build())
				.build());
	}

	@Bean
	public RedisRateLimiter redisRateLimiter() {
		return new RedisRateLimiter(1, 1, 1);
	}

	@Bean
	KeyResolver keyResolver() {
		return exchange -> Mono.justOrEmpty(exchange.getRequest().getHeaders()
						.getFirst("X-user-token"))
				.defaultIfEmpty("anonymous");
	}

}
