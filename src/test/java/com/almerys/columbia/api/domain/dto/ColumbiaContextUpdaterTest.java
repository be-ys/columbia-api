package com.almerys.columbia.api.domain.dto;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ColumbiaContextUpdaterTest {

  @Test
  public void mustCreateContextUpdater() {
    new ContextUpdater();

    new ContextUpdater(8L, "coucou", null, null);
  }

  @Test
  public void mustReturnItsOwnValues() {
    ContextUpdater context = new ContextUpdater(8L, "coucou", null, null);

    assertThat(context.getName()).isEqualTo("coucou");
    assertThat(context.getId()).isEqualTo(8L);
    assertThat(context.getDescription()).isEqualTo(null);
    assertThat(context.getParentContext()).isEqualTo(null);

    context.setName("hi");
    context.setId(5L);
    context.setDescription("bonjour");
    context.setParentContext(new ContextUpdater(8L, "coucou", null, null));

    assertThat(context.getName()).isEqualTo("hi");
    assertThat(context.getId()).isEqualTo(5L);
    assertThat(context.getDescription()).isEqualTo("bonjour");
    assertThat(context.getParentContext()).isEqualToComparingFieldByFieldRecursively(new ContextUpdater(8L, "coucou", null, null));

  }
}
