package com.almerys.columbia.api.security;

import com.almerys.columbia.api.ColumbiaConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

  private final AuthenticationController restAuthenticationEntryPoint;

  private ColumbiaConfiguration columbiaConfiguration;

  public SecurityConfiguration(AuthenticationController restAuthenticationEntryPoint,
      ColumbiaConfiguration columbiaConfiguration) {
    this.restAuthenticationEntryPoint = restAuthenticationEntryPoint;
    this.columbiaConfiguration = columbiaConfiguration;
  }


  @Bean
  CorsConfigurationSource corsConfigurationSource() {
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    CorsConfiguration corsConfiguration = new CorsConfiguration().applyPermitDefaultValues();
    corsConfiguration.addAllowedMethod(HttpMethod.GET);
    corsConfiguration.addAllowedMethod(HttpMethod.POST);
    corsConfiguration.addAllowedMethod(HttpMethod.PUT);
    corsConfiguration.addAllowedMethod(HttpMethod.DELETE);
    corsConfiguration.addExposedHeader("Authorization");

    source.registerCorsConfiguration("/**", corsConfiguration);
    return source;
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {

    http.cors()
        .and()
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
        .csrf()
        .disable()
        .exceptionHandling()
        .authenticationEntryPoint(restAuthenticationEntryPoint)
        .and()
        .authorizeRequests()
        //Définitions
        .antMatchers(HttpMethod.POST, "/contexts/{^[\\\\d+]$}/terms**")
        .hasAnyAuthority(columbiaConfiguration.getAdminRoleName(), columbiaConfiguration.getModeratorRoleName())
        .antMatchers(HttpMethod.PUT, "/contexts/{^[\\\\d+]$}/terms/**")
        .hasAnyAuthority(columbiaConfiguration.getAdminRoleName(), columbiaConfiguration.getModeratorRoleName())
        .antMatchers(HttpMethod.DELETE, "/contexts/{^[\\\\d+]$}/terms/**")
        .hasAnyAuthority(columbiaConfiguration.getAdminRoleName(), columbiaConfiguration.getModeratorRoleName())

        .antMatchers(HttpMethod.POST, "/terms/{^[\\\\d+]$}/contexts**")
        .hasAnyAuthority(columbiaConfiguration.getAdminRoleName(), columbiaConfiguration.getModeratorRoleName())
        .antMatchers(HttpMethod.PUT, "/terms/{^[\\\\d+]$}/contexts/**")
        .hasAnyAuthority(columbiaConfiguration.getAdminRoleName(), columbiaConfiguration.getModeratorRoleName())
        .antMatchers(HttpMethod.DELETE, "/terms/{^[\\\\d+]$}/contexts/**")
        .hasAnyAuthority(columbiaConfiguration.getAdminRoleName(), columbiaConfiguration.getModeratorRoleName())

        //Termes
        .antMatchers(HttpMethod.POST, "/terms/**")
        .hasAnyAuthority(columbiaConfiguration.getAdminRoleName(), columbiaConfiguration.getModeratorRoleName())
        .antMatchers(HttpMethod.PUT, "/terms/**")
        .hasAnyAuthority(columbiaConfiguration.getAdminRoleName())
        .antMatchers(HttpMethod.DELETE, "/terms/**")
        .hasAnyAuthority(columbiaConfiguration.getAdminRoleName())

        //Contextes
        .antMatchers(HttpMethod.POST, "/contexts/**")
        .hasAnyAuthority(columbiaConfiguration.getAdminRoleName())
        .antMatchers(HttpMethod.PUT, "/contexts/**")
        .hasAnyAuthority(columbiaConfiguration.getAdminRoleName())
        .antMatchers(HttpMethod.DELETE, "/contexts/**")
        .hasAnyAuthority(columbiaConfiguration.getAdminRoleName())

        //Utilisateur
        .antMatchers(HttpMethod.GET, "/users/**")
        .authenticated()
        .antMatchers(HttpMethod.PUT, "/users/{^[\\\\S+]$}/activate/**")
        .permitAll()
        .antMatchers(HttpMethod.PUT, "/users/**")
        .authenticated()

        .antMatchers(HttpMethod.DELETE, "/users/**")
        .hasAnyAuthority(columbiaConfiguration.getAdminRoleName())

        .antMatchers(HttpMethod.GET, "**")
        .permitAll()
        .and()
        .addFilter(new TokenBasedAuthorizationFilter(authenticationManager()));
  }

  @Bean
  public PasswordEncoder encoder() {
    return new BCryptPasswordEncoder(columbiaConfiguration.getCryptPower());
  }
}
