package com.almerys.columbia.api.security;

import com.almerys.columbia.api.domain.ColumbiaUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class CustomUserDetails implements UserDetails {
  private ColumbiaUser columbiaUser;
  private String id;

  public CustomUserDetails(ColumbiaUser columbiaUser) {
    this.columbiaUser = columbiaUser;
    this.id = columbiaUser.getId();
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    List<GrantedAuthority> authorities = new ArrayList<>();
    authorities.add(new SimpleGrantedAuthority(columbiaUser.getRole()));
    columbiaUser.getGrantedContexts().forEach(e -> authorities.add(new SimpleGrantedAuthority("CONTEXT_" + e.getId())));

    return authorities;
  }

  public String getId() {
    return id;
  }

  @Override
  public String getPassword() {
    return columbiaUser.getPassword();
  }

  @Override
  public String getUsername() {
    return columbiaUser.getUsername();
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}
