package com.dayone.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TokenProvider {
    // 토큰 생성 매서드

    private static final long TOKEN_EXPIRE_TIME = 1000 * 60 * 60; // 1hour
    private static final String KEY_ROLES = "roles";

    @Value("{spring.jwt.secret}")
    private String secretKey;

    public String generateToken(String username, List<String> roles){
        Claims claims = Jwts.claims().setSubject(username);
        claims.put(KEY_ROLES, roles);

        var now = new Date();
        var expireDate = new Date(now.getTime() + TOKEN_EXPIRE_TIME);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now) // 토큰 생성 시간
                .setExpiration(expireDate) // 토큰 만료 시간
                .signWith(SignatureAlgorithm.HS512, this.secretKey)
                // 사용할 암호화 알고리즘, 비밀키
                .compact();

    }
    private Claims parseClaims(String token){
        try{
            return Jwts.parser().setSigningKey(this.secretKey)
                    .parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e){
            return e.getClaims();
        }
    }

    public String getUsername(String token){
        return this.parseClaims(token).getSubject();
    }

    public boolean validateToken(String token){
        // 토큰 빈 값이면 false 반환
        if (!StringUtils.hasText(token)) return false;

        // 토큰 만료 여부 확인
        var claims = this.parseClaims(token);
        return !claims.getExpiration().before(new Date());
    }
}
