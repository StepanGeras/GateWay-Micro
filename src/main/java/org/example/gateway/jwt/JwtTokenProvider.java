package org.example.gateway.jwt;

import io.jsonwebtoken.*;
import jakarta.servlet.http.HttpServletRequest;
import org.example.gateway.dto.UserValidationRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class JwtTokenProvider {

    @Value("${jwt.token.secret}")
    private String jwtSecret;

    @Value("${jwt.token.expired}")
    private long jwtExpirationInMs;

    public String generateToken(String username, String password, Set<UserValidationRequest.Role> roles) {
        Claims claims = Jwts.claims().setSubject(username);
        claims.put("password", password);
        claims.put("roles", getUserRoleNamesFromJWT(roles));

        Date now = new Date();
        Date validity = new Date(now.getTime() + jwtExpirationInMs);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, jwtSecret)
                .compact();
    }

    public String resolveToken(HttpServletRequest req) {
        String bearerToken = req.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);

            return !claims.getBody().getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String getUserUsernameFromJWT(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
    }

    public Set<UserValidationRequest.Role> getUserRolesFromJWT(String token) {
        List<String> roles = (List<String>) Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().get("roles");
        return getUserRoleNamesFromJWT(roles);
    }

    private Set<String> getUserRoleNamesFromJWT(Set<UserValidationRequest.Role> roles) {
        Set<String> result = new HashSet<>();
        roles.forEach(role -> result.add(role.getAuthority()));
        return result;
    }

    private Set<UserValidationRequest.Role> getUserRoleNamesFromJWT(List<String> roles) {
        Set<UserValidationRequest.Role> result = new HashSet<>();
        roles.forEach(s -> result.add(UserValidationRequest.Role.valueOf(s)));
        return result;
    }

    public String getUserPasswordFromJWT(String token) {
        return (String) Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().get("password");
    }

    public Authentication getAuthentication(String token) {
        UserValidationRequest user = new UserValidationRequest();
        user.setUsername(getUserUsernameFromJWT(token));
        user.setPassword(getUserPasswordFromJWT(token));
        user.setRoles(getUserRolesFromJWT(token));
        return new UsernamePasswordAuthenticationToken(user, token, user.getRoles());
    }
}
