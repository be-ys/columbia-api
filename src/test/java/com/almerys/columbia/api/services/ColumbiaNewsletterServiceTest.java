package com.almerys.columbia.api.services;

import com.almerys.columbia.api.domain.ColumbiaContext;
import com.almerys.columbia.api.domain.ColumbiaNewsletter;
import com.almerys.columbia.api.repository.NewsletterRepository;
import com.almerys.columbia.api.domain.dto.ContextUpdater;
import com.almerys.columbia.api.domain.dto.NewsletterUpdater;
import com.almerys.columbia.api.services.mailer.SendWelcomeMail;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.validation.constraints.Null;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ColumbiaNewsletterServiceTest {
  @Mock
  SendWelcomeMail sendWelcomeMail;

  @Mock
  NewsletterRepository newsletterRepository;

  @Mock
  ContextService contextService;

  @Mock
  Utilities utilities;

  @InjectMocks
  NewsletterService service;

  //Teste la récupération par token
  @Test
  public void testGetByToken() {
    when(utilities.decryptEmail(any())).thenReturn("bonjour@almerys.com");

    //Doit retourner un mail
    when(newsletterRepository.findByToken(any())).thenReturn(Optional.of(new ColumbiaNewsletter("bonjour@almerys.com")));
    assertThat(service.getByToken("Hiuabashz")
                      .getEmail()).isEqualTo("bonjour@almerys.com");

    //Doit retourner un null
    when(newsletterRepository.findByToken(any())).thenReturn(Optional.empty());
    assertThat(service.getByToken("Hiuabashz")).isNull();
  }

  //Teste la suppression d'un contexte spécifique pour toute les newsletter
  @Test
  public void testRemoveAllNewsletterFromSpecificContext() {
    //Doit ne pas planter.
    when(contextService.getById(any())).thenReturn(new ColumbiaContext(4L, "coucou", null, null));

    Collection<ColumbiaContext> columbiaContexts = new ArrayList<>();
    columbiaContexts.add(new ColumbiaContext(4L, "bonjour", null, null));
    columbiaContexts.add(new ColumbiaContext(5L, "coucou", null, null));

    Collection<ColumbiaNewsletter> columbiaNewsletters = new ArrayList<>();
    ColumbiaNewsletter ns1 = new ColumbiaNewsletter("bonjour@almerys.com");
    ColumbiaNewsletter ns2 = new ColumbiaNewsletter("hey@almerys.com");
    ns2.setSubscribedContexts(columbiaContexts);
    columbiaNewsletters.add(ns1);
    columbiaNewsletters.add(ns2);

    when(newsletterRepository.findBySubscribedContextsContains(any())).thenReturn(columbiaNewsletters);
    service.removeAllNewsletterFromSpecificContext(4L);

    //Doit planter
    when(contextService.getById(any())).thenReturn(new ColumbiaContext(4L, "coucou", null, new ColumbiaContext(5L, "parent", null, null)));
    when(contextService.isParent(4L)).thenReturn(true);
    assertThatThrownBy(() -> service.removeAllNewsletterFromSpecificContext(4L)).isInstanceOf(IllegalArgumentException.class);

    when(contextService.getById(any())).thenReturn(null);
    assertThatThrownBy(() -> service.removeAllNewsletterFromSpecificContext(4L)).isInstanceOf(IllegalArgumentException.class);
  }

  //Teste le getAll... u_u
  @Test
  public void testGetAll() {
    when(newsletterRepository.findAll()).thenReturn(null);
    service.getAll();
  }

  //Teste la mise à jour et création de la newsletter
  @Test
  public void testUpdateAndCreation() {
    //Doit planter.
    when(utilities.decryptEmail(any())).thenReturn("normal@almerys.com");

    when(newsletterRepository.findByToken(any())).thenReturn(Optional.empty());
    assertThat(service.update(new NewsletterUpdater("hi@almerys.com", null), "uihi")).isNull();

    assertThatThrownBy(() -> service.createFromUpdater(null)).isInstanceOf(IllegalArgumentException.class);
    assertThatThrownBy(() -> service.createFromUpdater(new NewsletterUpdater(null, null))).isInstanceOf(IllegalArgumentException.class);

    //Création (doit passer)
    when(newsletterRepository.findByToken(any())).thenReturn(Optional.empty());
    service.createFromUpdater(new NewsletterUpdater("bonjour@local.com", null));

    Collection<ContextUpdater> contextUpdaters = new ArrayList<>();
    contextUpdaters.add(new ContextUpdater(4L, null, null, null));
    contextUpdaters.add(new ContextUpdater(5L, null, null, null));

    when(contextService.getById(any())).thenReturn(new ColumbiaContext());
    service.createFromUpdater(new NewsletterUpdater("bonjour@local.com", contextUpdaters));


    //Erreurs
    //Contexte inexistant
    when(contextService.getById(any())).thenReturn(null);
    assertThatThrownBy(() -> service.createFromUpdater(new NewsletterUpdater("hi@almerys.com", contextUpdaters), "aaa")).isInstanceOf(
        IllegalArgumentException.class);

    //Email déjà existant
    when(newsletterRepository.findByToken(any())).thenReturn(Optional.of(new ColumbiaNewsletter("bonjour@local", null)));
    assertThatThrownBy(() -> service.createFromUpdater(new NewsletterUpdater("hi@almerys.com", contextUpdaters), "aaa")).isInstanceOf(
        IllegalArgumentException.class);

    //Email à null
    assertThatThrownBy(() -> service.update(new NewsletterUpdater(null, contextUpdaters), "aaa")).isInstanceOf(
        NullPointerException.class);
  }

  //Teste la suppression de newsletter
  @Test
  public void testDeleteFromToken() {
    //Doit passer
    doNothing().when(newsletterRepository)
               .deleteByToken(any());
    service.deleteFromToken("token");
  }
}
