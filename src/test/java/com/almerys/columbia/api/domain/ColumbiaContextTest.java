package com.almerys.columbia.api.domain;

import org.junit.Test;

import static org.assertj.core.api.Assertions.*;

public class ColumbiaContextTest {

  @Test
  public void mustNotCreateWithoutName() {
    assertThatThrownBy(() -> new ColumbiaContext(1L, null, "coucou", new ColumbiaContext(8L, "coucou", "root", null))).isInstanceOf(
        IllegalArgumentException.class);
    assertThatThrownBy(() -> new ColumbiaContext(null, null, "essai", null)).isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void mustCreateWithArguments() {
    new ColumbiaContext(1L, "hello", "essai", null);
    new ColumbiaContext(null, "coucou", "moi", null);
    new ColumbiaContext(3L, "hello", "essai", new ColumbiaContext(null, "coucou", "moi", null));
  }

  @Test
  public void mustReturnItsValues() {
    ColumbiaContext hello = new ColumbiaContext(800L, "groupe", "essai", null);
    ColumbiaContext deux = new ColumbiaContext(550L, "numero deux", "sig", hello);

    assertThat(hello.getId()).isEqualTo(800L);
    assertThat(hello.getName()).isEqualTo("groupe");
    assertThat(hello.getParentContext()).isEqualTo(null);
    assertThat(hello.getDescription()).isEqualTo("essai");

    assertThat(deux.getParentContext()).isEqualTo(hello);

    deux.setParentContext(null);
    deux.setName("trois");
    deux.setDescription("Je suis un camion.");

    assertThat(deux.getName()).isEqualTo("trois");
    assertThat(deux.getParentContext()).isEqualTo(null);
    assertThat(deux.getDescription()).isEqualTo("Je suis un camion.");

    deux.setId(4L);
    assertThat(deux.getId()).isEqualTo(4L);
  }

  @Test
  public void mustCreateEmptyObject() {
    new ColumbiaContext();
  }

  @Test
  public void mustNotUpdateWithEmptyName() {
    ColumbiaContext hello = new ColumbiaContext(1L, "hello", "world", null);
    assertThatThrownBy(() -> hello.setName("")).isInstanceOf(IllegalArgumentException.class);
    assertThatThrownBy(() -> hello.setName(null)).isInstanceOf(IllegalArgumentException.class);
  }
}
