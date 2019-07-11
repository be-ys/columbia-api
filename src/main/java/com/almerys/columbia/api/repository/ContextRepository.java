package com.almerys.columbia.api.repository;

import com.almerys.columbia.api.domain.ColumbiaContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContextRepository extends SearchablePagingAndSortingRepository<ColumbiaContext, Long> {

  Optional<ColumbiaContext> findByNameIgnoreCaseAndParentContextId(String name, Long id);

  Iterable<ColumbiaContext> findAllByParentContextId(Long id);

  default boolean isParent(Long id) {
    return findAllByParentContextId(id).iterator().hasNext();
  }
}