package com.alienworkspace.cdr.cdredgegatewayserver.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

/*
 *  Security Configuration.
 *
 *  <p>
 *  We are configuring the security for the gateway server.
 *  @author: Ike Melody Enumah
 */
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    /*
     *  Security Configuration.
     *
     *  <p>
     *  Configure security filter to accept non-authenticated requests to auth endpoint and authenticated requests to
     *  cdr endpoint. Authenticated requests to cdr endpoint will be authenticated using JWT. The oauth2 jwt should be
     *  customizer with defaults.
     *
     * <p>
     * disable csrf because we are not expecting browser communication
     *
     *  @author: Ike Melody Enumah
     */
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/auth/**").permitAll()
                        .pathMatchers(HttpMethod.GET, "/cdr/metadata/**").permitAll()
                        .pathMatchers(HttpMethod.POST, "/cdr/metadata/**").hasRole("CREATE_METADATA")
                        .pathMatchers(HttpMethod.PUT, "/cdr/metadata/**").hasRole("CREATE_METADATA")
                        .pathMatchers(HttpMethod.DELETE, "/cdr/metadata/**").hasRole("DELETE_METADATA")
                        .pathMatchers(HttpMethod.GET, "/cdr/demographic/**").hasRole("READ_DEMOGRAPHIC")
                        .pathMatchers(HttpMethod.POST, "/cdr/demographic/**").hasRole("CREATE_DEMOGRAPHIC")
                        .pathMatchers(HttpMethod.PUT, "/cdr/demographic/**").hasRole("CREATE_DEMOGRAPHIC")
                        .pathMatchers(HttpMethod.DELETE, "/cdr/demographic/**").hasRole("DELETE_DEMOGRAPHIC")
                        .pathMatchers(HttpMethod.GET, "/cdr/patient/**").hasRole("READ_PATIENT")
                        .pathMatchers(HttpMethod.POST, "/cdr/patient/**").hasRole("CREATE_PATIENT")
                        .pathMatchers(HttpMethod.PUT, "/cdr/patient/**").hasRole("CREATE_PATIENT")
                        .pathMatchers(HttpMethod.DELETE, "/cdr/patient/**").hasRole("DELETE_PATIENT")
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwtSpec ->
                                jwtSpec.jwtAuthenticationConverter(grantedAuthorityConverter())))
                .csrf(ServerHttpSecurity.CsrfSpec::disable); //
        return http.build();
    }

    private Converter<Jwt, Mono<AbstractAuthenticationToken>> grantedAuthorityConverter() {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new KeyCloakRoleConverter());
        return new ReactiveJwtAuthenticationConverterAdapter(jwtAuthenticationConverter);
    }
}
