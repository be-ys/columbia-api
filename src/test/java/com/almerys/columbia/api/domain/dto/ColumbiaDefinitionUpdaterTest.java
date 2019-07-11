package com.almerys.columbia.api.domain.dto;

import com.almerys.columbia.api.domain.ColumbiaContext;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ColumbiaDefinitionUpdaterTest {

  @Test
  public void mustCreateDefinitionUpdater() {
    new DefinitionUpdater();
  }

  @Test
  public void mustReturnItsOwnValues() {
    DefinitionUpdater definition = new DefinitionUpdater();
    definition.setContext(new ContextUpdater(4L, "coucou", null, null));
    definition.setTerm(new TermUpdater(8L, "salut", null));
    definition.setDefinition("ahah");

    assertThat(definition.getDefinition()).isEqualTo("ahah");
    assertThat(definition.getContext()).isEqualToComparingFieldByFieldRecursively(new ColumbiaContext(4L, "coucou", null, null));
    assertThat(definition.getTerm()).isEqualToComparingFieldByFieldRecursively(new TermUpdater(8L, "salut", null));
    assertThat(definition.getSynonymsTermList()).isEqualTo(new TermUpdater[] {});
    assertThat(definition.getAntonymsTermList()).isEqualTo(new TermUpdater[] {});
    assertThat(definition.getRelatedTermList()).isEqualTo(new TermUpdater[] {});
    assertThat(definition.getBibliography()).isEqualTo(new String[] {});
    assertThat(definition.getSources()).isEqualTo(new String[] {});
    assertThat(definition.getGdpr()).isEqualTo(null);

    TermUpdater[] synonyms = new TermUpdater[] { new TermUpdater(8L, "coucou", null), new TermUpdater(5L, "hi", null) };
    TermUpdater[] antonyms = new TermUpdater[] { new TermUpdater(5L, "hello", null), new TermUpdater(1L, "hey", null) };
    TermUpdater[] related = new TermUpdater[] { new TermUpdater(9L, "marrant", null), new TermUpdater(25L, "ahah", null) };

    definition.setAntonymsTermList(synonyms);
    definition.setSynonymsTermList(antonyms);
    definition.setRelatedTermList(related);
    definition.setGdpr(true);

    definition.setBibliography(null);
    definition.setSources(null);

    definition.setDefinition("eheh");
    assertThat(definition.getDefinition()).isEqualTo("eheh");

    definition.setContext(new ContextUpdater(8L, "coucou", null, null));
    definition.setTerm(new TermUpdater(6L, "eheh", null));

    assertThat(definition.getGdpr()).isEqualTo(true);
    assertThat(definition.getContext()).isEqualToComparingFieldByFieldRecursively(new ColumbiaContext(8L, "coucou", null, null));
    assertThat(definition.getTerm()).isEqualToComparingFieldByFieldRecursively(new TermUpdater(6L, "eheh", null));

    definition.setAntonymsTermList(null);
    definition.setSynonymsTermList(null);
    definition.setRelatedTermList(null);

    assertThat(definition.getSynonymsTermList()).isEqualTo(new TermUpdater[] {});
    assertThat(definition.getAntonymsTermList()).isEqualTo(new TermUpdater[] {});
    assertThat(definition.getRelatedTermList()).isEqualTo(new TermUpdater[] {});

    definition.setGdpr(false);
    assertThat(definition.getGdpr()).isEqualTo(false);

    definition.setGdpr(null);
    assertThat(definition.getGdpr()).isEqualTo(false);
  }

}
