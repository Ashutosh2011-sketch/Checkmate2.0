package com.checkmate.backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService service;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        
        // 1. Request ke header se "Authorization" wali line nikalo
        String authorizationHeader = request.getHeader("Authorization");

        String token = null;
        String email = null;

        // 2. Check karo ki kya token "Bearer " se shuru ho raha hai
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring(7); // "Bearer " hatakar asli token nikalo
            email = jwtUtil.extractEmail(token); // Token se email nikalo
        }

        // 3. Agar email mil gaya aur bouncer ne abhi tak check nahi kiya hai
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails userDetails = service.loadUserByUsername(email);

            // 4. Token validate karo
            if (jwtUtil.validateToken(token, userDetails.getUsername())) {
                // Agar token sahi hai, toh green signal de do
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                // Spring Security ko bata do ki "Banda verified hai, isko aane do"
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }
        // 5. Agle darwaze ya API tak request ko jaane do
        filterChain.doFilter(request, response);
    }
}
