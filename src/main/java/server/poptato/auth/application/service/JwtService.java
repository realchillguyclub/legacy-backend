package server.poptato.auth.application.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import io.lettuce.core.RedisCommandTimeoutException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import server.poptato.auth.status.AuthErrorStatus;
import server.poptato.global.dto.TokenPair;
import server.poptato.global.exception.CustomException;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.concurrent.TimeUnit;


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

    /**
     * JWT 비밀키를 Base64로 인코딩합니다.
     * 이 메서드는 클래스 초기화 시 실행됩니다.
     */
    @PostConstruct
    protected void init() {
        jwtSecret = Base64.getEncoder()
                .encodeToString(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 액세스 토큰을 생성합니다.
     *
     * @param userId 토큰에 포함할 유저 ID
     * @return 생성된 액세스 토큰
     */
    public String createAccessToken(final String userId) {
        final Claims claims = getAccessTokenClaims();
        claims.put(USER_ID, userId);
        return createToken(claims);
    }

    /**
     * 리프레시 토큰을 생성합니다.
     *
     * @param userId 토큰에 포함할 유저 ID
     * @return 생성된 리프레시 토큰
     */
    public String createRefreshToken(final String userId) {
        final Claims claims = getRefreshTokenClaims();
        claims.put(USER_ID, userId);
        return createToken(claims);
    }

    /**
     * 액세스 토큰의 유효성을 검증합니다.
     * 유효하지 않거나 만료된 토큰인 경우 예외를 발생시킵니다.
     *
     * @param token 검증할 액세스 토큰
     * @throws CustomException 액세스 토큰이 유효하지 않거나 만료된 경우
     */
    public void verifyAccessToken(final String token) {
        try {
            final Claims claims = getBody(token);
        } catch (ExpiredJwtException e) {
            throw new CustomException(AuthErrorStatus._EXPIRED_ACCESS_TOKEN);
        } catch (UnsupportedJwtException | SignatureException | MalformedJwtException e) {
            throw new CustomException(AuthErrorStatus._INVALID_ACCESS_TOKEN);
        } catch (RuntimeException e) {
            throw e;
        }
    }

    /**
     * 리프레쉬 토큰의 유효성을 검증합니다.
     * 유효하지 않거나 만료된 토큰인 경우 예외를 발생시킵니다.
     *
     * @param token 검증할 리프레쉬 토큰
     * @throws CustomException 리프레쉬 토큰이 유효하지 않거나 만료된 경우
     */
    public void verifyRefreshToken(final String token) {
        try {
            final Claims claims = getBody(token);
        } catch (ExpiredJwtException e) {
            throw new CustomException(AuthErrorStatus._EXPIRED_REFRESH_TOKEN);
        } catch (UnsupportedJwtException | SignatureException | MalformedJwtException e) {
            throw new CustomException(AuthErrorStatus._INVALID_REFRESH_TOKEN);
        } catch (RuntimeException e) {
            throw e;
        }
    }

    /**
     * 토큰에서 유저 ID를 추출합니다.
     *
     * @param token JWT 토큰
     * @return 토큰에 포함된 유저 ID
     */
    public String getUserIdInToken(final String token) {
        final Claims claims = getBody(token);
        return (String) claims.get(USER_ID);
    }

    /**
     * 액세스 토큰과 리프레시 토큰으로 구성된 토큰 페어를 생성합니다.
     * 생성된 리프레시 토큰은 Redis에 저장됩니다.
     *
     * @param userId 유저 ID
     * @return 생성된 토큰 페어 (액세스 토큰, 리프레시 토큰)
     */
    public TokenPair generateTokenPair(final String userId) {
        final String accessToken = createAccessToken(userId);
        final String refreshToken = createRefreshToken(userId);
        saveRefreshToken(userId, refreshToken);
        return new TokenPair(accessToken, refreshToken);
    }

    /**
     * Redis에 저장된 리프레시 토큰과 입력받은 리프레시 토큰을 비교합니다.
     *
     * @param userId 유저 ID
     * @param refreshToken 입력받은 리프레시 토큰
     * @throws CustomException 저장된 리프레시 토큰과 일치하지 않을 경우
     */
    public void compareRefreshToken(final String userId, final String refreshToken) {
        try {
            final String storedRefreshToken = redisTemplate.opsForValue().get(userId);

            if (storedRefreshToken == null) {
                throw new CustomException(AuthErrorStatus._EXPIRED_OR_NOT_FOUND_REFRESH_TOKEN_IN_REDIS);
            }

            if (!storedRefreshToken.equals(refreshToken)) {
                throw new CustomException(AuthErrorStatus._DIFFERENT_REFRESH_TOKEN);
            }
        } catch (RedisConnectionFailureException | RedisCommandTimeoutException e) {
            throw new CustomException(AuthErrorStatus._REDIS_UNAVAILABLE);
        }
    }

    /**
     * 리프레시 토큰을 Redis에 저장합니다.
     *
     * @param userId 유저 ID
     * @param refreshToken 저장할 리프레시 토큰
     */
    public void saveRefreshToken(final String userId, final String refreshToken) {
        redisTemplate.opsForValue().set(userId, refreshToken, REFRESH_TOKEN_EXPIRATION_DAYS, TimeUnit.DAYS);
    }

    /**
     * Redis에 저장된 리프레시 토큰을 삭제합니다.
     *
     * @param userId 유저 ID
     */
    public void deleteRefreshToken(final String userId) {
        redisTemplate.delete(userId);
    }

    /**
     * JWT 토큰을 생성합니다.
     *
     * @param claims 토큰에 포함할 클레임
     * @return 생성된 JWT 토큰
     */
    private String createToken(final Claims claims) {
        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setClaims(claims)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * 리프레시 토큰 생성 시 사용할 클레임을 반환합니다.
     *
     * @return 리프레시 토큰에 포함할 클레임
     */
    private Claims getRefreshTokenClaims() {
        final Date now = new Date();
        return Jwts.claims()
                .setSubject(REFRESH_TOKEN)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + REFRESH_TOKEN_EXPIRATION_DAYS * DAYS_IN_MILLISECONDS));
    }

    /**
     * 액세스 토큰 생성 시 사용할 클레임을 반환합니다.
     *
     * @return 액세스 토큰에 포함할 클레임
     */
    private Claims getAccessTokenClaims() {
        final Date now = new Date();
        return Jwts.claims()
                .setSubject(ACCESS_TOKEN)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + ACCESS_TOKEN_EXPIRATION_MINUTE * MINUTE_IN_MILLISECONDS));
    }

    /**
     * JWT 토큰의 클레임 정보를 파싱하여 반환합니다.
     *
     * @param token 파싱할 JWT 토큰
     * @return 토큰의 클레임 정보
     */
    private Claims getBody(final String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * JWT 서명에 사용할 키를 반환합니다.
     *
     * @return 서명 키
     */
    private Key getSigningKey() {
        final byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Authorization 헤더에서 사용자 ID를 추출합니다.
     * JWT 토큰을 검증하고, 유효한 경우 토큰에서 사용자 ID를 가져옵니다.
     *
     * @param authorization 요청 헤더의 Authorization (Bearer 토큰)
     * @return 토큰에서 추출된 사용자 ID
     * @throws CustomException 토큰이 없거나 유효하지 않은 경우 예외 발생
     */
    public Long extractUserIdFromToken(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new CustomException(AuthErrorStatus._NOT_EXIST_ACCESS_TOKEN);
        }
        String token = authorization.substring("Bearer ".length());
        verifyAccessToken(token);
        return Long.parseLong(getUserIdInToken(token));
    }
}
