package com.almerys.columbia.api.domain.dto;

import org.junit.Test;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;

public class ColumbiaUserUpdaterTest {

  @Test
  public void shouldCreateAndAccessData() {
    Date user = new Date();
    UserUpdater userUpdater = new UserUpdater();
    userUpdater.setId("333zed");
    userUpdater.setEmail("coucou@almerys.com");
    userUpdater.setPassword("aaaa");
    userUpdater.setRole("ADMIN");
    userUpdater.setUsername("ahah");
    userUpdater.setLastLogin(user);
    userUpdater.setActivationKey("coucou");

    assertThat(userUpdater.getLastLogin()).isEqualTo(user);
    assertThat(userUpdater.getRole()).isEqualTo("ADMIN");
    assertThat(userUpdater.getPassword()).isEqualTo("aaaa");
    assertThat(userUpdater.getUsername()).isEqualTo("ahah");
    assertThat(userUpdater.getEmail()).isEqualTo("coucou@almerys.com");
    assertThat(userUpdater.getId()).isEqualTo("333zed");
    assertThat(userUpdater.getActivationKey()).isEqualTo("coucou");

    Collection<ContextUpdater> contexts = new HashSet<>();
    ContextUpdater c1 = new ContextUpdater(1L, "coucou", null, null);
    ContextUpdater c2 = new ContextUpdater(2L, "coucoud", null, null);
    contexts.add(c1);
    contexts.add(c2);

    userUpdater.setGrantedContexts(contexts);
    assertThat(userUpdater.getGrantedContexts()).usingFieldByFieldElementComparator()
                                                .containsExactlyInAnyOrder(c1, c2);

  }

}
