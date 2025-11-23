package com.delivery_api.Projeto.Delivery.API.util;

import com.delivery_api.Projeto.Delivery.API.entity.Usuario;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    public static Usuario getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            return null;
        }
        return (Usuario) authentication.getPrincipal();
    }

    public static Long getCurrentUserId() {
        Usuario currentUser = getCurrentUser();
        return (currentUser != null) ? currentUser.getId() : null;
    }

    public static boolean hasRole(String role) {
        Usuario currentUser = getCurrentUser();
        if (currentUser == null) {
            return false;
        }
        return currentUser.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_" + role));
    }
}
