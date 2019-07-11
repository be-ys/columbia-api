package com.almerys.columbia.api.domain;

import org.hibernate.envers.DefaultRevisionEntity;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ColumbiaRevisionTest {
  @Test
  public void testColumbiaRevision() {
    ColumbiaRevision columbiaRevision = new ColumbiaRevision();

    columbiaRevision.setUsername("Lol");
    assertThat(columbiaRevision.getUsername()).isEqualTo("Lol");

    assertThat(columbiaRevision.hashCode()).isEqualTo(new DefaultRevisionEntity().hashCode());
  }

  @Test
  public void testEquals() {
    ColumbiaRevision columbiaRevision = new ColumbiaRevision();
    ColumbiaRevision columbiaRevision2 = new ColumbiaRevision();

    columbiaRevision.setUsername("User");
    columbiaRevision2.setUsername("User");

    columbiaRevision.setId(23);
    columbiaRevision2.setId(30);

    //Objets de base pas égaux
    assertThat(columbiaRevision.equals(columbiaRevision2)).isEqualTo(false);

    //Objets de base égaux et username égaux.
    columbiaRevision.setId(30);
    assertThat(columbiaRevision.equals(columbiaRevision2)).isEqualTo(true);

    //Objets de base égaux et usernames différents.
    columbiaRevision.setUsername("User2");
    assertThat(columbiaRevision.equals(columbiaRevision2)).isEqualTo(false);

  }

}
