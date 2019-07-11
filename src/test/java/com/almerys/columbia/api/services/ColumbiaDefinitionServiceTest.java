package com.almerys.columbia.api.services;

import com.almerys.columbia.api.domain.ColumbiaContext;
import com.almerys.columbia.api.domain.ColumbiaDefinition;
import com.almerys.columbia.api.domain.ColumbiaTerm;
import com.almerys.columbia.api.domain.ColumbiaUser;
import com.almerys.columbia.api.domain.dto.ContextUpdater;
import com.almerys.columbia.api.repository.DefinitionRepository;
import com.almerys.columbia.api.domain.dto.DefinitionUpdater;
import com.almerys.columbia.api.domain.dto.TermUpdater;
import org.apache.commons.text.StringEscapeUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ColumbiaDefinitionServiceTest {
  @Mock
  DefinitionRepository repository;

  @Mock
  ContextService contextService;

  @Mock
  TermService termService;

  @InjectMocks
  DefinitionService service;

  @Test
  public void testByContextId() {
    assertThatThrownBy(() -> service.getByContextId(null)).isInstanceOf(IllegalArgumentException.class);

    ColumbiaDefinition def1 = new ColumbiaDefinition();
    def1.setDefinition("ah");
    def1.setContext(new ColumbiaContext(4L, "bonjour", "test", null));
    def1.setTerm(new ColumbiaTerm(1L, "bonjour"));

    ColumbiaDefinition def2 = new ColumbiaDefinition();
    def2.setDefinition("Essai");
    def2.setContext(new ColumbiaContext(3L, "test", "test", null));
    def2.setTerm(new ColumbiaTerm(2L, "Essai"));

    Collection<ColumbiaDefinition> defs = new HashSet<>();
    defs.add(def1);
    defs.add(def2);

    when(repository.findAllByContextId(any())).thenReturn(defs);
    assertThat(service.getByContextId(4L)).usingRecursiveFieldByFieldElementComparator().contains(def1, def2);

  }

  @Test
  public void testRemoveAllReferences() {
    assertThatThrownBy(() -> service.removeAllReferences(null)).isInstanceOf(IllegalArgumentException.class);

    ArrayList<ColumbiaDefinition> definitions1 = new ArrayList<>();
    definitions1.add(new ColumbiaDefinition());

    when(repository.findAllByAntonymsTermListContains(any())).thenReturn(definitions1);
    when(repository.findAllByRelatedTermListContains(any())).thenReturn(definitions1);
    when(repository.findAllBySynonymsTermListContains(any())).thenReturn(definitions1);

    service.removeAllReferences(new ColumbiaTerm());
  }

  @Test
  public void testUpdate() {
    //Must update

    DefinitionUpdater def = new DefinitionUpdater();
    def.setDefinition("ahah");

    ColumbiaDefinition def2 = new ColumbiaDefinition();
    def2.setContext(new ColumbiaContext(1L, "essai", null, null));
    def2.setTerm(new ColumbiaTerm(2L, "coucou"));
    def2.setDefinition(StringEscapeUtils.escapeHtml4("coucou"));

    when(contextService.getById(any())).thenReturn(new ColumbiaContext(4L, "nullol", null, null));
    when(termService.getById(any())).thenReturn(new ColumbiaTerm(4L, "coucou"));
    when(repository.findByContextIdAndTermId(any(), any())).thenReturn(Optional.of(def2));

    when(repository.save(any())).thenReturn(def2);

    service.update(def, 4L, 4L);

    //Must throw errors

    when(repository.findByContextIdAndTermId(any(), any())).thenReturn(Optional.empty());
    assertThatThrownBy(() -> service.update(def, 4L, 4L)).isInstanceOf(IllegalArgumentException.class);

    when(termService.getById(any())).thenReturn(null);
    assertThatThrownBy(() -> service.update(def, 4L, 4L)).isInstanceOf(IllegalArgumentException.class);

    when(contextService.getById(any())).thenReturn(null);
    assertThatThrownBy(() -> service.update(def, 4L, 4L)).isInstanceOf(IllegalArgumentException.class);

  }

  //Teste la vérification de définitions identiques.
  @Test
  public void testCreationCheckParentDef() {
    //Must create
    DefinitionUpdater def = new DefinitionUpdater();
    def.setTerm(new TermUpdater(8L, "salut", null));
    def.setDefinition("ahah");

    when(contextService.getById(any())).thenReturn(new ColumbiaContext(4L, "okay", null, new ColumbiaContext(8L, "ah", null, null)));

    when(termService.getById(any())).thenReturn(new ColumbiaTerm(8L, "coucou"));
    when(repository.findByContextIdAndTermId(any(), any())).thenReturn(Optional.empty());

    ColumbiaDefinition def2 = new ColumbiaDefinition();
    def2.setContext(new ColumbiaContext(1L, "essai", null, null));
    def2.setTerm(new ColumbiaTerm(2L, "coucou"));
    def2.setDefinition(StringEscapeUtils.escapeHtml4("coucou"));

    when(repository.save(any())).thenReturn(def2);

    when(repository.findByContextIdAndTermId(8L, 8L)).thenReturn(Optional.of(def2));

    //Doit passer
    service.create(def, 4L);

    when(repository.findByContextIdAndTermId(4L, 8L)).thenReturn(Optional.of(def2));

    //Doit planter
    assertThatThrownBy(() -> service.create(def, 4L)).isInstanceOf(IllegalArgumentException.class);

  }

  @Test
  public void testCreationDefinitionChecker() {
    //Must not create.
    DefinitionUpdater def = new DefinitionUpdater();
    def.setContext(new ContextUpdater(4L, "coucou", null, new ContextUpdater(5L, "hello", null, null)));
    def.setTerm(new TermUpdater(8L, "salut", null));
    def.setDefinition("ok");

    ColumbiaDefinition def2 = new ColumbiaDefinition();
    def2.setContext(new ColumbiaContext(1L, "essai", null, null));
    def2.setTerm(new ColumbiaTerm(2L, "coucou"));
    def2.setDefinition(StringEscapeUtils.escapeHtml4("ok"));

    when(contextService.getById(any())).thenReturn(new ColumbiaContext(4L, "okay", null, new ColumbiaContext(5L, "hello", null, null)));
    when(termService.getById(any())).thenReturn(new ColumbiaTerm(8L, "coucou"));
    when(repository.findByContextIdAndTermId(any(), any())).thenReturn(Optional.empty());

    service.create(def, 4L);

    when(repository.findByContextIdAndTermId(5L, 8L)).thenReturn(Optional.empty());
    service.create(def, 4L);

    when(repository.findByContextIdAndTermId(5L, 8L)).thenReturn(Optional.of(def2));
    assertThatThrownBy(() -> service.create(def, 4L)).isInstanceOf(IllegalArgumentException.class);

    def.setSynonymsTermList(new TermUpdater[] { new TermUpdater(6L, "coucou", null) });
    service.create(def, 4L);

    def.setAntonymsTermList(new TermUpdater[] { new TermUpdater(6L, "coucou", null) });
    service.create(def, 4L);

    def.setRelatedTermList(new TermUpdater[] { new TermUpdater(6L, "coucou", null) });
    service.create(def, 4L);

  }

  @Test
  public void testCreation() {
    //Must create
    DefinitionUpdater def = new DefinitionUpdater();
    def.setContext(new ContextUpdater(4L, "coucou", null, null));
    def.setTerm(new TermUpdater(8L, "salut", null));
    def.setDefinition("ahah");

    when(contextService.getById(any())).thenReturn(new ColumbiaContext(4L, "okay", null, null));
    when(termService.getById(any())).thenReturn(new ColumbiaTerm(8L, "coucou"));
    when(repository.findByContextIdAndTermId(any(), any())).thenReturn(Optional.empty());

    ColumbiaDefinition def2 = new ColumbiaDefinition();
    def2.setContext(new ColumbiaContext(1L, "essai", null, null));
    def2.setTerm(new ColumbiaTerm(2L, "coucou"));
    def2.setDefinition(StringEscapeUtils.escapeHtml4("coucou"));

    when(repository.save(any())).thenReturn(def2);

    service.create(def, 4L);

    //Fails
    when(repository.findByContextIdAndTermId(any(), any())).thenReturn(Optional.of(def2));

    assertThatThrownBy(() -> service.create(def, 4L)).isInstanceOf(IllegalArgumentException.class);

    when(termService.getById(any())).thenReturn(null);
    assertThatThrownBy(() -> service.create(def, 4L)).isInstanceOf(IllegalArgumentException.class)
                                                     .hasMessage("ColumbiaTerm does not exist in database.");

    DefinitionUpdater deftwo = new DefinitionUpdater();
    deftwo.setContext(new ContextUpdater(4L, "coucou", null, null));
    deftwo.setTerm(new TermUpdater(null, "salut", null));
    deftwo.setDefinition("ahah");

    assertThatThrownBy(() -> service.create(deftwo, 4L)).isInstanceOf(IllegalArgumentException.class)
                                                        .hasMessage("ColumbiaTerm id could not be null.");

    deftwo.setTerm(null);
    assertThatThrownBy(() -> service.create(deftwo, 4L)).isInstanceOf(IllegalArgumentException.class)
                                                        .hasMessage("ColumbiaTerm id could not be null.");

    when(contextService.getById(any())).thenReturn(null);
    assertThatThrownBy(() -> service.create(def, 4L)).isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void testArrayInsertion() {

    DefinitionUpdater defUp = new DefinitionUpdater();
    defUp.setContext(new ContextUpdater(4L, "coucou", null, null));
    defUp.setTerm(new TermUpdater(8L, "salut", null));
    defUp.setDefinition("ahah");
    defUp.setSynonymsTermList(new TermUpdater[] { new TermUpdater(8L, "coucou", null), new TermUpdater(6L, "hey", null) });
    defUp.setAntonymsTermList(new TermUpdater[] { new TermUpdater(5L, "grtg", null), new TermUpdater(9L, "hedzy", null) });
    defUp.setRelatedTermList(new TermUpdater[] { new TermUpdater(4L, "ser", null), new TermUpdater(8L, "heazy", null) });
    defUp.setSources(new String[] { "http://ah", "https://bh" });
    defUp.setBibliography(new String[] { "essai", "ok" });

    when(contextService.getById(any())).thenReturn(new ColumbiaContext(4L, "okay", null, null));
    when(termService.getById(any())).thenReturn(new ColumbiaTerm(8L, "coucou"));
    when(repository.findByContextIdAndTermId(any(), any())).thenReturn(Optional.empty());

    ColumbiaDefinition def = new ColumbiaDefinition();
    def.setContext(new ColumbiaContext(1L, "essai", null, null));
    def.setTerm(new ColumbiaTerm(2L, "coucou"));
    def.setDefinition(StringEscapeUtils.escapeHtml4("coucou"));

    when(repository.save(any())).thenReturn(def);

    when(termService.getById(any())).thenReturn(new ColumbiaTerm(8L, "coucou"));
    service.create(defUp, 8L);

    //Fail
    when(termService.getById(4L)).thenReturn(null);
    assertThatThrownBy(() -> service.create(defUp, 8L)).isInstanceOf(IllegalArgumentException.class);

    when(termService.getById(9L)).thenReturn(null);
    assertThatThrownBy(() -> service.create(defUp, 8L)).isInstanceOf(IllegalArgumentException.class);

    when(termService.getById(6L)).thenReturn(null);
    assertThatThrownBy(() -> service.create(defUp, 8L)).isInstanceOf(IllegalArgumentException.class);

    when(termService.getById(9L)).thenReturn(new ColumbiaTerm(9L, "coucou"));
    when(termService.getById(6L)).thenReturn(new ColumbiaTerm(6L, "coucou"));

    defUp.setRelatedTermList(new TermUpdater[] { new TermUpdater(null, "coucou", null) });
    assertThatThrownBy(() -> service.create(defUp, 8L)).isInstanceOf(IllegalArgumentException.class);

    defUp.setAntonymsTermList(new TermUpdater[] { new TermUpdater(null, "coucou", null) });
    assertThatThrownBy(() -> service.create(defUp, 8L)).isInstanceOf(IllegalArgumentException.class);

    defUp.setSynonymsTermList(new TermUpdater[] { new TermUpdater(null, "coucou", null) });
    assertThatThrownBy(() -> service.create(defUp, 8L)).isInstanceOf(IllegalArgumentException.class);

  }

  @Test
  public void mustDeleteFromRepository() {
    service.deleteByTermIdAndContextId(8L, 4L);
  }

  @Test
  public void mustNotDeleteFromRepository() {
    assertThatThrownBy(() -> service.deleteByTermIdAndContextId(null, 4L)).isInstanceOf(IllegalArgumentException.class);
    assertThatThrownBy(() -> service.deleteByTermIdAndContextId(8L, null)).isInstanceOf(IllegalArgumentException.class);
    assertThatThrownBy(() -> service.deleteByTermIdAndContextId(null, null)).isInstanceOf(IllegalArgumentException.class);

  }

  @Test
  public void mustReturnDataFromRepository() {

    ColumbiaDefinition def = new ColumbiaDefinition();
    def.setContext(new ColumbiaContext(1L, "essai", null, null));
    def.setTerm(new ColumbiaTerm(2L, "coucou"));
    def.setDefinition(StringEscapeUtils.escapeHtml4("coucou"));

    when(repository.findByContextIdAndTermId(any(), any())).thenReturn(Optional.of(def));

    ColumbiaDefinition result = service.getByContextIdAndTermId(1L, 8L);
    assertThat(result).isEqualToComparingFieldByFieldRecursively(def);
  }

  @Test
  public void mustDeleteAllByContextId() {
    when(contextService.getById(any())).thenReturn(new ColumbiaContext(4L, "coucou", null, null));
    service.deleteAllByContextId(4L);

    when(contextService.getById(any())).thenReturn(new ColumbiaContext(4L, "coucou", null, new ColumbiaContext(6L, "hello", null, null)));
    when(contextService.isParent(4L)).thenReturn(true);
    assertThatThrownBy(() -> service.deleteAllByContextId(4L)).isInstanceOf(IllegalArgumentException.class);

    when(contextService.getById(any())).thenReturn(null);
    assertThatThrownBy(() -> service.deleteAllByContextId(4L)).isInstanceOf(IllegalArgumentException.class);

  }

  @Test
  public void mustNotDeleteAllByContextId() {
    assertThatThrownBy(() -> service.deleteAllByContextId(null)).isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void mustDeleteAllByTermId() {
    service.deleteAllByTermId(4L);
  }

  @Test
  public void mustNotDeleteAllByTermId() {
    assertThatThrownBy(() -> service.deleteAllByTermId(null)).isInstanceOf(IllegalArgumentException.class);
  }
}
