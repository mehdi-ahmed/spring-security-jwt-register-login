package com.mytutorials.spring.springdockerjwt.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
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

    public static final String SECRET_KEY = "925574ABF652E9BEABC7D8550942C7A0A70A79D546FFCF42A84DCD9568ABB14D";


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
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {
        // byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        //  return Keys.hmacShaKeyFor(keyBytes);
        return Jwts.SIG.HS256.key().build();
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        Instant now = Instant.now();
        return Jwts
                .builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(1, ChronoUnit.HOURS)))
                .signWith(getSigningKey(), Jwts.SIG.HS256)
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
