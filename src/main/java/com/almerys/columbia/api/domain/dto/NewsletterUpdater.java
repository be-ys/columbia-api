package com.almerys.columbia.api.domain.dto;

import javax.validation.constraints.Email;
import java.util.Collection;
import java.util.HashSet;

public class NewsletterUpdater {

  @Email
  private String email;

  private Collection<ContextUpdater> subscribedContexts = new HashSet<>();

  public NewsletterUpdater() {
    //Ignoré
  }

  public NewsletterUpdater(@Email String email, Collection<ContextUpdater> subscribedContexts) {

    this.email = email;

    this.subscribedContexts = (subscribedContexts == null) ? new HashSet<>() : subscribedContexts;
  }

  public String getEmail() {
    return this.email;
  }

  public Collection<ContextUpdater> getSubscribedContexts() {
    return this.subscribedContexts;
  }

  public void setEmail(@Email String email) {
    this.email = email;
  }

  public void setSubscribedContexts(Collection<ContextUpdater> contexts) {
    this.subscribedContexts = (contexts == null) ? new HashSet<>() : contexts;
  }

}
