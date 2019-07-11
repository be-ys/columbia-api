package com.almerys.columbia.api.services;

import com.almerys.columbia.api.domain.ColumbiaTerm;
import com.almerys.columbia.api.repository.TermRepository;
import com.almerys.columbia.api.domain.dto.TermUpdater;
import org.apache.commons.codec.language.Metaphone;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.HashSet;

@Service
public class TermService {
  private final TermRepository repository;

  public TermService(TermRepository repository) {
    this.repository = repository;
  }

  public ColumbiaTerm getRandomTerm() {
    ColumbiaTerm random;

    do {
      random = repository.randomTerm();
    }
    while (random.getDefinitionList().isEmpty());

    return random;
  }

  public Page research(String name, Pageable pageable) {
    if (name == null || name.isEmpty()) {
      return getAll(pageable);
    }

    //Metaphone
    Metaphone meta = new Metaphone();
    meta.setMaxCodeLen(10);


    Page list = (name.endsWith("*"))
        ? repository.findAllByNameStartingWithOrMetaphoneStartingWith(name.substring(0, name.length() - 2), meta.metaphone(name.substring(0, name.length() - 2)), pageable)
        : repository.findAllByNameIgnoreCaseOrMetaphone(name, meta.metaphone(name), pageable);

    if (list.getContent().isEmpty() && pageable.getPageNumber() == 0) {
      return repository.findByAbbreviations(name, pageable);
    } else {
      return list;
    }
  }

  public Page getAll(Pageable pageable) {
    return repository.findAll(pageable);
  }

  public ColumbiaTerm getById(Long id) {
    return repository.findById(id).orElse(null);
  }

  public ColumbiaTerm getByName(String name) {
    return repository.findByNameIgnoreCase(name).orElse(null);
  }

  public ColumbiaTerm create(TermUpdater term) {
    Assert.hasText(term.getName(), "Name cannot be empty.");
    Assert.isNull(repository.findByNameIgnoreCase(term.getName()).orElse(null), "term already exist.");

    ColumbiaTerm createdTerm = new ColumbiaTerm(null, Utilities.escapeHtmlTags(term.getName()));

    Collection<String> abbrs = new HashSet<>();
    term.getAbbreviations().forEach(a -> {
      if (a.length() >= 2) {
        abbrs.add(Utilities.escapeHtmlTags(a));
      }
    });

    createdTerm.setAbbreviations(abbrs);

    return repository.save(createdTerm);
  }

  public ColumbiaTerm update(Long id, TermUpdater updater) {
    Assert.notNull(id, "termId could not be null");
    Assert.hasText(updater.getName(), "name could not be empty");
    Assert.notNull(getById(id), "columbiaTerm does not exist.");

    if (getByName(updater.getName()) != null && !getByName(updater.getName()).getId().equals(id)) {
      throw new IllegalArgumentException("new columbiaTerm name already exists.");
    }

    ColumbiaTerm columbiaTerm = getById(id);
    columbiaTerm.setName(Utilities.escapeHtmlTags(updater.getName()));

    Collection<String> abbrs = new HashSet<>();
    updater.getAbbreviations().forEach(a -> abbrs.add(Utilities.escapeHtmlTags(a)));

    columbiaTerm.setAbbreviations(abbrs);

    return repository.save(columbiaTerm);
  }

  @Transactional
  public void delete(Long id) {
    Assert.notNull(id, "id cannot be null");
    Assert.notNull(getById(id), "context does not exist");

    repository.deleteById(id);
  }
}
