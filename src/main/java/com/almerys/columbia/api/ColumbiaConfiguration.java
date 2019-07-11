package com.almerys.columbia.api;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("columbia")
public class ColumbiaConfiguration {
  private static final String ADMINROLE = "ADMIN";
  private static final String MODERATORROLE = "GLOSSATEUR";
  private static final String USERROLE = "UTILISATEUR";

  private Integer maxContextLevel;

  private Integer cryptPower;

  private String cryptPassword;

  private String frontURL;

  private Integer tokenLifetime;

  private String tokenPrefix;

  private String tokenSecret;

  private Boolean delegatedAuthentication;

  private String oauth2UserinfoUrl;

  private Boolean enforceHttps;

  private Boolean openRegistration;

  public Boolean getEnforceHttps() {
    return enforceHttps;
  }

  public void setEnforceHttps(Boolean enforceHttps) {
    this.enforceHttps = enforceHttps;
  }

  public Boolean getOpenRegistration() {
    return (openRegistration == null) ? Boolean.FALSE : openRegistration;
  }

  public void setOpenRegistration(Boolean openRegistration) {
    this.openRegistration = openRegistration;
  }

  public Boolean getDelegatedAuthentication() {
    return (delegatedAuthentication == null) ? Boolean.FALSE : delegatedAuthentication;
  }

  public void setDelegatedAuthentication(Boolean delegatedAuthentication) {
    this.delegatedAuthentication = delegatedAuthentication;
  }

  public String getOauth2UserinfoUrl() {
    return oauth2UserinfoUrl;
  }

  public void setOauth2UserinfoUrl(String oauth2UserinfoUrl) {
    this.oauth2UserinfoUrl = oauth2UserinfoUrl;
  }

  public Integer getTokenLifetime() {
    return tokenLifetime;
  }

  public void setTokenLifetime(Integer tokenLifetime) {
    this.tokenLifetime = tokenLifetime;
  }

  public String getTokenPrefix() {
    return tokenPrefix;
  }

  public void setTokenPrefix(String tokenPrefix) {
    this.tokenPrefix = tokenPrefix;
  }

  public String getTokenSecret() {
    return tokenSecret;
  }

  public void setTokenSecret(String tokenSecret) {
    this.tokenSecret = tokenSecret;
  }

  public String getFrontURL() {
    return frontURL;
  }

  public void setFrontURL(String frontURL) {
    this.frontURL = frontURL;
  }

  public String getUserRoleName() {
    return USERROLE;
  }


  public String getAdminRoleName() {
    return ADMINROLE;
  }

  public String getModeratorRoleName() {
    return MODERATORROLE;
  }

  public Integer getMaxContextLevel() {
    return maxContextLevel;
  }

  public void setMaxContextLevel(Integer maxContextLevel) {
    this.maxContextLevel = maxContextLevel;
  }

  public Integer getCryptPower() {
    return cryptPower;
  }

  public void setCryptPower(Integer cryptPower) {
    this.cryptPower = cryptPower;
  }

  public String getCryptPassword() {
    return cryptPassword;
  }

  public void setCryptPassword(String cryptPassword) {
    this.cryptPassword = cryptPassword;
  }
}
