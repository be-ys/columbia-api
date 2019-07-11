package com.almerys.columbia.api.domain;

import org.junit.Test;

import static org.assertj.core.api.Assertions.*;

public class ColumbiaColumbiaDefinitionIdTest {

  @Test
  public void mustCreateEmptyObject() {
    new ColumbiaDefinitionId();
  }

  @Test
  public void mustCreateObject() {
    new ColumbiaDefinitionId(8L, 4L);
  }

  @Test
  public void mustNotCreateObject() {
    assertThatThrownBy(() -> new ColumbiaDefinitionId(null, null)).isInstanceOf(IllegalArgumentException.class);
    assertThatThrownBy(() -> new ColumbiaDefinitionId(4L, null)).isInstanceOf(IllegalArgumentException.class);
    assertThatThrownBy(() -> new ColumbiaDefinitionId(null, 7L)).isInstanceOf(IllegalArgumentException.class);

  }

  @Test
  public void mustSet() {
    ColumbiaDefinitionId def = new ColumbiaDefinitionId();
    def.setContextId(4L);
    def.setTermId(2L);

    assertThat(def.getContextId()).isEqualTo(4L);
    assertThat(def.getTermId()).isEqualTo(2L);

  }

  @Test
  public void uselessHashCodeTest() {
    assertThat(new ColumbiaDefinitionId(4L, 7L).hashCode()).isNotEqualTo(new ColumbiaDefinitionId(5L, 3L).hashCode());
  }

  @Test
  public void mustKnowIfEqualsObjects() {
    ColumbiaDefinitionId first = new ColumbiaDefinitionId(4L, 7L);
    ColumbiaDefinitionId second = new ColumbiaDefinitionId(8L, 3L);
    ColumbiaDefinitionId third = new ColumbiaDefinitionId(4L, 7L);

    assertThat(first.equals(second)).isEqualTo(false);
    assertThat(first.equals(third)).isEqualTo(true);
  }

}
