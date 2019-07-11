package com.almerys.columbia.api.repository;

import com.almerys.columbia.api.domain.ColumbiaContext;
import com.almerys.columbia.api.domain.ColumbiaNewsletter;

import java.util.Optional;

public interface NewsletterRepository extends SearchablePagingAndSortingRepository<ColumbiaNewsletter, Long> {

  Optional<ColumbiaNewsletter> findByToken(String token);

  Iterable<ColumbiaNewsletter> findBySubscribedContextsContains(ColumbiaContext columbiaContext);

  void deleteByToken(String token);
}
