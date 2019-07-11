package com.almerys.columbia.api.controllers;

import com.almerys.columbia.api.ColumbiaConfiguration;
import com.almerys.columbia.api.domain.ColumbiaNewsletter;
import com.almerys.columbia.api.domain.dto.NewsletterUpdater;
import com.almerys.columbia.api.services.NewsletterService;
import com.almerys.columbia.api.services.Utilities;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.web.util.UriComponentsBuilder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ColumbiaNewsletterControllerTest {
  @Mock
  NewsletterService service;

  @Mock
  Utilities utilities;

  @Mock
  ColumbiaConfiguration columbiaConfiguration;

  @InjectMocks
  NewsletterController controller;

  @Test
  public void testRemoveNewsletter() {
    //Doit retourner une 200
    assertThat(controller.removeNewsletter("zzzzzzza")
                         .getStatusCode()).isEqualTo(HttpStatus.OK);

    //Doit jeter une 401
    doThrow(IllegalArgumentException.class).when(service)
                                           .deleteFromToken(any());
    assertThatThrownBy(() -> controller.removeNewsletter("zzzzzzza")).isInstanceOf(IllegalArgumentException.class);

  }

  //Teste la récupération du statut de newsletter
  @Test
  public void testGetNewsletter() {
    //Doit retourner une 200
    when(service.getByToken(any())).thenReturn(new ColumbiaNewsletter());
    assertThat(controller.getNewsletter("zzzzzzza")
                         .getStatusCode()).isEqualTo(HttpStatus.OK);

    //Doit retourner une 401
    when(service.getByToken(any())).thenReturn(null);
    assertThat(controller.getNewsletter("zzzzzzza")
                         .getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

    //Doit jeter une 401
    doThrow(IllegalArgumentException.class).when(service)
                                           .getByToken(any());
    assertThatThrownBy(() -> controller.getNewsletter("zzzzzzza")).isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void testCreateNewsletter() {
    when(utilities.getScheme()).thenReturn("http");

    //Doit retourner une 201
    NewsletterUpdater updater = new NewsletterUpdater("coucou@almerys.com", null);
    when(service.createFromUpdater(updater)).thenReturn(new ColumbiaNewsletter());
    assertThat(controller.createNewsletter(updater, UriComponentsBuilder.newInstance())
                         .getStatusCode()).isEqualTo(HttpStatus.CREATED);

    //Doit retourner une 400
    when(service.createFromUpdater(any())).thenReturn(null);
    assertThat(controller.createNewsletter(updater, UriComponentsBuilder.newInstance())
                         .getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

    //Doit jeter une 400
    doThrow(IllegalArgumentException.class).when(service)
                                           .createFromUpdater(any());
    assertThatThrownBy(() -> controller.createNewsletter(updater, UriComponentsBuilder.newInstance())).isInstanceOf(
        IllegalArgumentException.class);
  }

  @Test
  public void testUpdateNewsletter() {
    //Doit retourner une 200
    NewsletterUpdater updater = new NewsletterUpdater("coucou@almerys.com", null);
    when(service.update(updater, "zzzzzzza")).thenReturn(new ColumbiaNewsletter("ciyciy@almerys.com"));
    assertThat(controller.updateNewsletter(updater, "zzzzzzza")
                         .getStatusCode()).isEqualTo(HttpStatus.OK);

    //Doit retourner une 400
    when(service.update(updater, "zzzzzzza")).thenReturn(null);
    assertThat(controller.updateNewsletter(updater, "zzzzzzza")
                         .getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

    //Doit jeter une 401.
    doThrow(IllegalArgumentException.class).when(service)
                                           .update(any(), any());
    assertThatThrownBy(() -> controller.updateNewsletter(updater, "zzzzzzza")).isInstanceOf(IllegalArgumentException.class);

  }
}
