package com.handalsali.handali.security;

import com.handalsali.handali.exception.TokenValidationException;
import com.handalsali.handali.service.TokenBlacklistService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.authentication.AuthenticationServiceException;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;
    private final TokenBlacklistService tokenBlacklistService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader=request.getHeader("Authorization");

        if(authHeader!=null&&authHeader.startsWith("Bearer ")){
            String token = authHeader.replace("Bearer ", "").trim();

            //블랙리스트 검사
            if(tokenBlacklistService.isTokenBlacklisted(token)){
                throw new TokenValidationException("해당 토큰은 블랙리스트에 등록되었습니다.");
            }

            //jwt 유효성 검사
            Claims claims=jwtUtil.validateToken(token);
            String email=claims.getSubject();

            UserDetails userDetails=userDetailsService.loadUserByUsername(email);

            UsernamePasswordAuthenticationToken authentication=
                    new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());

            //인증 등록
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request,response);

    }
}
