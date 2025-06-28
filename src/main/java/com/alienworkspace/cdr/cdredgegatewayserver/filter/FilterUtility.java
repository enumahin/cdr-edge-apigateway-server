package com.alienworkspace.cdr.cdredgegatewayserver.filter;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import java.util.List;

@Component
public class FilterUtility {

    public static final String CDR_CORRELATION_ID = "X-cdr-correlation-id";

    public ServerWebExchange setCorrelationId(ServerWebExchange exchange, String correlationId) {
        return this.setRequestHeader(exchange, CDR_CORRELATION_ID, correlationId);
    }

    public String getCorrelationId(HttpHeaders requestHeaders) {
        if (requestHeaders.get(CDR_CORRELATION_ID) != null) {
            List<String> requestHeaderList = requestHeaders.get(CDR_CORRELATION_ID);
            return requestHeaderList.stream().findFirst().get();
        } else {
            return null;
        }
    }

    public ServerWebExchange setRequestHeader(ServerWebExchange exchange, String key, String value) {
        return exchange.mutate()
                .request(exchange.getRequest().mutate()
                        .header(key, value)
                        .build())
                .build();
    }
}
