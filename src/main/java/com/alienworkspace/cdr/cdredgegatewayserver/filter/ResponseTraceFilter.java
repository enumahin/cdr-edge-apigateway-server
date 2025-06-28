package com.alienworkspace.cdr.cdredgegatewayserver.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import reactor.core.publisher.Mono;

@Configuration
public class ResponseTraceFilter {

    private static final Logger log = LoggerFactory.getLogger(ResponseTraceFilter.class);

    @Autowired
    private FilterUtility filterUtility;

    /**
     *  Add the cdr-correlation-id to the outbound response headers.
     *
     *  <p>
     *      With chain.filter(exchange).then(...) we are extracting the cdr-correlation-id from the request headers
     *      and adding it to the outbound response headers after the request execution is completed;
     *
     * @return GlobalFilter
     */
    @Bean
    public GlobalFilter postGlobalFilter() {
        return (exchange, chain) -> chain.filter(exchange)
                .then(Mono.fromRunnable(() -> {
                    HttpHeaders requestHeaders = exchange.getRequest().getHeaders();
                    String correlationID = filterUtility.getCorrelationId(requestHeaders);
                    log.debug("Updated the cdr-correlation-id to the outbound headers: {}", correlationID);
                    exchange.getResponse().getHeaders().add(FilterUtility.CDR_CORRELATION_ID, correlationID);
                }));
    }
}
