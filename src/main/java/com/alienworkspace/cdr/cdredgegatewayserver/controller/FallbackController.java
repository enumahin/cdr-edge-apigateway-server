package com.alienworkspace.cdr.cdredgegatewayserver.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
public class FallbackController {

    @RequestMapping("/contact-support")
    public Mono<String> fallback() {
        return Mono.just("An error occurred. Please try after some time or contact support.");
    }
}
