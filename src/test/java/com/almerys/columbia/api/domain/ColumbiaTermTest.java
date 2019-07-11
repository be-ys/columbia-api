package com.almerys.columbia.api.domain;

import org.apache.commons.text.StringEscapeUtils;
import org.junit.Test;

import java.util.Collection;
import java.util.HashSet;

import static org.assertj.core.api.Assertions.*;

public class ColumbiaTermTest {

  @Test
  public void mustNotCreateWithoutName() {
    assertThatThrownBy(() -> new ColumbiaTerm(null, null)).isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void mustCreateWithArguments() {
    new ColumbiaTerm(null, "hello");
    new ColumbiaTerm(4L, "hello");
  }

  @Test
  public void mustReturnItsValues() {
    ColumbiaTerm hello = new ColumbiaTerm(4L, "hello");

    assertThat(hello.getId()).isEqualTo(4L);
    assertThat(hello.getName()).isEqualTo("hello");

    hello.setName("heyheyhey");

    ColumbiaDefinition def = new ColumbiaDefinition();
    def.setContext(new ColumbiaContext(1L, "essai", null, null));
    def.setTerm(new ColumbiaTerm(2L, "coucou"));
    def.setDefinition(StringEscapeUtils.escapeHtml4("coucou"));

    hello.addDefinition(def);

    Collection<String> abbreviations = new HashSet<>();
    abbreviations.add("Coucou");
    abbreviations.add("Hello");

    hello.setAbbreviations(abbreviations);
    assertThat(hello.getAbbreviations()).containsExactlyInAnyOrder("Coucou", "Hello");

    hello.setAbbreviations(null);
    assertThat(hello.getAbbreviations()).isEqualTo(new HashSet<>());

    assertThat(hello.getId()).isEqualTo(4L);
    assertThat(hello.getName()).isEqualTo("heyheyhey");
  }

  @Test
  public void mustCreateEmptyObject() {
    new ColumbiaTerm();
  }
}