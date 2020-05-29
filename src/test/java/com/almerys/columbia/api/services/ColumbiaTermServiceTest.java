package com.almerys.columbia.api.services;

import com.almerys.columbia.api.ColumbiaConfiguration;
import com.almerys.columbia.api.domain.ColumbiaDefinition;
import com.almerys.columbia.api.domain.ColumbiaTerm;
import com.almerys.columbia.api.repository.TermRepository;
import com.almerys.columbia.api.domain.ColumbiaContext;
import com.almerys.columbia.api.domain.dto.TermUpdater;
import org.apache.commons.text.StringEscapeUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)

public class ColumbiaTermServiceTest {
  @Mock
  TermRepository repository;

  @Mock
  ColumbiaConfiguration columbiaConfiguration;

  @InjectMocks
  TermService service;

  //Teste le retour aléatoire
  @Test
  public void testGetRandomTerm() {
    ColumbiaDefinition def = new ColumbiaDefinition();
    def.setContext(new ColumbiaContext(1L, "essai", null, null));
    def.setTerm(new ColumbiaTerm(2L, "coucou"));
    def.setDefinition(StringEscapeUtils.escapeHtml4("coucou"));

    ColumbiaTerm columbiaTerm = new ColumbiaTerm(4L, "coucou");
    columbiaTerm.addDefinition(def);

    when(repository.randomTerm()).thenReturn(new ColumbiaTerm(8L, "hello"))
                                 .thenReturn(columbiaTerm);

    //Doit retourner columbiaTerm et pas le terme 8L/hello, car il ne contient pas de définition.
    assertThat(service.getRandomTerm()).isEqualToComparingFieldByFieldRecursively(columbiaTerm);
  }

  //Teste la recherche
  @Test
  public void testResearch() {
    //Doit retourner tout le repository
    ColumbiaTerm t1 = new ColumbiaTerm(8L, "coucou");
    ColumbiaTerm t2 = new ColumbiaTerm(7L, "hello");
    List<ColumbiaTerm> te = new ArrayList<>();
    te.add(t1);
    te.add(t2);

    Page<ColumbiaTerm> terms = new PageImpl<>(te);
    when(repository.findAll(PageRequest.of(0, 10))).thenReturn(terms);
    assertThat(service.research("", PageRequest.of(0, 10))).usingFieldByFieldElementComparator()
                                                           .containsExactlyInAnyOrder(t1, t2);
    assertThat(service.research(null, PageRequest.of(0, 10))).usingFieldByFieldElementComparator()
                                                             .containsExactlyInAnyOrder(t1, t2);

    //Doit retourner la recherche avec wildcard
    ColumbiaTerm t3 = new ColumbiaTerm(6L, "ajout étoile");
    te.add(t3);
    terms = new PageImpl<>(te);

    when(repository.findAllByNameStartingWithIgnoreCaseOrMetaphoneStartingWith(any(), any(), any())).thenReturn(terms);
    assertThat(service.research("coucou*", PageRequest.of(0, 10))).usingFieldByFieldElementComparator()
                                                                  .containsExactlyInAnyOrder(t1, t2, t3);
    assertThat(service.research("coucou*", PageRequest.of(0, 10))).usingFieldByFieldElementComparator()
                                                                  .containsExactlyInAnyOrder(t1, t2, t3);

    //Recherche standard.
    te.remove(t2);
    terms = new PageImpl<>(te);

    when(repository.findAllByNameIgnoreCase(any(), any())).thenReturn(terms);
    assertThat(service.research("cou", PageRequest.of(0, 10))).usingFieldByFieldElementComparator()
                                                              .containsExactlyInAnyOrder(t1, t3);

  }

  //Teste le retour entier de tout les termes
  @Test
  public void testGetAll() {
    //Doit retourner tout le repository
    ColumbiaTerm t1 = new ColumbiaTerm(8L, "coucou");
    ColumbiaTerm t2 = new ColumbiaTerm(7L, "hello");
    List<ColumbiaTerm> te = new ArrayList<>();
    te.add(t1);
    te.add(t2);

    Page<ColumbiaTerm> terms = new PageImpl<>(te);

    when(repository.findAll(PageRequest.of(3, 10, Sort.by("id")
                                                      .ascending()))).thenReturn(new PageImpl<>(new ArrayList<>()));
    when(repository.findAll(PageRequest.of(0, 10, Sort.by("id")
                                                      .ascending()))).thenReturn(terms);
    assertThat(service.getAll(PageRequest.of(0, 10, Sort.by("id")
                                                        .ascending()))).usingFieldByFieldElementComparator()
                                                                       .containsExactlyInAnyOrder(t1, t2);
    assertThat(service.getAll(PageRequest.of(0, 10, Sort.by("id")
                                                        .ascending()))).usingFieldByFieldElementComparator()
                                                                       .containsExactlyInAnyOrder(t1, t2);
    assertThat(service.getAll(PageRequest.of(3, 10, Sort.by("id")
                                                        .ascending()))).isEmpty();
  }

