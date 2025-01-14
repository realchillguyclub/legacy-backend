package server.poptato.auth.application.service;


import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import server.poptato.auth.exception.AuthException;
import server.poptato.global.dto.TokenPair;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static server.poptato.auth.exception.errorcode.AuthExceptionErrorCode.INVALID_TOKEN;
import static server.poptato.auth.exception.errorcode.AuthExceptionErrorCode.TOKEN_TIME_EXPIRED;


@Service
@RequiredArgsConstructor
public class JwtService {
    @Value("${jwt.secret}")
    private String jwtSecret;
    private static final String USER_ID = "USER_ID";
    private static final String ACCESS_TOKEN = "ACCESS_TOKEN";
    private static final String REFRESH_TOKEN = "REFRESH_TOKEN";
    public static final int MINUTE_IN_MILLISECONDS = 60 * 1000;
    public static final long DAYS_IN_MILLISECONDS = 24 * 60 * 60 * 1000L;
    public static final int ACCESS_TOKEN_EXPIRATION_MINUTE = 20;
    public static final int REFRESH_TOKEN_EXPIRATION_DAYS = 14;
    private final RedisTemplate<String, String> redisTemplate;

    @PostConstruct
    protected void init() {
        jwtSecret = Base64.getEncoder()
                .encodeToString(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    public String createAccessToken(final String userId) {
        final Claims claims = getAccessTokenClaims();

        claims.put(USER_ID, userId);
        return createToken(claims);
    }

    public String createRefreshToken(final String userId) {
        final Claims claims = getRefreshTokenClaims();

        claims.put(USER_ID, userId);
        return createToken(claims);
    }

    public void verifyToken(final String token) {
        try {
            final Claims claims = getBody(token);
        } catch (ExpiredJwtException e) {
            throw new AuthException(TOKEN_TIME_EXPIRED);
        } catch (UnsupportedJwtException | SignatureException | MalformedJwtException  e){
            throw new AuthException(INVALID_TOKEN);
        } catch (RuntimeException e) {
            throw e;
        }
    }

    public String getUserIdInToken(final String token) {
        final Claims claims = getBody(token);
        return (String) claims.get(USER_ID);
    }

    public TokenPair generateTokenPair(final String userId) {
        final String accessToken = createAccessToken(userId);
        final String refreshToken = createRefreshToken(userId);
        saveRefreshToken(userId, refreshToken);
        return new TokenPair(accessToken, refreshToken);
    }

    public void compareRefreshToken(final String userId, final String refreshToken) {
        final String storedRefreshToken = redisTemplate.opsForValue().get(userId);
        if (storedRefreshToken == null || !storedRefreshToken.equals(refreshToken))
            throw new AuthException(INVALID_TOKEN);
    }

    public void saveRefreshToken(final String userId, final String refreshToken) {
        redisTemplate.opsForValue().set(userId, refreshToken, REFRESH_TOKEN_EXPIRATION_DAYS, TimeUnit.DAYS);
    }

    private String createToken(final Claims claims) {
        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setClaims(claims)
                .signWith(getSigningKey())
                .compact();
    }

    private Claims getRefreshTokenClaims() {
        final Date now = new Date();
        return Jwts.claims()
                .setSubject(REFRESH_TOKEN)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + REFRESH_TOKEN_EXPIRATION_DAYS * DAYS_IN_MILLISECONDS));
    }

    private Claims getAccessTokenClaims() {
        final Date now = new Date();
        return Jwts.claims()
                .setSubject(ACCESS_TOKEN)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + ACCESS_TOKEN_EXPIRATION_MINUTE * MINUTE_IN_MILLISECONDS));
    }

    private Claims getBody(final String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSigningKey() {
        final byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public void deleteRefreshToken(final String userId) {
        redisTemplate.delete(userId);
    }
}
