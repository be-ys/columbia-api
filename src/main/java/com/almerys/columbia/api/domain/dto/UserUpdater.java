package com.almerys.columbia.api.domain.dto;

import java.util.Collection;
import java.util.Date;

public class UserUpdater {

  private String id;

  private String username;

  private String email;

  private String password;

  private String role;

  private Collection<ContextUpdater> grantedContexts;

  private Date lastLogin;

  private String domain;

  private Boolean isActiv;

  private String activationKey;


  public UserUpdater() {
    //Ignoré
  }

  public Boolean getActiv() {
    return isActiv;
  }

  public void setActiv(Boolean activ) {
    isActiv = activ;
  }

  public String getActivationKey() {
    return activationKey;
  }

  public void setActivationKey(String activationKey) {
    this.activationKey = activationKey;
  }

  public String getDomain() {
    return domain;
  }

  public void setDomain(String domain) {
    this.domain = domain;
  }

  public Date getLastLogin() {
    return lastLogin;
  }

  public void setLastLogin(Date lastLogin) {
    this.lastLogin = lastLogin;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getRole() {
    return role;
  }

  public void setRole(String role) {
    this.role = role;
  }

  public Collection<ContextUpdater> getGrantedContexts() {
    return grantedContexts;
  }

  public void setGrantedContexts(Collection<ContextUpdater> grantedContexts) {
    this.grantedContexts = grantedContexts;
  }
}
