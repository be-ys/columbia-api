package com.almerys.columbia.api.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.FetchType;
import javax.persistence.Id;
import java.util.Collection;
import java.util.HashSet;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
public class ColumbiaNewsletter {

  @Id
  private String email;

  @ManyToMany(fetch = FetchType.EAGER)
  @JsonIgnoreProperties({ "description", "parentContext", "termList" })
  private Collection<ColumbiaContext> subscribedContexts;

  @JsonIgnore
  private String token;

  public ColumbiaNewsletter() {
    //Ignoré
  }

  public ColumbiaNewsletter(String email) {
    this.email = email;
  }

  public ColumbiaNewsletter(String email, Collection<ColumbiaContext> subscribedContexts) {
    this.subscribedContexts = (subscribedContexts == null) ? new HashSet<>() : subscribedContexts;
    this.email = email;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public String getEmail() {
    return this.email;
  }

  public Collection<ColumbiaContext> getSubscribedContexts() {
    return this.subscribedContexts;
  }

  public String getToken() {
    return this.token;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public void setSubscribedContexts(Collection<ColumbiaContext> columbiaContexts) {
    this.subscribedContexts = (columbiaContexts != null) ? columbiaContexts : new HashSet<>();
  }

}
