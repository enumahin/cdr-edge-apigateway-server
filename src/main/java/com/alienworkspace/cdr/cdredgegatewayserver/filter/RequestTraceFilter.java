package com.alienworkspace.cdr.cdredgegatewayserver.filter;


import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 *  Custom Request Trace Filter.
 *
 *  <p>
 *  Order of the filter execution
 *  By implementing GlobalFilter our filter will be executed by all traffics
 *  By implementing GlobalFilter our filter will be executed by all traffics
 * @author Firstname Lastname
 *
 */
@Order(1)
@Component
public class RequestTraceFilter implements GlobalFilter {

    private static final Logger log = LoggerFactory.getLogger(RequestTraceFilter.class);

    @Autowired
    private FilterUtility filterUtility;

    /**
     *  Implement the filter.
     *
     *  <p>
     *  The check to know if the header already exists is very usefull so we don't generate another id in case
     *  the request is redirected and it hase to pass through the load balancer again.
     *
     * @param exchange ServerWebExchange
     * @param chain GatewayFilterChain All filter are executed in chain so that's why we are
     *              getting the chain here as a parameter so we can call the next chain when
     *              we are done executing our filter
     * @return Mono of Void
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        HttpHeaders requestHeaders = exchange.getRequest().getHeaders();

        if (isCorrelationIdPresent(requestHeaders)) {
            log.debug("cdr-correlation-id found in RequestTraceFilter: {}",
                    filterUtility.getCorrelationId(requestHeaders));
        } else {
            String correlationID = generateCorrelationId();
            exchange = filterUtility.setCorrelationId(exchange, correlationID);
            log.debug("cdr-correlation-id generated in RequestTraceFilter: {}", correlationID);
        }
        return chain.filter(exchange);
    }

    private boolean isCorrelationIdPresent(HttpHeaders requestHeaders) {
        return filterUtility.getCorrelationId(requestHeaders) != null;
    }

    private String generateCorrelationId() {
        return UUID.randomUUID().toString();
    }
}
