package com.almerys.columbia.api.domain.dto;

import org.junit.Test;

import java.util.Collection;
import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;

public class ColumbiaNewsletterUpdaterTest {

  @Test
  public void testCreation() {
    NewsletterUpdater newsletterUpdater = new NewsletterUpdater();
    new NewsletterUpdater("coucou@mail.fr", null);

    Collection<ContextUpdater> contexts = new HashSet<>();
    contexts.add(new ContextUpdater(4L, "coucou", null, null));
    contexts.add(new ContextUpdater(8L, "deux", null, null));

    newsletterUpdater.setEmail("hey@almerys.com");
    newsletterUpdater.setSubscribedContexts(contexts);

    assertThat(newsletterUpdater.getEmail()).isEqualTo("hey@almerys.com");
    assertThat(newsletterUpdater.getSubscribedContexts()
                                .containsAll(contexts)).isEqualTo(true);

    newsletterUpdater.setSubscribedContexts(null);
    assertThat(newsletterUpdater.getSubscribedContexts()).isEqualTo(new HashSet<>());
  }
}
