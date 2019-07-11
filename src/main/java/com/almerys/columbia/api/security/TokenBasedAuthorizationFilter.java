package com.almerys.columbia.api.security;

import com.almerys.columbia.api.ColumbiaConfiguration;
import com.almerys.columbia.api.services.ApplicationContextService;
import io.jsonwebtoken.Jwts;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;

public class TokenBasedAuthorizationFilter extends BasicAuthenticationFilter {
  TokenBasedAuthorizationFilter(AuthenticationManager authenticationManager) {
    super(authenticationManager);
  }

  private ColumbiaConfiguration columbiaConfiguration = ApplicationContextService.getColumbiaConfiguration();


  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    String authorizationToken = request.getHeader(HttpHeaders.AUTHORIZATION);
    if (authorizationToken != null && authorizationToken.startsWith(columbiaConfiguration.getTokenPrefix())) {
      authorizationToken = authorizationToken.replaceFirst(columbiaConfiguration.getTokenPrefix(), "");
      String username = Jwts.parser()
                            .setSigningKey(columbiaConfiguration.getTokenSecret())
                            .parseClaimsJws(authorizationToken)
                            .getBody()
                            .getSubject();

      Collection<String> test = (Collection<String>) Jwts.parser()
                                                         .setSigningKey(columbiaConfiguration.getTokenSecret())
                                                         .parseClaimsJws(authorizationToken)
                                                         .getBody()
                                                         .get("rights");

      Collection<GrantedAuthority> grantedAuthorities = new HashSet<>();
      test.forEach(e -> grantedAuthorities.add(new SimpleGrantedAuthority(e)));

      SecurityContextHolder.getContext()
                           .setAuthentication(new UsernamePasswordAuthenticationToken(username, Jwts.parser()
                                                                                                    .setSigningKey(columbiaConfiguration.getTokenSecret())
                                                                                                    .parseClaimsJws(authorizationToken)
                                                                                                    .getBody()
                                                                                                    .getId(), grantedAuthorities));
    }
    chain.doFilter(request, response);
  }
}