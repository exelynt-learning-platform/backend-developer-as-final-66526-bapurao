package com.example.booking.security;

import io.jsonwebtoken.*; import io.jsonwebtoken.security.Keys; import org.springframework.beans.factory.annotation.Value; import org.springframework.security.core.userdetails.UserDetails; import org.springframework.stereotype.Service;
import javax.crypto.SecretKey; import java.nio.charset.StandardCharsets; import java.util.Date; import java.util.function.Function;

@Service
public class JwtService {
 private final SecretKey key; private final long expiration;
 public JwtService(@Value("${app.jwt.secret}") String secret,@Value("${app.jwt.expiration-ms}") long expiration){if(secret.getBytes(StandardCharsets.UTF_8).length<32) throw new IllegalArgumentException("JWT secret must be at least 32 bytes"); this.key=Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));this.expiration=expiration;}
 public String generateToken(UserDetails user){Date now=new Date();return Jwts.builder().subject(user.getUsername()).claim("role",user.getAuthorities().stream().findFirst().map(a->a.getAuthority().replace("ROLE_","")).orElse("USER")).issuedAt(now).expiration(new Date(now.getTime()+expiration)).signWith(key).compact();}
 public String extractUsername(String token){return extract(token,Claims::getSubject);}
 public boolean isValid(String token,UserDetails user){try{return extractUsername(token).equals(user.getUsername()) && !extract(token,Claims::getExpiration).before(new Date());}catch(JwtException|IllegalArgumentException e){return false;}}
 private <T>T extract(String token,Function<Claims,T> f){return f.apply(Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload());}
 public long getExpiration(){return expiration;}
}
