package com.autozone.cazss_backend.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.autozone.cazss_backend.entity.UserEntity;
import com.autozone.cazss_backend.repository.UserRepository;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

  private final String secretKey;
  private final long accessTokenExpirationMs;
  private final long refreshTokenExpirationMs;
  private final UserRepository userRepository;

  public JwtUtil(
      @Value("${jwt.secret}") String secretKey,
      @Value("${jwt.access.token.expiration}") long accessTokenExpirationMs,
      @Value("${jwt.refresh.token.expiration}") long refreshTokenExpirationMs,
      UserRepository userRepository) {
    this.secretKey = secretKey;
    this.accessTokenExpirationMs = accessTokenExpirationMs;
    this.refreshTokenExpirationMs = refreshTokenExpirationMs;
    this.userRepository = userRepository;
  }

  private Algorithm getAlgorithm() {
    return Algorithm.HMAC256(secretKey);
  }

  public String generateAccessToken(UserDetails userDetails) {
    String email = userDetails.getUsername();
    UserEntity userEntity =
        userRepository
            .findByEmail(email)
            .orElseThrow(
                () ->
                    new UsernameNotFoundException(
                        "User not found with email: " + email + " during JWT generation"));

    String role =
        userDetails.getAuthorities().stream()
            .findFirst()
            .map(GrantedAuthority::getAuthority)
            .orElse(null);

    return JWT.create()
        .withSubject(email)
        .withClaim("userId", userEntity.getUserId())
        .withClaim("username", userEntity.getUsername())
        .withClaim("role", role)
        .withIssuedAt(new Date())
        .withExpiresAt(new Date(System.currentTimeMillis() + accessTokenExpirationMs))
        .sign(getAlgorithm());
  }

  public String generateRefreshToken(UserDetails userDetails) {
    String email = userDetails.getUsername();
    UserEntity userEntity =
        userRepository
            .findByEmail(email)
            .orElseThrow(
                () ->
                    new UsernameNotFoundException(
                        "User not found with email: "
                            + email
                            + " during refresh token generation"));

    return JWT.create()
        .withSubject(email)
        .withClaim("userId", userEntity.getUserId())
        .withClaim("username", userEntity.getUsername())
        .withIssuedAt(new Date())
        .withExpiresAt(new Date(System.currentTimeMillis() + refreshTokenExpirationMs))
        .sign(getAlgorithm());
  }

  public DecodedJWT verifyToken(String token) throws JWTVerificationException {
    JWTVerifier verifier = JWT.require(getAlgorithm()).build();
    return verifier.verify(token);
  }

  public String extractUserId(String token) {
    return String.valueOf(verifyToken(token).getClaim("userId").asInt());
  }

  public String extractUsername(String token) {
    DecodedJWT decodedJWT = verifyToken(token);
    if (decodedJWT.getClaim("username").isNull()) {
      return null;
    }
    return decodedJWT.getClaim("username").asString();
  }

  public String extractEmail(String token) {
    return verifyToken(token).getSubject();
  }

  public String extractRole(String token) {
    DecodedJWT decodedJWT = verifyToken(token);
    if (decodedJWT.getClaim("role").isNull()) {
      return null;
    }
    return decodedJWT.getClaim("role").asString();
  }

  public boolean isTokenExpired(String token) {
    try {
      return verifyToken(token).getExpiresAt().before(new Date());
    } catch (JWTVerificationException e) {
      return true;
    }
  }

  public boolean validateToken(String token, UserDetails userDetails) {
    try {
      final String emailFromToken = extractEmail(token);
      return (emailFromToken.equals(userDetails.getUsername()) && !isTokenExpired(token));
    } catch (JWTVerificationException e) {
      return false;
    }
  }
}
