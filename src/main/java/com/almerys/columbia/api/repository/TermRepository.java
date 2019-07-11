package com.almerys.columbia.api.repository;

import com.almerys.columbia.api.domain.ColumbiaContext;
import com.almerys.columbia.api.domain.ColumbiaTerm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TermRepository extends SearchablePagingAndSortingRepository<ColumbiaTerm, Long> {

  Optional<ColumbiaTerm> findById(Long id);

  @Query("SELECT t FROM ColumbiaTerm t JOIN ColumbiaDefinition d ON d.term=t WHERE d.context=:context")
  Page<ColumbiaTerm> findForSpecificContext(ColumbiaContext context, Pageable pageable);

  @Query("SELECT t FROM ColumbiaTerm t JOIN ColumbiaDefinition d ON d.term=t WHERE d.context=:context AND (t.name LIKE :string OR t.metaphone LIKE :metaphone)")
  Page<ColumbiaTerm> findByNameAndMetaphoneForSpecificContext(ColumbiaContext context, String string, String metaphone, Pageable pageable);

  @Query("SELECT t FROM ColumbiaTerm t JOIN ColumbiaDefinition d ON d.term=t WHERE d.context=:context AND (t.name LIKE CONCAT(:string, '%') OR t.metaphone LIKE CONCAT(:metaphone, '%'))")
  Page<ColumbiaTerm> findByNameStartingWithAndMetaphoneStartingWithForSpecificContext(ColumbiaContext context, String string, String metaphone, Pageable pageable);

  Page<ColumbiaTerm> findAllByNameIgnoreCaseOrMetaphone(String name, String metaphone, Pageable pageable);

  Page<ColumbiaTerm> findAllByNameStartingWithOrMetaphoneStartingWith(String name, String metaphone, Pageable pageable);

  Page<ColumbiaTerm> findByAbbreviations(String abbr, Pageable pageable);

  Optional<ColumbiaTerm> findByNameIgnoreCase(String name);

  @Query(value = "SELECT * FROM columbia_term ORDER BY RAND() LIMIT 1;", nativeQuery = true)
  ColumbiaTerm randomTerm();

}
