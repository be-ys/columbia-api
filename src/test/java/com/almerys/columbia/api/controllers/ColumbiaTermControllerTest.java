package com.almerys.columbia.api.controllers;

import com.almerys.columbia.api.ColumbiaConfiguration;
import com.almerys.columbia.api.domain.ColumbiaTerm;
import com.almerys.columbia.api.domain.dto.TermUpdater;
import com.almerys.columbia.api.services.GlobalService;
import com.almerys.columbia.api.services.TermService;
import com.almerys.columbia.api.services.Utilities;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ColumbiaTermControllerTest {
  @Mock
  TermService service;

  @Mock
  GlobalService globalService;

  @Mock
  Utilities utilities;

  @Mock
  ColumbiaConfiguration columbiaConfiguration;

  @InjectMocks
  TermController controller;

  //Teste la recherche de termes.
  @Test
  public void testGetTerms() {
    //Doit retourner une 307 (random)
    when(utilities.getScheme()).thenReturn("http");
    when(service.getRandomTerm()).thenReturn(new ColumbiaTerm(4L, "essai"));
    assertThat(controller.getTerms("random", PageRequest.of(0, 10), UriComponentsBuilder.newInstance())
                         .getStatusCode()).isEqualTo(HttpStatus.TEMPORARY_REDIRECT);

    //Doit retourner une 200
    ColumbiaTerm columbiaTerm1 = new ColumbiaTerm(4L, "coucou");
    ColumbiaTerm columbiaTerm2 = new ColumbiaTerm(5L, "hey");
    List columbiaTerms = new ArrayList();
    columbiaTerms.add(columbiaTerm1);
    columbiaTerms.add(columbiaTerm2);

    when(service.research(any(), any())).thenReturn(new PageImpl(columbiaTerms));
    assertThat(controller.getTerms(null, PageRequest.of(0, 10), UriComponentsBuilder.newInstance())
                         .getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(controller.getTerms("coucou", PageRequest.of(0, 10), UriComponentsBuilder.newInstance())
                         .getStatusCode()).isEqualTo(HttpStatus.OK);

    //Doit jeter une 400
    when(service.research(any(), any())).thenThrow(IllegalArgumentException.class);
    assertThatThrownBy(() -> controller.getTerms("coucou", PageRequest.of(0, 10), UriComponentsBuilder.newInstance())).isInstanceOf(
        IllegalArgumentException.class);
  }

  //Teste la récupération d'un terme précis
  @Test
  public void testGetTermById() {
    //Doit retourner une 200 OK
    when(service.getById(any())).thenReturn(new ColumbiaTerm(4L, "hello"));
    assertThat(controller.getTermById(4L)
                         .getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(controller.getTermById(4L)
                         .getBody()).isEqualToComparingFieldByFieldRecursively(new ColumbiaTerm(4L, "hello"));

    //Doit retourner une 404 NOT FOUND
    when(service.getById(any())).thenReturn(null);
    assertThat(controller.getTermById(4L)
                         .getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

    //Doit jetere une 400 BAD REQUEST
    when(service.getById(any())).thenThrow(IllegalArgumentException.class);
    assertThatThrownBy(() -> controller.getTermById(4L)).isInstanceOf(IllegalArgumentException.class);
  }

  //Teste la suppression d'un terme
  @Test
  public void testDeleteTerm() {
    //Doit retourner une 200 OK
    doNothing().when(globalService)
               .deleteTerm(any());
    assertThat(controller.deleteTerm(4L)
                         .getStatusCode()).isEqualTo(HttpStatus.OK);

    //Doit jeter une 400
    doThrow(IllegalArgumentException.class).when(globalService)
                                           .deleteTerm(any());
    assertThatThrownBy(() -> controller.deleteTerm(4L)).isInstanceOf(IllegalArgumentException.class);
  }

  //Teste la création d'un terme
  @Test
  public void testCreateTerm() {

    //Doit retourner une 201 CREATED
    when(service.create(any())).thenReturn(new ColumbiaTerm(5L, "coucou"));
    assertThat(controller.createTerm(new TermUpdater(), UriComponentsBuilder.newInstance())
                         .getStatusCode()).isEqualTo(HttpStatus.CREATED);

    //Doit retourner une 400 BAD REQUEST
    when(service.create(any())).thenReturn(null);
    assertThat(controller.createTerm(new TermUpdater(), UriComponentsBuilder.newInstance())
                         .getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

    //Doit jeter une 400 BAD REQUEST
    when(service.create(any())).thenThrow(IllegalArgumentException.class);
    assertThatThrownBy(() -> controller.createTerm(new TermUpdater(), UriComponentsBuilder.newInstance())).isInstanceOf(
        IllegalArgumentException.class);
  }

  //Teste la mise à jour d'un terme
  @Test
  public void testUpdateTerm() {
    //Doit retourner une 201 CREATED
    when(service.update(any(), any())).thenReturn(new ColumbiaTerm(5L, "coucou"));
    assertThat(controller.updateTerm(4L, new TermUpdater())
                         .getStatusCode()).isEqualTo(HttpStatus.OK);

    //Doit jeter une 400 BAD REQUEST
    when(service.update(any(), any())).thenThrow(IllegalArgumentException.class);
    assertThatThrownBy(() -> controller.updateTerm(4L, new TermUpdater())).isInstanceOf(IllegalArgumentException.class);
  }
}
