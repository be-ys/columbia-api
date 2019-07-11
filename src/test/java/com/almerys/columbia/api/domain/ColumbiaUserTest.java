package com.almerys.columbia.api.domain;

import org.junit.Test;

import java.util.Collection;
import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;

public class ColumbiaUserTest {
  @Test
  public void mustCreateAndAccessUser() {
    ColumbiaUser columbiaUser = new ColumbiaUser();
    columbiaUser.setRole("ADMIN");
    columbiaUser.setEmail("hi@almerys.com");
    columbiaUser.setUsername("Administrateur");
    columbiaUser.setPassword("aaaa");
    columbiaUser.setId("zbr");

    Collection<ColumbiaContext> columbiaContexts = new HashSet<>();
    ColumbiaContext c1 = new ColumbiaContext(1L, "coucou", null, null);
    ColumbiaContext c2 = new ColumbiaContext(2L, "hello", null, null);
    columbiaContexts.add(c1);
    columbiaContexts.add(c2);

    columbiaUser.setGrantedContexts(columbiaContexts);

    //Get
    assertThat(columbiaUser.getRole()).isEqualTo("ADMIN");
    assertThat(columbiaUser.getUsername()).isEqualTo("Administrateur");
    assertThat(columbiaUser.getId()).isEqualTo("zbr");
    assertThat(columbiaUser.getEmail()).isEqualTo("hi@almerys.com");

    assertThat(columbiaUser.getGrantedContexts()).usingFieldByFieldElementComparator()
                                                 .containsExactlyInAnyOrder(c1, c2);

    columbiaUser.setGrantedContexts(null);
    assertThat(columbiaUser.getGrantedContexts()).isEqualTo(new HashSet<>());
  }

}
