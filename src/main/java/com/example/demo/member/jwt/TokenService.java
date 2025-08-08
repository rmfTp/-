package com.example.demo.member.jwt;

import com.example.demo.global.exceptions.UnAuthorizedException;
import com.example.demo.global.libs.Utils;
import com.example.demo.member.MemberInfo;
import com.example.demo.member.authorities.Authority;
import com.example.demo.member.entities.Member;
import com.example.demo.member.services.MemberInfoService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Date;
import java.util.List;

@Lazy
@Service
@EnableConfigurationProperties(JwtProperties.class)
public class TokenService {
    private final JwtProperties properties;
    private final MemberInfoService infoService;
    @Autowired
    private Utils utils;

    private Key key;

    public TokenService(JwtProperties properties, MemberInfoService infoService) {
        this.properties = properties;
        this.infoService = infoService;

        byte[] keyBytes = Decoders.BASE64.decode(properties.getSecret());
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     *  JWT 토큰 발급
      * @param email
     * @return
     */
    public String create(String email) {
        MemberInfo userDetails = (MemberInfo) infoService.loadUserByUsername(email);
        Member member = userDetails.getMember();

        Date date = new Date(new Date().getTime() + properties.getValidTime() * 1000);

        return Jwts.builder()
                .setSubject(member.getEmail())
                .claim("authority", member.getAuthority())
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(date)
                .compact();
    }

    /**
     * JWT 토큰으로 인증 처리
     * @param token
     * @return
     */
    public Authentication authenticate(String token) {
        validate(token);
        Claims claims = Jwts.parser()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getPayload();
        String email = claims.getSubject();
        Authority authority = Authority.valueOf((String) claims.get("authority"));
        MemberInfo userDetails = (MemberInfo) infoService.loadUserByUsername(email);
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(authority.name()));
        userDetails.getMember().setAuthority(authority);

        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return authentication;
    }

    /**
     * 요청헤더
     * Authorization: Brarer JWT 토큰
     * @param request
     * @return
     */
    public Authentication authenticate(ServletRequest request) {
        HttpServletRequest req = (HttpServletRequest) request;
        String token = req.getHeader("Authorization");
        if (!StringUtils.hasText(token)) return null;
        token = token.substring(7);
        if (!StringUtils.hasText(token)) return null;
        return authenticate(token);
    }

    /**
     * 토큰 유효성 검사
     * @param token
     */
    public void validate(String token) {
        String errorCode = null;
        Exception error = null;
        try {
            Jwts.parser().setSigningKey(key).build().parseClaimsJws(token).getPayload();
        } catch (ExpiredJwtException e){
            errorCode = "JWT.expired";
            error = e;
        } catch (MalformedJwtException | SecurityException e) {
            errorCode = "JWT.malformed";
            error = e;
        } catch (UnsupportedJwtException e) {
            errorCode = "JWT.unsupported";
            error = e;
        } catch (Exception e) {
            errorCode = "JWT.error";
            error = e;
        }
        if (StringUtils.hasText(errorCode)) throw new UnAuthorizedException(utils.getMessage(errorCode));
        if (error != null) error.printStackTrace();
    }
}
