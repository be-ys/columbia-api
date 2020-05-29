package com.almerys.columbia.api.controllers;

import com.almerys.columbia.api.ColumbiaConfiguration;
import com.almerys.columbia.api.domain.ColumbiaContext;
import com.almerys.columbia.api.domain.ColumbiaTerm;
import com.almerys.columbia.api.domain.dto.ContextUpdater;
import com.almerys.columbia.api.services.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ColumbiaContextControllerTest {
  @Mock
  ContextService contextService;

  @Mock
  ColumbiaConfiguration columbiaConfiguration;

  @Mock
  Utilities utilities;

  @Mock
  GlobalService globalService;

  @InjectMocks
  ContextController controller;

  //Teste la récupération de tout les contenus.
  @Test
  public void testGetAllContexts() {
    //Test vide
    when(contextService.getAll(any())).thenReturn(null);
    assertThat(controller.getAllContexts(PageRequest.of(0, 10))
                         .getStatusCode()).isEqualTo(HttpStatus.OK);

    //Test avec des columbiaContexts
    ColumbiaContext columbiaContextTest = new ColumbiaContext(4L, "bonjour", null, null);

    List<ColumbiaContext> columbiaContexts = new ArrayList<>();
    columbiaContexts.add(columbiaContextTest);
    Page<ColumbiaContext> columbiaContextPage = new PageImpl<ColumbiaContext>(columbiaContexts);

    when(contextService.getAll(any())).thenReturn(columbiaContextPage);
    assertThat(controller.getAllContexts(PageRequest.of(0, 10))
                         .getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  //Teste le système de recherche par contexte.
  @Test
  public void testGetTermsInContext() {
    //Doit retourner 200 si vide
    assertThat(controller.getTermsInContext(4L, "", Boolean.FALSE, PageRequest.of(0, 10))
                         .getStatusCode()).isEqualTo(HttpStatus.OK);

    //Doit retourner 200 et contenu si rempli.
    ColumbiaTerm columbiaTerm1 = new ColumbiaTerm(1L, "bonjour");
    ColumbiaTerm columbiaTerm2 = new ColumbiaTerm(2L, "bientot");
    List columbiaTerms = new ArrayList();
    columbiaTerms.add(columbiaTerm1);
    columbiaTerms.add(columbiaTerm2);

    assertThat(controller.getTermsInContext(1L, "", Boolean.FALSE,PageRequest.of(0, 10))
                         .getStatusCode()).isEqualTo(HttpStatus.OK);

    //Doit retourner une 400 si le service soulève une IAE
    when(contextService.research(any(), any(), any(), any())).thenThrow(IllegalArgumentException.class);
    assertThatThrownBy(() -> controller.getTermsInContext(1L, "", Boolean.FALSE,PageRequest.of(0, 10))).isInstanceOf(IllegalArgumentException.class);
  }

  //Teste le système de récupération de contexte.
  @Test
  public void testGetContextById() {
    //Doit retourner une 404
    when(contextService.getById(any())).thenReturn(null);
    assertThat(controller.getContextById(1L)
                         .getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

    //Doit retourner une 200
    ColumbiaContext columbiaContext = new ColumbiaContext(1L, "bonjour", null, null);
    when(contextService.getById(1L)).thenReturn(columbiaContext);
    assertThat(controller.getContextById(1L)
                         .getBody()).isEqualToComparingFieldByFieldRecursively(columbiaContext);
    assertThat(controller.getContextById(1L)
                         .getStatusCode()).isEqualTo(HttpStatus.OK);

    //Doit retourner une 400
    when(contextService.getById(any())).thenThrow(IllegalArgumentException.class);
    assertThatThrownBy(() -> controller.getContextById(1L)).isInstanceOf(IllegalArgumentException.class);
  }

  //Teste la suppression de contexte.
  @Test
  public void testDeleteContext() {
    //Doit retourner une 200
    doNothing().when(globalService)
               .deleteContext(any());

    assertThat(controller.deleteContext(1L)
                         .getStatusCode()).isEqualTo(HttpStatus.OK);

    //Doit jeter une 400
    doThrow(IllegalArgumentException.class).when(globalService)
                                           .deleteContext(any());
    assertThatThrownBy(() -> controller.deleteContext(1L)).isInstanceOf(IllegalArgumentException.class);

    //Doit jeter une 500
    doThrow(NullPointerException.class).when(globalService)
                                       .deleteContext(any());
    assertThatThrownBy(() -> controller.deleteContext(1L)).isInstanceOf(NullPointerException.class);

  }

  //Teste la création de contexte.
  @Test
  public void testCreateContext() {
    //Doit retourner une 201
    when(utilities.getScheme()).thenReturn("http");


    ContextUpdater contextUpdater = new ContextUpdater();

    when(contextService.create(any())).thenReturn(new ColumbiaContext(1L, "bonjour", null, null));
    assertThat(controller.createContext(contextUpdater, UriComponentsBuilder.newInstance())
                         .getStatusCode()).isEqualTo(HttpStatus.CREATED);

    //Doit retourner une 400
    when(contextService.create(any())).thenReturn(null);
    assertThat(controller.createContext(contextUpdater, UriComponentsBuilder.newInstance())
                         .getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

    //Doit jeter une erreur.
    when(contextService.create(any())).thenThrow(IllegalArgumentException.class);
    assertThatThrownBy(() -> controller.createContext(contextUpdater, UriComponentsBuilder.newInstance())).isInstanceOf(
        IllegalArgumentException.class);
  }

  //Teste la mise à jour de contexte.
  @Test
  public void testUpdateContext() {
    //Doit retourner une 200
    ContextUpdater contextUpdater = new ContextUpdater();
    when(contextService.update(any(), any())).thenReturn(new ColumbiaContext(1L, "coucou", null, null));
    assertThat(controller.updateContext(1L, contextUpdater)
                         .getStatusCode()).isEqualTo(HttpStatus.OK);

    //Doit retourner une 400
    when(contextService.update(any(), any())).thenThrow(IllegalArgumentException.class);
    assertThatThrownBy(() -> controller.updateContext(1L, contextUpdater)).isInstanceOf(IllegalArgumentException.class);

  }

}
