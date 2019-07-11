package com.almerys.columbia.api.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.validation.constraints.NotBlank;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.UUID;

@Entity
public class ColumbiaUser {

  @Id
  @JsonView(View.MinimalDisplay.class)
  private String id = UUID.randomUUID().toString();

  @Column(unique = true)
  @NotBlank(message = "Username cannot be blank")
  @JsonView(View.MinimalDisplay.class)
  private String username;

  @Column(unique = true)
  @JsonView(View.DefaultDisplay.class)
  private String email;

  @JsonIgnore
  private String password;

  @JsonView(View.MinimalDisplay.class)
  @NotBlank(message = "Role could not be null.")
  private String role;

  @ManyToMany(fetch = FetchType.EAGER)
  @JsonIgnoreProperties({ "description" })
  @JsonView(View.DefaultDisplay.class)
  private Collection<ColumbiaContext> grantedContexts = new HashSet<>();

  @JsonView(View.MinimalDisplay.class)
  private String domain;

  @JsonView(View.DefaultDisplay.class)
  private Date lastLogin;

  @JsonView(View.DefaultDisplay.class)
  private Boolean isActiv;

  @JsonIgnore
  private String activationKey;

  public ColumbiaUser() {
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
    return this.email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public String getRole() {
    return role;
  }

  public void setRole(String role) {
    this.role = role;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public Collection<ColumbiaContext> getGrantedContexts() {
    return grantedContexts;
  }

  public void setGrantedContexts(Collection<ColumbiaContext> grantedContexts) {
    this.grantedContexts = (grantedContexts == null) ? new HashSet<>() : grantedContexts;
  }
}