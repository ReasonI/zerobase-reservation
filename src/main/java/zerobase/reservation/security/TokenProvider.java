package zerobase.reservation.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import zerobase.reservation.service.PartnerService;
import zerobase.reservation.service.UserService;

import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TokenProvider {

    //identify로 user, partner 식별
    private static final long TOKEN_EXPIRE_TIME = 1000 * 60 * 60; // 1hour
    private static final String KEY_ROLES = "roles";

    private final UserService userService;
    private final PartnerService partnerService;

    @Value("{spring.jwt.secret}")
    private String secretKey;

    /**토큰 생성(발급)
     *
     * @param username
     * @param roles
     * @return
     */
    public String generateToken(String identify, String username, List<String> roles) {
        //사용자의 권한 정보 저장
        Claims claims = Jwts.claims().setSubject(username).setIssuer(identify);
        claims.put(KEY_ROLES, roles);

        var now = new Date();
        var expierdDate = new Date(now.getTime() + TOKEN_EXPIRE_TIME);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now) // 토큰 생성 시간
                .setExpiration(expierdDate) // 토큰 만료 시간
                .signWith(SignatureAlgorithm.HS512, this.secretKey) // 사용할 암호화 알고리즘, 비밀키
                .compact();
    }

    public Authentication getAuthentication(String jwt) {

        UserDetails userDetails = this.userService.loadUserByUsername(this.getUsername(jwt));

        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    // identify로 식별하는 getPartnerAuth 추가
    public Authentication getPartnerAuthentication(String jwt) {
        System.out.println(this.getUsername(jwt));
        UserDetails userDetails =this.partnerService.loadUserByUsername(this.getUsername(jwt));

        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }


    public String getIdentify(String token) {
        return this.parseClaims(token).getIssuer();
    }

    public String getUsername(String token) {
        return this.parseClaims(token).getSubject();
    }
    public boolean validateToken(String token) {
        if(!StringUtils.hasText(token)) return false;

        var claims = this.parseClaims(token);

        return !claims.getExpiration().before(new Date());
    }
    private Claims parseClaims(String token) {
        try{
            return Jwts.parser().setSigningKey(this.secretKey).parseClaimsJws(token).getBody();
        }catch(ExpiredJwtException e){
            return e.getClaims();
        }
    }
}