  //Teste le retour par id
  @Test
  public void testGetById() {
    when(repository.findById(4L)).thenReturn(Optional.of(new ColumbiaTerm(8L, "coucou")));
    when(repository.findById(5L)).thenReturn(Optional.empty());

    assertThat(service.getById(4L)).isEqualToComparingFieldByFieldRecursively(new ColumbiaTerm(8L, "coucou"));
    assertThat(service.getById(5L)).isNull();
  }

  //Teste le retour par nom
  @Test
  public void testGetByName() {
    when(repository.findByNameIgnoreCase("coucou")).thenReturn(Optional.of(new ColumbiaTerm(8L, "coucou")));
    when(repository.findByNameIgnoreCase("bonjour")).thenReturn(Optional.empty());

    assertThat(service.getByName("coucou")).isEqualToComparingFieldByFieldRecursively(new ColumbiaTerm(8L, "coucou"));
    assertThat(service.getByName("bonjour")).isNull();
  }

  //Teste la création
  @Test
  public void testCreate() {
    //Doit passer
    TermUpdater termUpdater = new TermUpdater();
    termUpdater.setName("coucou");
    when(repository.save(any())).thenReturn(new ColumbiaTerm(4L, "coucou"));
    assertThat(service.create(termUpdater)).isEqualToComparingFieldByFieldRecursively(new ColumbiaTerm(4L, "coucou"));

    ArrayList<String> abbrs = new ArrayList<>();
    abbrs.add("abbr");
    abbrs.add("a");
    abbrs.add("uidhzei");
    termUpdater.setAbbreviations(abbrs);

    ArrayList<String> abb = new ArrayList<>();
    abb.add("abbr");
    abb.add("uidhzei");

    ColumbiaTerm columbiaTerm = new ColumbiaTerm(4L, "coucou");
    columbiaTerm.setAbbreviations(abb);
    when(repository.save(any())).thenReturn(columbiaTerm);

    assertThat(service.create(termUpdater)).isEqualToComparingFieldByFieldRecursively(columbiaTerm);

    //Doit échouer
    when(repository.findByNameIgnoreCase(any())).thenReturn(Optional.of(new ColumbiaTerm(4L, "nope")));
    assertThatThrownBy(() -> service.create(termUpdater)).isInstanceOf(IllegalArgumentException.class);

    termUpdater.setName(null);
    assertThatThrownBy(() -> service.create(termUpdater)).isInstanceOf(IllegalArgumentException.class);
  }

  //Teste la mise à jour
  @Test
  public void testUpdate() {
    //Doit passer
    TermUpdater termUpdater = new TermUpdater();
    termUpdater.setName("coucou");

    when(repository.findById(any())).thenReturn(Optional.of(new ColumbiaTerm(4L, "coucou")));
    service.update(4L, termUpdater);

    //Doit toujours passer : Le terme existe déjà mais c'est le même que l'on met à jour.
    when(repository.findByNameIgnoreCase(any())).thenReturn(Optional.of(new ColumbiaTerm(4L, "coucou")));
    service.update(4L, termUpdater);

    //Doit échouer
    //Terme déjà existant
    when(repository.findByNameIgnoreCase(any())).thenReturn(Optional.of(new ColumbiaTerm(3L, "coucou")));
    assertThatThrownBy(() -> service.update(4L, termUpdater)).isInstanceOf(IllegalArgumentException.class);

    //Terme inexistant
    when(repository.findById(any())).thenReturn(Optional.empty());
    assertThatThrownBy(() -> service.update(4L, termUpdater)).isInstanceOf(IllegalArgumentException.class);

    //Terme sans nom
    termUpdater.setName(null);
    assertThatThrownBy(() -> service.update(4L, termUpdater)).isInstanceOf(IllegalArgumentException.class);

    //Id null
    assertThatThrownBy(() -> service.update(null, termUpdater)).isInstanceOf(IllegalArgumentException.class);
  }

  //Teste la suppression
  @Test
  public void testDelete() {
    //Doit supprimer
    when(repository.findById(any())).thenReturn(Optional.of(new ColumbiaTerm(4L, "coucou")));
    service.delete(4L);

    //Doit échouer
    //Terme inexistant
    when(repository.findById(any())).thenReturn(Optional.empty());
    assertThatThrownBy(() -> service.delete(4L)).isInstanceOf(IllegalArgumentException.class);

    //Id null
    assertThatThrownBy(() -> service.delete(null)).isInstanceOf(IllegalArgumentException.class);

  }

}
