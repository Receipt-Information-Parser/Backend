package cloudComputing.ReceiptMate.base.util;

import cloudComputing.ReceiptMate.auth.enumeration.Token;
import cloudComputing.ReceiptMate.auth.data.TokenInfo;
import cloudComputing.ReceiptMate.auth.dto.response.TokenResponse;
import cloudComputing.ReceiptMate.user.enumeration.Authority;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtUtil implements Serializable {
    @Value("${spring.jwt.secret}")
    private String JWT_SECRET_KEY;

    @Value("${spring.jwt.subject}")
    private String JWT_SUBJECT;

    @Value("${spring.jwt.claims.user-id}")
    private String USER_ID_CLAIM;

    @Value("${spring.jwt.claims.authority}")
    private String USER_AUTHORITY_CLAIM;

    @Value("${spring.jwt.claims.email}")
    private String EMAIL_CLAIM;

    // 토큰 유효시간
    public static final long JWT_TOKEN_EXPIRATION = 30 * 60; // 30분
    public static final long JWT_REFRESH_TOKEN_EXPIRATION = 30 * 24 * 60 * 60; // 30일

    /**
     * 토큰 발급
     * @param tokenInfo token에 포함되는 id, email, authority를 담은 객체
     * @return String
     */
    public TokenResponse generateToken(TokenInfo tokenInfo) {
        String accessToken = generateTokenByType(Token.ACCESS_TOKEN, tokenInfo.getId(), tokenInfo.getEmail(),
            tokenInfo.getAuthority());
        String refreshToken = generateTokenByType(Token.REFRESH_TOKEN, tokenInfo.getId(), tokenInfo.getEmail(),
            tokenInfo.getAuthority());

        log.info("accessToken : " + accessToken);
        log.info("refreshToken : " + refreshToken);

        return new TokenResponse(accessToken, refreshToken);
    }

    /**
     * 리프레시 토큰 발급
     * @param userId long 유저 id
     * @param email String 이메일
     * @param authority String 권한
     * @return String
     */
    public String generateRefreshToken(TokenInfo tokenInfo){
        return generateTokenByType(Token.ACCESS_TOKEN, tokenInfo.getId(), tokenInfo.getEmail(),
            tokenInfo.getAuthority());
    }


    /**
     * 토큰 타입으로 유효기간 설정후 토큰 발급
     * @param userId long 유저 id
     * @param email String 이메일
     * @param authority String 권한
     * @return String
     */
    public String generateTokenByType(Token tokenType, long userId, String email, Authority authority){
        Long expirationSeconds = tokenType.equals(Token.ACCESS_TOKEN)? JWT_TOKEN_EXPIRATION : JWT_REFRESH_TOKEN_EXPIRATION;

        Map<String, Object> claims = new HashMap<>();
        claims.put(USER_ID_CLAIM, userId);
        claims.put(USER_AUTHORITY_CLAIM, authority);
        claims.put(EMAIL_CLAIM, email);

        return Jwts.builder().setHeaderParam("typ", "JWT")
            .setClaims(claims).setSubject(JWT_SUBJECT).setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + expirationSeconds * 1000))
            .signWith(SignatureAlgorithm.HS512, JWT_SECRET_KEY).compact();
    }

    /**
     * 토큰에 저장된 정보(claim) 얻기
     * @param token String
     * @param claimsResolver Function<Claims, T>
     * @return T
     */
    public <T> T getClaimByToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsByToken(token);
        return claimsResolver.apply(claims);
    }

    /**
     * 토큰에 저장된 정보(claim) 얻기
     * @param token String
     * @return Claims
     */
    private Claims getAllClaimsByToken(String token) {
        return Jwts.parser().setSigningKey(JWT_SECRET_KEY).parseClaimsJws(token).getBody();
    }

    /**
     * 토큰 만료일 얻기
     * @param token String
     * @return Date
     */
    public Date getExpirationDateByToken(String token){
        return getClaimByToken(token, Claims::getExpiration);
    }

    /**
     * 토큰값에서 유저 아이디를 가져옴
     * @param token String
     * @return Long
     */
    public Long getUserIdByToken(String token){
        Claims claims = getAllClaimsByToken(token);
        return claims.get(USER_ID_CLAIM, Long.class);
    }

    /**
     * 토큰값에서 권한을 가져옴
     * @param token String
     * @return String
     */
    public String getAuthorityByToken(String token){
        Claims claims = getAllClaimsByToken(token);
        return claims.get(USER_AUTHORITY_CLAIM, String.class);
    }

    /**
     * 토큰값에서 이메일값 가져옴
     * @param token String
     * @return String
     */
    public String getEmailByToken(String token){
        Claims claims = getAllClaimsByToken(token);
        return claims.get(EMAIL_CLAIM, String.class);
    }

    /**
     * 토큰 만료여부 판별
     * @param token String
     * @return boolean
     */
    public boolean isTokenExpired(String token){
        boolean isTokenExpired = false;
        try {
            Date expirationDate = getExpirationDateByToken(token);
            isTokenExpired = expirationDate.before(new Date());
        } catch (ExpiredJwtException e){
            isTokenExpired = true;
        }

        return isTokenExpired;
    }
}
