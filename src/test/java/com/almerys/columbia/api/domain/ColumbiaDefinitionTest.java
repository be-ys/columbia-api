package com.almerys.columbia.api.domain;

import org.junit.Test;

import java.util.HashSet;

import static org.assertj.core.api.Assertions.*;

public class ColumbiaDefinitionTest {

  @Test
  public void mustCreateEmptyObject() {
    new ColumbiaDefinition();
  }

  @Test
  public void mustReturnValues() {
    ColumbiaContext co = new ColumbiaContext(3L, "Zoo", null, null);
    ColumbiaTerm te = new ColumbiaTerm(6L, "Loutre");
    String deftext = "Animal de la classe des mammiféres.";

    ColumbiaDefinition def = new ColumbiaDefinition();
    def.setContext(co);
    def.setTerm(te);
    def.setDefinition(deftext);

    assertThat(def.getTerm()).isEqualTo(te);
    assertThat(def.getContext()).isEqualTo(co);
    assertThat(def.getDefinition()).isEqualTo(deftext);
    //        assertThat(def.getId()).isEqualToComparingFieldByFieldRecursively(new ColumbiaDefinitionId(6L,3L));

    assertThat(def.getSynonymsTermList()).isEqualTo(new HashSet<>());
    assertThat(def.getAntonymsTermList()).isEqualTo(new HashSet<>());
    assertThat(def.getRelatedTermList()).isEqualTo(new HashSet<>());
    assertThat(def.getBibliography()).isEqualTo(new HashSet<>());
    assertThat(def.getSources()).isEqualTo(new HashSet<>());

    //Update
    def.setSynonymsTermList(null);
    def.setAntonymsTermList(null);
    def.setRelatedTermList(null);
    def.setBibliography(null);
    def.setSources(null);

    assertThat(def.getSynonymsTermList()).isEqualTo(new HashSet<>());
    assertThat(def.getAntonymsTermList()).isEqualTo(new HashSet<>());
    assertThat(def.getRelatedTermList()).isEqualTo(new HashSet<>());
    assertThat(def.getBibliography()).isEqualTo(new HashSet<>());
    assertThat(def.getSources()).isEqualTo(new HashSet<>());

    //Update
    HashSet<ColumbiaTerm> syn = new HashSet<>();
    syn.add(new ColumbiaTerm(null, "a"));

    HashSet<ColumbiaTerm> ant = new HashSet<>();
    ant.add(new ColumbiaTerm(null, "b"));

    HashSet<ColumbiaTerm> rel = new HashSet<>();
    rel.add(new ColumbiaTerm(null, "c"));

    HashSet<String> bib = new HashSet<>();
    bib.add("bib");

    HashSet<String> sou = new HashSet<>();
    sou.add("sou");

    def.setSynonymsTermList(syn);
    def.setAntonymsTermList(ant);
    def.setRelatedTermList(rel);
    def.setBibliography(bib);
    def.setSources(sou);

    assertThat(def.getSynonymsTermList()).isEqualTo(syn);
    assertThat(def.getAntonymsTermList()).isEqualTo(ant);
    assertThat(def.getRelatedTermList()).isEqualTo(rel);
    assertThat(def.getBibliography()).isEqualTo(bib);
    assertThat(def.getSources()).isEqualTo(sou);

    def.setDefinition("Pouet");
    def.setTerm(new ColumbiaTerm(null, "Poulet"));
    def.setContext(new ColumbiaContext(null, "sans t ça fait poule", null, null));

    assertThat(def.getDefinition()).isEqualTo("Pouet");
    assertThat(def.getTerm()).isEqualToComparingFieldByFieldRecursively(new ColumbiaTerm(null, "Poulet"));
    assertThat(def.getContext()).isEqualToComparingFieldByFieldRecursively(new ColumbiaContext(null, "sans t ça fait poule", null, null));

    def.setGdpr(false);
    assertThat(def.getGdpr()).isEqualTo(false);

    def.setGdpr(true);
    assertThat(def.getGdpr()).isEqualTo(true);

    def.setGdpr(null);
    assertThat(def.getGdpr()).isEqualTo(false);

    def.setId(new ColumbiaDefinitionId(4L, 8L));
    assertThat(def.getId()).isEqualTo(new ColumbiaDefinitionId(4L, 8L));

  }

