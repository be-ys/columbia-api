package com.almerys.columbia.api.services;

import com.almerys.columbia.api.domain.ColumbiaContext;
import com.almerys.columbia.api.domain.ColumbiaNewsletter;
import com.almerys.columbia.api.repository.NewsletterRepository;
import com.almerys.columbia.api.domain.dto.NewsletterUpdater;
import com.almerys.columbia.api.services.mailer.SendWelcomeMail;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.transaction.Transactional;
import javax.validation.constraints.Email;
import java.util.Collection;
import java.util.HashSet;

@Service
public class NewsletterService {

  private final NewsletterRepository newsletterRepository;
  private final ContextService contextService;
  private final SendWelcomeMail sendWelcomeMail;
  private final Utilities utilities;

  public NewsletterService(NewsletterRepository newsletterRepository, ContextService contextService, SendWelcomeMail sendWelcomeMail,
      Utilities utilities) {
    this.newsletterRepository = newsletterRepository;
    this.contextService = contextService;
    this.sendWelcomeMail = sendWelcomeMail;
    this.utilities = utilities;
  }

  public ColumbiaNewsletter getByToken(String token) {
    ColumbiaNewsletter ns = newsletterRepository.findByToken(token).orElse(null);
    if (ns != null) {
      ns.setEmail(utilities.decryptEmail(ns.getEmail()));
    }

    return ns;
  }

  public Iterable<ColumbiaNewsletter> getAll() {
    return newsletterRepository.findAll();
  }

  public void removeAllNewsletterFromSpecificContext(Long contextId) {
    ColumbiaContext columbiaContextToDelete = contextService.getById(contextId);

    if (columbiaContextToDelete == null) {
      throw new IllegalArgumentException("ColumbiaContext does not exists");
    }

    if (contextService.isParent(contextId)) {
      throw new IllegalArgumentException("ColumbiaContext is parent, cannot delete.");
    }

    Iterable<ColumbiaNewsletter> ns = newsletterRepository.findBySubscribedContextsContains(columbiaContextToDelete);

    ns.forEach(a -> {
      Collection<ColumbiaContext> old = a.getSubscribedContexts();
      if (old != null) {
        old.remove(columbiaContextToDelete);
        a.setSubscribedContexts(old);

      }
    });

    newsletterRepository.saveAll(ns);

  }

  @Transactional
  public ColumbiaNewsletter update(NewsletterUpdater newsletterUpdater, String token) {
    ColumbiaNewsletter ns = newsletterRepository.findByToken(token).orElse(null);
    newsletterRepository.deleteByToken(token);
    if (ns != null) {
      return createFromUpdater(newsletterUpdater, utilities.decryptEmail(ns.getEmail()));
    }
    return null;
  }

  public ColumbiaNewsletter createFromUpdater(NewsletterUpdater newsletterUpdater, @Email String email) {
    Assert.hasText(email, "email cannot be null.");

    //On vérifie si l'adresse n'est pas déjà inscrite
    if (getByToken(DigestUtils.sha256Hex(newsletterUpdater.getEmail())) != null) {
      throw new IllegalArgumentException("This email is already registered.");
    }

    Collection<ColumbiaContext> columbiaContexts = new HashSet<>();

    newsletterUpdater.getSubscribedContexts().forEach(e -> {
      if (contextService.getById(e.getId()) == null) {
        throw new IllegalArgumentException("ColumbiaContext " + e.getId() + " does not exist.");
      }

      columbiaContexts.add(contextService.getById(e.getId()));
    });

    //Si la liste des contextes est vide, on les ajoute tous.
    if (columbiaContexts.isEmpty()) {
      contextService.getAll().forEach(columbiaContexts::add);
    }

    ColumbiaNewsletter columbiaNewsletter = new ColumbiaNewsletter();
    columbiaNewsletter.setSubscribedContexts(columbiaContexts);
    columbiaNewsletter.setEmail(utilities.cryptEmail(newsletterUpdater.getEmail()));
    columbiaNewsletter.setToken(DigestUtils.sha256Hex(newsletterUpdater.getEmail()));

    //Sauvegarde
    ColumbiaNewsletter ns = newsletterRepository.save(columbiaNewsletter);
    sendWelcomeMail.prepareAndSend(ns);

    return ns;
  }

  public ColumbiaNewsletter createFromUpdater(NewsletterUpdater newsletterUpdater) {
    //Vérifications de base
    Assert.notNull(newsletterUpdater, "newsletterUpdater cannot be null.");
    Assert.hasText(newsletterUpdater.getEmail(), "Email cannot be null");

    return createFromUpdater(newsletterUpdater, newsletterUpdater.getEmail());
  }

  @Transactional
  public void deleteFromToken(String token) {
    newsletterRepository.deleteByToken(token);
  }

}
