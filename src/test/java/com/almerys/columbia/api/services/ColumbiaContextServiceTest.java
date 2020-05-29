package com.almerys.columbia.api.services;

import com.almerys.columbia.api.ColumbiaConfiguration;
import com.almerys.columbia.api.domain.ColumbiaTerm;
import com.almerys.columbia.api.repository.ContextRepository;
import com.almerys.columbia.api.repository.DefinitionRepository;
import com.almerys.columbia.api.repository.TermRepository;
import com.almerys.columbia.api.domain.ColumbiaContext;
import com.almerys.columbia.api.domain.dto.ContextUpdater;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ColumbiaContextServiceTest {
  @Mock
  ContextRepository contextRepository;

  //Il n'est pas inutile : Sans lui, l'appel au delete() fait une NullPointerException.
  @Mock
  DefinitionRepository definitionRepository;

  @Mock
  TermRepository termRepository;

  @InjectMocks
  ContextService service;

  @Mock
  ColumbiaConfiguration columbiaConfiguration;

  //Teste la fonction permettant de savoir si un contexte est parent.
  @Test
  public void testIsParent() {
    //Oui
    when(contextRepository.isParent(any())).thenReturn(true);
    assertThat(service.isParent(4L)).isEqualTo(true);

    //Non
    when(contextRepository.isParent(any())).thenReturn(false);
    assertThat(service.isParent(4L)).isEqualTo(false);
  }

  //Teste la recherche de termes dans un contexte spécifique.
  @Test
  public void testResearch() {
    //Doit retourner une liste de termes via recherche wildcard
    ColumbiaTerm columbiaTerm1 = new ColumbiaTerm(4L, "coucou");
    ColumbiaTerm columbiaTerm2 = new ColumbiaTerm(5L, "hello");

    List<ColumbiaTerm> te = new ArrayList<>();
    te.add(columbiaTerm1);
    te.add(columbiaTerm2);

    Page<ColumbiaTerm> terms = new PageImpl<>(te);

    when(contextRepository.findById(any())).thenReturn(Optional.of(new ColumbiaContext(4L, "coucou", null, null)));
    when(termRepository.findByNameStartingWithAndMetaphoneStartingWithForSpecificContext(any(), any(), any(), any())).thenReturn(terms);

    assertThat(service.research(4L, "bon*", PageRequest.of(0, 10))).usingFieldByFieldElementComparator()
                                                                   .containsExactlyInAnyOrder(columbiaTerm1, columbiaTerm2);

    //Doit retourner un liste de termes via recherche
    ColumbiaTerm columbiaTerm3 = new ColumbiaTerm(6L, "bonjour");
    te.add(columbiaTerm3);
    terms = new PageImpl<>(te);

    when(termRepository.findByNameAndMetaphoneForSpecificContext(any(), any(), any(), any())).thenReturn(terms);
    assertThat(service.research(4L, "bonjour", PageRequest.of(0, 10))).usingFieldByFieldElementComparator()
                                                                      .containsExactlyInAnyOrder(columbiaTerm1, columbiaTerm2,
                                                                          columbiaTerm3);
    when(termRepository.findForSpecificContext(any(), any())).thenReturn(new PageImpl<>(new ArrayList<>()));
    assertThat(service.research(4L, null, PageRequest.of(0, 10))).isEmpty();

    assertThat(service.research(4L, "e", PageRequest.of(0, 10))).usingFieldByFieldElementComparator()
                                                                .containsExactlyInAnyOrder(columbiaTerm1, columbiaTerm2, columbiaTerm3);
    assertThat(service.research(4L, "e", null)).usingFieldByFieldElementComparator()
                                               .containsExactlyInAnyOrder(columbiaTerm1, columbiaTerm2, columbiaTerm3);
    assertThat(service.research(4L, "e", PageRequest.of(0, 10))).usingFieldByFieldElementComparator()
                                                                .containsExactlyInAnyOrder(columbiaTerm1, columbiaTerm2, columbiaTerm3);

    //Doit retourner la liste des termes du contexte, en l'occurence, vide.
    assertThat(service.research(4L, "", PageRequest.of(0, 10))).isEmpty();

    //Doit retourner une liste d'abbréviation.
    when(termRepository.findByNameAndMetaphoneForSpecificContext(any(), any(), any(), any())).thenReturn(new PageImpl<>(new ArrayList<>()));
    when(termRepository.findByAbbreviations(any(), eq(PageRequest.of(0, 10)))).thenReturn(terms);
    assertThat(service.research(4L, "BONJOUR", PageRequest.of(0, 10), Boolean.FALSE)
                      .getContent()).usingFieldByFieldElementComparator()
                                    .containsExactlyInAnyOrder(columbiaTerm1, columbiaTerm2, columbiaTerm3);
    assertThat(service.research(4L, "bonjour", PageRequest.of(1, 10), Boolean.FALSE)
                      .isEmpty()).isTrue();

  }

  @Test
  public void testGetAllPage() {

    Page<ColumbiaContext> u = new PageImpl<>(new ArrayList<>());
    when(contextRepository.findAll((Pageable) any())).thenReturn(u);
    assertThat(service.getAll(PageRequest.of(0, 10))).isNullOrEmpty();
    assertThat(service.getAll(null)).isNullOrEmpty();
    assertThat(service.getAll(PageRequest.of(0, 10))).isNullOrEmpty();
  }

  //Teste la récupération de contexte par id.
  @Test
  public void testGetById() {
    //Doit retourner le contexte
    when(contextRepository.findById(any())).thenReturn(Optional.of(new ColumbiaContext(4L, "coucou", null, null)));
    assertThat(service.getById(4L)).isEqualToComparingFieldByFieldRecursively(new ColumbiaContext(4L, "coucou", null, null));

    //Doit retourner null
    when(contextRepository.findById(any())).thenReturn(Optional.empty());
    assertThat(service.getById(4L)).isNull();

    //Doit planter
    assertThatThrownBy(() -> service.getById(null)).isInstanceOf(IllegalArgumentException.class);
  }

  //Teste la récupération par nom et parent.
  @Test
  public void testGetByNameAndParentContext() {
    //Doit planter
    assertThatThrownBy(() -> service.getByNameAndParentContext(null, null)).isInstanceOf(IllegalArgumentException.class);

    //Doit retourner la valeur, avec ou sans contexte défini (si aucun, => root context).
    when(contextRepository.findByNameIgnoreCaseAndParentContextId(any(), any())).thenReturn(
        Optional.of(new ColumbiaContext(4L, "coucou", null, null)));
    assertThat(service.getByNameAndParentContext("coucou", null)).isEqualToComparingFieldByFieldRecursively(
        new ColumbiaContext(4L, "coucou", null, null));
    assertThat(service.getByNameAndParentContext("coucou",
        new ColumbiaContext(4L, "coucou", null, null))).isEqualToComparingFieldByFieldRecursively(
        new ColumbiaContext(4L, "coucou", null, null));

    //Doit retourner null
    when(contextRepository.findByNameIgnoreCaseAndParentContextId(any(), any())).thenReturn(Optional.empty());
    assertThat(service.getByNameAndParentContext("coucou", null)).isNull();
  }

  //Teste la suppression de contexte.
  @Test
  public void testDelete() {
    //Doit retourner une IAE
    assertThatThrownBy(() -> service.delete(null)).isInstanceOf(IllegalArgumentException.class);

    when(contextRepository.findById(4L)).thenReturn(Optional.empty());
    assertThatThrownBy(() -> service.delete(4L)).isInstanceOf(IllegalArgumentException.class);

    when(contextRepository.findById(5L)).thenReturn(Optional.of(new ColumbiaContext(4L, "coucou", null, null)));
    when(contextRepository.isParent(5L)).thenReturn(true);
    assertThatThrownBy(() -> service.delete(5L)).isInstanceOf(IllegalArgumentException.class);

    //Doit pouvoir supprimer
    when(contextRepository.isParent(5L)).thenReturn(false);
    service.delete(5L);
  }

  //Teste le retour entier de la base
  @Test
  public void testGetAll() {
    ColumbiaContext t1 = new ColumbiaContext(4L, "coucou", null, null);
    ColumbiaContext t2 = new ColumbiaContext(5L, "coucou", null, null);

    ColumbiaContext[] mock = { t1, t2 };

    when(contextRepository.findAll()).thenReturn(Arrays.asList(mock));
    assertThat(service.getAll()).usingFieldByFieldElementComparator()
                                .containsExactlyInAnyOrder(t1, t2);

    when(contextRepository.findAll()).thenReturn(null);
    assertThat(service.getAll()).isNullOrEmpty();
  }

  //Teste le système de vérification des parents
  @Test
  public void testIsValidParent() {
    //Doit passer : On définis le niveau à 5, et on regarde un columbiaContext ayant 4 parents.
    ColumbiaContext columbiaContext = new ColumbiaContext(4L, "coucou", null,
        new ColumbiaContext(3L, "parent", null, new ColumbiaContext(2L, "père", null, new ColumbiaContext(1L, "boss", null, null))));

    assertThat(service.isValidParent(columbiaContext, 5)).isEqualTo(true);

    //Doit planter : Le même mais avec le niveau à 3.
    assertThat(service.isValidParent(columbiaContext, 3)).isEqualTo(false);

  }

  //Teste la création de contexte
  @Test
  public void testCreate() {

    when(columbiaConfiguration.getMaxContextLevel()).thenReturn(2);

    //Test de la création sans encombre.
    ContextUpdater contextUpdater = new ContextUpdater();
    contextUpdater.setName("essai");
    when(contextRepository.save(any())).thenReturn(new ColumbiaContext(4L, "essai", null, null));

    assertThat(service.create(contextUpdater)).isEqualToComparingFieldByFieldRecursively(new ColumbiaContext(4L, "essai", null, null));

    //Test de la création avec un parent.
    contextUpdater.setParentContext(new ContextUpdater(3L, "hi", null, null));
    when(contextRepository.findById(3L)).thenReturn(Optional.of(new ColumbiaContext(3L, "coucou", null, null)));
    when(contextRepository.findByNameIgnoreCaseAndParentContextId(any(), any())).thenReturn(Optional.empty());

    assertThat(service.create(contextUpdater)).isEqualToComparingFieldByFieldRecursively(new ColumbiaContext(4L, "essai", null, null));

    //Test de toute les exceptions possibles...

    //Contexte déjà existant
    contextUpdater.setParentContext(null);
    when(contextRepository.findByNameIgnoreCaseAndParentContextId(any(), any())).thenReturn(
        Optional.of(new ColumbiaContext(4L, "fail", null, null)));
    assertThatThrownBy(() -> service.create(contextUpdater)).isInstanceOf(IllegalArgumentException.class);

    contextUpdater.setParentContext(new ContextUpdater(3L, "hello", null, null));

    //Contexte déjà existant dans le contexte parent
    when(contextRepository.findByNameIgnoreCaseAndParentContextId(any(), any())).thenReturn(
        Optional.of(new ColumbiaContext(4L, "fail", null, null)));
    assertThatThrownBy(() -> service.create(contextUpdater)).isInstanceOf(IllegalArgumentException.class);

    //Niveau invalide
    when(contextRepository.findById(3L)).thenReturn(Optional.of(
        new ColumbiaContext(3L, "coucou", null, new ColumbiaContext(2L, "coucou", null, new ColumbiaContext(1L, "hello", null, null)))));

    assertThatThrownBy(() -> service.create(contextUpdater)).isInstanceOf(IllegalArgumentException.class);

    //Parent qui n'existe pas.
    when(contextRepository.findById(3L)).thenReturn(Optional.empty());
    assertThatThrownBy(() -> service.create(contextUpdater)).isInstanceOf(IllegalArgumentException.class);
  }

  //Teste la mise à jour de contexte
  @Test
  public void testUpdate() {
    when(columbiaConfiguration.getMaxContextLevel()).thenReturn(2);

    //Test de la mise à jour sans encombre.
    ContextUpdater contextUpdater = new ContextUpdater();
    contextUpdater.setName("hello");

    when(contextRepository.findById(4L)).thenReturn(Optional.of(new ColumbiaContext(4L, "coucou", null, null)));
    when(contextRepository.findByNameIgnoreCaseAndParentContextId(any(), any())).thenReturn(Optional.empty());
    when(contextRepository.save(any())).thenReturn(new ColumbiaContext(4L, "coucou", null, null));

    assertThat(service.update(4L, contextUpdater)).isEqualToComparingFieldByFieldRecursively(new ColumbiaContext(4L, "coucou", null, null));

    //Test de la mise à jour avec un parent
    contextUpdater.setParentContext(new ContextUpdater(3L, "coucou", null, null));
    when(contextRepository.findById(3L)).thenReturn(Optional.of(new ColumbiaContext(3L, "coucou", null, null)));
    assertThat(service.update(4L, contextUpdater)).isEqualToComparingFieldByFieldRecursively(new ColumbiaContext(4L, "coucou", null, null));

    contextUpdater.setParentContext(new ContextUpdater(null, "coucou", null, null));
    assertThat(service.update(4L, contextUpdater)).isEqualToComparingFieldByFieldRecursively(new ColumbiaContext(4L, "coucou", null, null));

    contextUpdater.setParentContext(new ContextUpdater(3L, "coucou", null, null));

    //Teste toute les erreurs au monde.

    //Contexte déjà existant dans le contexte parent
    when(contextRepository.findByNameIgnoreCaseAndParentContextId(any(), any())).thenReturn(
        Optional.of(new ColumbiaContext(8L, "coucou", null, null)));
    assertThatThrownBy(() -> service.update(4L, contextUpdater)).isInstanceOf(IllegalArgumentException.class);

    when(contextRepository.findByNameIgnoreCaseAndParentContextId(any(), any())).thenReturn(
        Optional.of(new ColumbiaContext(4L, "coucou", null, null)));
    assertThat(service.update(4L, contextUpdater)).isEqualToComparingFieldByFieldRecursively(new ColumbiaContext(4L, "coucou", null, null));

    //Niveau d'indirection invalide
    when(contextRepository.findById(3L)).thenReturn(Optional.of(new ColumbiaContext(3L, "coucou", null,
        new ColumbiaContext(2L, "hello", null, new ColumbiaContext(1L, "ahah", null, new ColumbiaContext(0L, "root", null, null))))));

    contextUpdater.setParentContext(new ContextUpdater(3L, "coucou", null, null));
    assertThatThrownBy(() -> service.update(4L, contextUpdater)).isInstanceOf(IllegalArgumentException.class);

    //parent inexistant
    when(contextRepository.findById(3L)).thenReturn(Optional.empty());
    assertThatThrownBy(() -> service.update(4L, contextUpdater)).isInstanceOf(IllegalArgumentException.class);

    //Contexte inexistant
    when(contextRepository.findById(4L)).thenReturn(Optional.empty());
    assertThatThrownBy(() -> service.update(4L, contextUpdater)).isInstanceOf(IllegalArgumentException.class);

    //Contexte auto-parent
    contextUpdater.setParentContext(new ContextUpdater(4L, "hello", null, null));
    assertThatThrownBy(() -> service.update(4L, contextUpdater)).isInstanceOf(IllegalArgumentException.class);

    //Contexte sans nom.
    contextUpdater.setName(null);
    assertThatThrownBy(() -> service.update(4L, contextUpdater)).isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void testCyclicRedundancy() {
    //Le fonctionnement normal est déjà testé par la mise à jour
    //Ici, on focalise sur les cas d'erreur.

    ColumbiaContext contextA = new ColumbiaContext(1L, "coucou", null, null);
    ColumbiaContext contextB = new ColumbiaContext(2L, "coucou", null, contextA);
    ColumbiaContext contextC = new ColumbiaContext(3L, "coucou", null, contextB);
    ColumbiaContext contextD = new ColumbiaContext(4L, "coucou", null, contextC);

    when(contextRepository.findById(2L)).thenReturn(Optional.of(contextB));
    when(contextRepository.findById(3L)).thenReturn(Optional.of(contextC));
    when(contextRepository.findById(4L)).thenReturn(Optional.of(contextD));

    ContextUpdater parentContextUpdater = new ContextUpdater(4L, "coucou", null, null);
    ContextUpdater contextUpdater = new ContextUpdater(1L, "coucou", null, parentContextUpdater);
    assertThatThrownBy(() -> service.update(1L, contextUpdater)).isInstanceOf(IllegalArgumentException.class)
                                                                .hasMessage("Cyclic dependency ! Aborting.");

    ContextUpdater parentContextUpdater2 = new ContextUpdater(4L, "coucou", null, null);
    ContextUpdater contextUpdater2 = new ContextUpdater(2L, "coucou", null, parentContextUpdater2);
    assertThatThrownBy(() -> service.update(1L, contextUpdater2)).isInstanceOf(IllegalArgumentException.class)
                                                                 .hasMessage("Cyclic dependency ! Aborting.");

  }
}
