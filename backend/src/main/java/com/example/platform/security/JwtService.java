package com.example.platform.security;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;

import java.util.function.Function;

import javax.crypto.SecretKey;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.example.platform.entity.User;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long jwtExpiration;


    // convert secret string into secret Key that can be used by the library
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // has three parts: header, payload (subject(username),issued at, expiration),
    // signature
    public String generateToken(User user) {
        return Jwts.builder()
                .subject(user.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSigningKey())
                .compact();
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Returns null if the jwt cannot be parsed or validated
    public String extractUsername(String token) {
        try{
        return extractClaim(token, Claims::getSubject);
        } catch (UnsupportedJwtException 
            | MalformedJwtException 
            | SecurityException 
            | ExpiredJwtException
            | IllegalArgumentException e){
            return null;
        }
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {

        return Jwts
                .parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

    }

    public boolean validateToken(
            String token,
            UserDetails userDetails) {

        //optimisation needed
        String username = extractUsername(token);
        if(username==null)
            return false;
        return username.equals(userDetails.getUsername())
                && !isTokenExpired(token);

    }

    private boolean isTokenExpired(String token) {

        return extractExpiration(token)
                .before(new Date());

    }

}