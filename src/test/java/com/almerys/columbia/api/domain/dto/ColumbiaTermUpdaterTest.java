package com.almerys.columbia.api.domain.dto;

import org.junit.Test;

import java.util.Collection;
import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;

public class ColumbiaTermUpdaterTest {

  @Test
  public void mustCreateTermUpdater() {
    new TermUpdater();
    new TermUpdater(8L, "coucou", null);
  }

  @Test
  public void mustReturnItsOwnValues() {
    TermUpdater term = new TermUpdater(8L, "coucou", null);
    assertThat(term.getId()).isEqualTo(8L);
    assertThat(term.getName()).isEqualTo("coucou");
    assertThat(term.getDefinitionList()).isEqualTo(null);

    Collection<String> abb = new HashSet<>();
    abb.add("A");
    abb.add("B");

    term.setAbbreviations(abb);

    term.setDefinitionList(null);
    term.setId(5L);
    term.setName("hello");

    assertThat(term.getAbbreviations()).containsExactlyInAnyOrder("A", "B");
    assertThat(term.getId()).isEqualTo(5L);
    assertThat(term.getDefinitionList()).isEqualTo(null);
    assertThat(term.getName()).isEqualTo("hello");

    term.setAbbreviations(null);
    assertThat(term.getAbbreviations()).isEqualTo(new HashSet<>());
  }

}
