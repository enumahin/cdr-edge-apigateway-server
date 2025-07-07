package com.alienworkspace.cdr.cdredgegatewayserver.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.*;

public class KeyCloakRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {


    @Override
    public Collection<GrantedAuthority> convert(Jwt source) {
        Map<String, Object> realmAccess = (Map<String, Object>) source.getClaims().get("realm_access");

        if (realmAccess == null || realmAccess.isEmpty()) {
            return new ArrayList<>();
        }

        return ((List<String>) realmAccess.get("roles"))
                .stream()
                .map(role -> "ROLE_" + role)
                .map(role -> (GrantedAuthority) () -> role)
                .toList();
    }
}
