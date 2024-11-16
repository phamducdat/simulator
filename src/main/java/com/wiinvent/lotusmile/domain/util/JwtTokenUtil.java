package com.wiinvent.lotusmile.domain.util;

import com.wiinvent.lotusmile.domain.entity.User;
import com.wiinvent.lotusmile.domain.entity.types.MerchantAccountRole;
import com.wiinvent.lotusmile.domain.entity.types.MerchantAccountState;
import com.wiinvent.lotusmile.domain.exception.ErrorMessage;
import com.wiinvent.lotusmile.domain.exception.UnAuthenticationException;
import com.wiinvent.lotusmile.domain.pojo.MerchantTokenInfo;
import com.wiinvent.lotusmile.domain.security.UserTokenInfo;
import io.jsonwebtoken.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Log4j2
public class JwtTokenUtil implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;
  @Value("${jwt.user-private-key}")
  private String userPrivateKey;

  @Value("${jwt.secret-merchant}")
  private String jwtSecretMerchant;

  @Value("${jwt.expire-time-merchant}")
  private Long jwtExpirationMerchantMs;

  @Value("${jwt.merchant-refresh-token-expired-ms}")
  private long refreshTokenMerchantValidityInMilliseconds;

  @Value("${jwt.access-token-expired-ms}") //1 day
  private long accessTokenValidityInMilliseconds;

  @Value("${jwt.refresh-token-expired-ms:2592000000}") // 30 days
  private long refreshTokenValidityInMilliseconds;

  public long getExpiredRefreshToken() {
    return refreshTokenValidityInMilliseconds;
  }

  public UserTokenInfo validateUserToken(String accessToken) {
    Claims claims = getAllUserClaimsFromToken(accessToken);

    Date expiration = claims.getExpiration();
    if (expiration.before(new Date())) {
      return null;
    }

    UserTokenInfo userTokenInfo = new UserTokenInfo();
    userTokenInfo.setUserId(claims.get("userId", Integer.class));
    return userTokenInfo;
  }

  public String createAccessToken(User user) {
    return createToken(user, accessTokenValidityInMilliseconds);
  }

  public String createRefreshToken(User user) {
    return createToken(user, refreshTokenValidityInMilliseconds);
  }

  private String createToken(User userSystem, long validity) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("sub", String.valueOf(userSystem.getId()));
    claims.put("customerId", userSystem.getCustomerId());
    claims.put("firstName", userSystem.getFirstName());
    claims.put("lastName", userSystem.getLastName());
    claims.put("status", userSystem.getState());
    claims.put("userId", userSystem.getId());
    return Jwts.builder()
        .setClaims(claims)
        .setExpiration(new Date(System.currentTimeMillis() + validity))
        .signWith(SignatureAlgorithm.HS512, userPrivateKey)
        .compact();
  }

  public Integer getUserId(String token) {
    return Integer.valueOf(getAllUserClaimsFromToken(token).getSubject());
  }

  private Claims getAllUserClaimsFromToken(String token) {
    try {
      return Jwts.parser().setSigningKey(userPrivateKey).parseClaimsJws(token).getBody();
    } catch (Exception e) {
      throw new UnAuthenticationException(ErrorMessage.UNAUTHORIZED);
    }
  }

  //merchant
  public String generateTokenMerchant(MerchantTokenInfo tokenInfo) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("role", tokenInfo.getRole().toString());
    claims.put("state", tokenInfo.getState().toString());
    claims.put("merchantId", tokenInfo.getMerchantId());
    claims.put("username", tokenInfo.getUserName());

    return Jwts.builder()
        .setClaims(claims)
        .setSubject(String.valueOf(tokenInfo.getUserId()))
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMerchantMs * 1000))
        .signWith(SignatureAlgorithm.HS512, jwtSecretMerchant)
        .compact();
  }

  public MerchantTokenInfo validateTokenMerchant(String authToken) {
    try {
      MerchantTokenInfo tokenInfo = new MerchantTokenInfo();
      Claims claims = Jwts.parser().setSigningKey(jwtSecretMerchant).parseClaimsJws(authToken).getBody();
      tokenInfo.setUserId(Integer.parseInt(claims.get(Claims.SUBJECT, String.class)));
      tokenInfo.setMerchantId(claims.get("merchantId", Integer.class));
      tokenInfo.setRole(MerchantAccountRole.valueOf(claims.get("role", String.class)));
      tokenInfo.setState(MerchantAccountState.valueOf(claims.get("state", String.class)));
      tokenInfo.setUserName(claims.get("username", String.class));
      return tokenInfo;
    } catch (MalformedJwtException ex) {
      log.error("Invalid JWT token");
    } catch (ExpiredJwtException ex) {
      log.error("Expired JWT token");
    } catch (UnsupportedJwtException ex) {
      log.error("Unsupported JWT token");
    } catch (IllegalArgumentException ex) {
      log.error("JWT claims string is empty.");
    }
    return null;
  }

  public long getExpiredRefreshTokenMerchant() {
    return refreshTokenMerchantValidityInMilliseconds;
  }

  public String createRefreshToken(String username, int userId, int teamId, MerchantAccountRole role) {
    List<MerchantAccountRole> roles = List.of(role);
    return createToken(username, userId, teamId, roles, refreshTokenValidityInMilliseconds);
  }

  private String createToken(String username, int userId, int teamId, List<MerchantAccountRole> roles, long validity) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("sub", username);
    claims.put("roles", roles);
    claims.put("userId", userId);
    claims.put("teamId", teamId);

    Date now = new Date();
    Date expirationDate = new Date(now.getTime() + validity);

    return Jwts.builder()
        .setClaims(claims)
        .setSubject(username)
        .setIssuedAt(now)
        .setExpiration(expirationDate)
        .signWith(SignatureAlgorithm.HS512, jwtSecretMerchant)
        .compact();
  }

  public String getUsernameMerchant(String token) {
    Claims claims = Jwts.parser().setSigningKey(jwtSecretMerchant).parseClaimsJws(token).getBody();
    return claims.getSubject();
  }

}
