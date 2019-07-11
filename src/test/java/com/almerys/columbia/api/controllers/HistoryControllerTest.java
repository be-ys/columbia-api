package com.almerys.columbia.api.controllers;

import com.almerys.columbia.api.services.HistoryService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class HistoryControllerTest {
  @Mock
  HistoryService historyService;

  @InjectMocks
  HistoryController controller;

  //Teste la récupération d'historique d'une définition
  @Test
  public void testGetLastTermModificationInContext() {
    when(historyService.getLastModificationsForTermAndContext(any(), any(), any())).thenReturn(null);
    assertThat(controller.getLastModificationForSpecificContextAndTerm(1L, 4L, PageRequest.of(0, 10))
                         .getStatusCode()).isEqualTo(HttpStatus.OK);

    when(historyService.getLastModificationsForTermAndContext(any(), any(), any())).thenThrow(IllegalArgumentException.class);
    assertThatThrownBy(() -> controller.getLastModificationForSpecificContextAndTerm(1L, 4L, PageRequest.of(0, 10))).isInstanceOf(
        IllegalArgumentException.class);
  }

  @Test
  public void testGetLastDefinitionsModifications() {
    when(historyService.getLastDefinitionModifications(any())).thenReturn(null);
    assertThat(controller.getLastDefinitionsModifications(PageRequest.of(0, 10))
                         .getStatusCode()).isEqualTo(HttpStatus.OK);

    when(historyService.getLastDefinitionModifications(any())).thenThrow(IllegalArgumentException.class);
    assertThatThrownBy(() -> controller.getLastDefinitionsModifications(PageRequest.of(0, 10))).isInstanceOf(
        IllegalArgumentException.class);
  }

  //Teste la récupération d'historique des contextes
  @Test
  public void testGetLastContextesModifications() {
    when(historyService.getLastContextesModifications(any())).thenReturn(null);
    assertThat(controller.getLastContextesModifications(PageRequest.of(0, 10))
                         .getStatusCode()).isEqualTo(HttpStatus.OK);

    when(historyService.getLastContextesModifications(any())).thenThrow(IllegalArgumentException.class);
    assertThatThrownBy(() -> controller.getLastContextesModifications(PageRequest.of(0, 10))).isInstanceOf(IllegalArgumentException.class);
  }

  //Teste la récupération d'historique d'un contexte
  @Test
  public void testGetLastContextModifications() {
    when(historyService.getLastContextModifications(any(), any())).thenReturn(null);
    assertThat(controller.getLastContextModifications(4L, PageRequest.of(0, 10))
                         .getStatusCode()).isEqualTo(HttpStatus.OK);

    when(historyService.getLastContextModifications(any(), any())).thenThrow(IllegalArgumentException.class);
    assertThatThrownBy(() -> controller.getLastContextModifications(4L, PageRequest.of(0, 10))).isInstanceOf(
        IllegalArgumentException.class);
  }

  //Teste la récupération d'historique des définitions d'un contexte.
  @Test
  public void testGetLastDefinitionsModificationInContext() {
    when(historyService.getLastTermsModificationInContext(any(), any())).thenReturn(null);
    assertThat(controller.getLastDefinitionsModificationInContext(4L, PageRequest.of(0, 10))
                         .getStatusCode()).isEqualTo(HttpStatus.OK);

    when(historyService.getLastTermsModificationInContext(any(), any())).thenThrow(IllegalArgumentException.class);
    assertThatThrownBy(() -> controller.getLastDefinitionsModificationInContext(4L, PageRequest.of(0, 10))).isInstanceOf(
        IllegalArgumentException.class);
  }

  //Teste la récupération d'historique des termes.
  @Test
  public void testGetLastTermsModifications() {
    when(historyService.getLastTermsModifications(any())).thenReturn(null);
    assertThat(controller.getLastTermsModifications(PageRequest.of(0, 10))
                         .getStatusCode()).isEqualTo(HttpStatus.OK);

    when(historyService.getLastTermsModifications(any())).thenThrow(IllegalArgumentException.class);
    assertThatThrownBy(() -> controller.getLastTermsModifications(PageRequest.of(0, 10))).isInstanceOf(IllegalArgumentException.class);
  }

  //Teste la récupération d'historique d'un terme
  @Test
  public void testGetLastTermModifications() {
    when(historyService.getLastTermModifications(any(), any())).thenReturn(null);
    assertThat(controller.getLastTermModifications(4L, PageRequest.of(0, 10))
                         .getStatusCode()).isEqualTo(HttpStatus.OK);

    when(historyService.getLastTermModifications(any(), any())).thenThrow(IllegalArgumentException.class);
    assertThatThrownBy(() -> controller.getLastTermModifications(4L, PageRequest.of(0, 10))).isInstanceOf(IllegalArgumentException.class);
  }

  //Teste la récupération d'historique des définitions d'un terme
  @Test
  public void testGetLastTermModificationInContexts() {
    when(historyService.getLastTermModificationInContexts(any(), any())).thenReturn(null);
    assertThat(controller.getLastTermModificationInContexts(4L, PageRequest.of(0, 10))
                         .getStatusCode()).isEqualTo(HttpStatus.OK);

    when(historyService.getLastTermModificationInContexts(any(), any())).thenThrow(IllegalArgumentException.class);
    assertThatThrownBy(() -> controller.getLastTermModificationInContexts(4L, PageRequest.of(0, 10))).isInstanceOf(
        IllegalArgumentException.class);
  }

}
