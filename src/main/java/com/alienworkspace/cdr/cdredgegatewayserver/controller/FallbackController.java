package com.alienworkspace.cdr.cdredgegatewayserver.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
public class FallbackController {

    @RequestMapping("/contact-support")
    public Mono<String> fallback(ServerWebExchange exchange) {
        HttpHeaders responseHeaders = exchange.getResponse().getHeaders();
            responseHeaders.add("X-cdr-correlation-id",
                    exchange.getAttributes().get("X-cdr-correlation-id").toString());
        return Mono.just("An error occurred. Please try after some time or contact support.");
    }
}
