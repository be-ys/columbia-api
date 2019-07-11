package com.almerys.columbia.api.domain;

import org.junit.Before;
import org.junit.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import javax.validation.Validator;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class ColumbiaNewsletterTest {

  private Validator validator;

  @Before
  public void setUp() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @Test
  public void testCreation() {
    Collection<ColumbiaContext> contexts = new HashSet<>();
    contexts.add(new ColumbiaContext(4L, "coucou", null, null));
    contexts.add(new ColumbiaContext(6L, "hello", null, null));

    new ColumbiaNewsletter();
    new ColumbiaNewsletter("local@localhost.fr");
    new ColumbiaNewsletter("local@localhost.fr", contexts);

    ColumbiaNewsletter contact = new ColumbiaNewsletter("local@localhost.fr", contexts);
    Set<ConstraintViolation<ColumbiaNewsletter>> violations = validator.validate(contact);
    assertThat(violations.isEmpty()).isEqualTo(true);

  }

  @Test
  public void mustPersists() {
    ColumbiaNewsletter news = new ColumbiaNewsletter();
    Collection<ColumbiaContext> contexts = new HashSet<>();
    contexts.add(new ColumbiaContext(4L, "coucou", null, null));
    contexts.add(new ColumbiaContext(6L, "hello", null, null));

    news.setEmail("Coucou@halleo.fr");
    news.setSubscribedContexts(contexts);

    assertThat(news.getEmail()).isEqualTo("Coucou@halleo.fr");

    assertThat(news.getSubscribedContexts()
                .containsAll(contexts)).isEqualTo(true);

    news.setSubscribedContexts(null);
    assertThat(news.getSubscribedContexts()).isEqualTo(new HashSet<>());

  }

}
