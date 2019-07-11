package com.almerys.columbia.api.repository;

import com.almerys.columbia.api.domain.ColumbiaDefinition;
import com.almerys.columbia.api.domain.ColumbiaTerm;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DefinitionRepository extends SearchablePagingAndSortingRepository<ColumbiaDefinition, Long> {

  Optional<ColumbiaDefinition> findByContextIdAndTermId(Long context, Long term);

  void deleteAllByContextId(Long context);

  void deleteByTermIdAndContextId(Long termId, long contextId);

  void deleteAllByTermId(Long term);

  Long countDefinitionByContextId(Long context);

  Iterable<ColumbiaDefinition> findAllBySynonymsTermListContains(ColumbiaTerm term);

  Iterable<ColumbiaDefinition> findAllByAntonymsTermListContains(ColumbiaTerm term);

  Iterable<ColumbiaDefinition> findAllByRelatedTermListContains(ColumbiaTerm term);

  Iterable<ColumbiaDefinition> findAllByContextId(Long contextId);

}
