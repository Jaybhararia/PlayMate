package com.jaybhararia.playmate.auth.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.time.LocalDateTime;
import java.util.Date;

@Service
public class JwtUtils {

    @Value("${spring.application.secret.key}")
    private static String SECRET;

    public String getTokenFromHeader(HttpServletRequest httpServletRequest){
        String jwt = httpServletRequest.getHeader("Authorization");
        if(jwt != null && jwt.startsWith("Bearer ")){
            return jwt.substring(7);
        }
        return null;
    }

    public String generateTokenFromUsername(String username){
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(new Date().getTime() + 24*60*60*1000))
                .signWith(key())
                .compact();
    }

    public String validateAndGetUsernameFromToken(String token){
        String username = null;
        try {
            username = Jwts.parser().verifyWith(key()).build().parseSignedClaims(token).getPayload().getSubject();
        } catch (JwtException e) {
            throw new JwtException(e.getMessage());
        } catch (Exception e){
            throw new RuntimeException(e);
        }

        return username;
    }



    public SecretKey key(){
//        byte[] arr = Decoders.BASE64.decode(SECRET); //for the securely generated key (base64 format)
        byte[] arr = SECRET.getBytes(); //Plain text format
        return Keys.hmacShaKeyFor(arr);
    }
}
