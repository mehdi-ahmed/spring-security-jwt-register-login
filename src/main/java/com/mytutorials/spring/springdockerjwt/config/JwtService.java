package com.mytutorials.spring.springdockerjwt.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    public static final String SECRET_KEY = "ykErQV4Alx25gQUWuJPhktrtqvlCQw7FsfvPZ1KUvNbeHB4htAicfjH08R4InM2M8H1JxdKswZ2aQ9VB248oX63EO2xBmoNjc1oAkwDk4qAX26oVvwupFE5SOjRKgTp6lX593Qx16J0wloJYP7SGsCRnSwhhzF8Qnqa29Qv7SzDyeIfsLxN6YwRTEp8hbSZv38jlC79roONBoTqnBpowVBrqggtsk8NB4nHSCPLOBV7R56aDHqMC3teu9dtHN0lrPdkFvtzuFY2ETtmuJL1q397rQMpxXG7PRdSBUdpv7QiYIxikobASsiD6fYnxksLB65m5htfvLDyprZf7WX1VqJ9DFie1k5kGSWwGUVPGO2s0oc9m62WI5eroLyiIDwdhy6E0FhfB5CKR4GqzNzMTOauBTI0DiOMaMyaBoUCnyS2VUCGmbSvJGEqTRUOVOE1YyXsJjgLTPZhj9z4KfShsqQCKiTvHIdTcubRrPR67BXSdgzwfkLdBdKGinOiJSpgI\n";


    public String extractUsername(String jwtToken) {
        return extractClaim(jwtToken, Claims::getSubject); // subject = email
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                .verifyWith(getPublicSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getPublicSigningKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_KEY));
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        Instant now = Instant.now();
        return Jwts
                .builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(1, ChronoUnit.HOURS)))
                .signWith(getPublicSigningKey(), Jwts.SIG.HS256)
                .compact();
    }

    // Generating a token from only the UserDetails
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String userEmail = extractUsername(token);
        return (userEmail.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }


    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration)
                .before(new Date());
    }


}