  @Test
  public void checkNonStandardMethods() {
    ColumbiaContext co = new ColumbiaContext(5L, "Zoo", null, null);
    ColumbiaTerm te = new ColumbiaTerm(72L, "Loutre");
    String deftext = "Animal de la classe des mammiféres.";

    ColumbiaDefinition def = new ColumbiaDefinition();
    def.setContext(co);
    def.setTerm(te);
    def.setDefinition(deftext);

    ColumbiaTerm columbiaTerm1 = new ColumbiaTerm(6L, "hey");
    ColumbiaTerm columbiaTerm2 = new ColumbiaTerm(8L, "coucou");

    //Antonyms
    def.addAntonym(columbiaTerm1);
    def.addAntonym(columbiaTerm2);

    assertThat(def.getAntonymsTermList()).contains(columbiaTerm1);
    assertThat(def.getAntonymsTermList()).contains(columbiaTerm2);

    def.removeAntonym(columbiaTerm1);
    assertThat(def.getAntonymsTermList()).contains(columbiaTerm2);
    assertThat(def.getAntonymsTermList()).doesNotContain(columbiaTerm1);

    def.addAntonym(new ColumbiaTerm(8L, "coucou"));

    //Synonyms
    def.addSynonym(columbiaTerm1);
    def.addSynonym(columbiaTerm2);

    assertThat(def.getSynonymsTermList()).contains(columbiaTerm1);
    assertThat(def.getSynonymsTermList()).contains(columbiaTerm2);

    def.removeSynonym(columbiaTerm1);
    assertThat(def.getSynonymsTermList()).contains(columbiaTerm2);
    assertThat(def.getSynonymsTermList()).doesNotContain(columbiaTerm1);

    def.addSynonym(new ColumbiaTerm(8L, "coucou"));

    //Related
    def.addRelated(columbiaTerm1);
    def.addRelated(columbiaTerm2);

    assertThat(def.getRelatedTermList()).contains(columbiaTerm1);
    assertThat(def.getRelatedTermList()).contains(columbiaTerm2);

    def.removeRelated(columbiaTerm1);
    assertThat(def.getRelatedTermList()).contains(columbiaTerm2);
    assertThat(def.getRelatedTermList()).doesNotContain(columbiaTerm1);

    def.addRelated(new ColumbiaTerm(8L, "coucou"));

    //Sources & biblio
    def.addSources("test");
    assertThat(def.getSources()).contains("test");
    def.removeSources("test");
    assertThat(def.getSources()).doesNotContain("test");

    def.addBibliography("test");
    assertThat(def.getBibliography()).contains("test");
    def.removeBibliography("test");
    assertThat(def.getBibliography()).doesNotContain("test");
  }

  @Test
  public void testEquals() {
    ColumbiaDefinitionId columbiaDefinitionId = new ColumbiaDefinitionId();
    ColumbiaDefinitionId columbiaDefinitionId1 = new ColumbiaDefinitionId();

    //Objets pas du même type
    ColumbiaTerm columbiaTerm = new ColumbiaTerm();

    assertThat(columbiaDefinitionId.equals(columbiaTerm)).isEqualTo(false);

    columbiaDefinitionId.setContextId(3L);
    columbiaDefinitionId1.setContextId(3L);

    columbiaDefinitionId.setTermId(8L);
    columbiaDefinitionId1.setTermId(5L);

    assertThat(columbiaDefinitionId.equals(columbiaDefinitionId1)).isEqualTo(false);

    columbiaDefinitionId.setContextId(0L);
    columbiaDefinitionId1.setContextId(3L);

    columbiaDefinitionId.setTermId(5L);
    columbiaDefinitionId1.setTermId(5L);

    assertThat(columbiaDefinitionId.equals(columbiaDefinitionId1)).isEqualTo(false);

    columbiaDefinitionId.setContextId(0L);
    columbiaDefinitionId1.setContextId(3L);

    columbiaDefinitionId.setTermId(8L);
    columbiaDefinitionId1.setTermId(5L);

    assertThat(columbiaDefinitionId.equals(columbiaDefinitionId1)).isEqualTo(false);

    columbiaDefinitionId.setContextId(3L);
    columbiaDefinitionId1.setContextId(3L);

    columbiaDefinitionId.setTermId(5L);
    columbiaDefinitionId1.setTermId(5L);

    assertThat(columbiaDefinitionId.equals(columbiaDefinitionId1)).isEqualTo(true);
  }

}
