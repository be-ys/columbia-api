package com.almerys.columbia.api.services;

import com.almerys.columbia.api.domain.ColumbiaContext;
import com.almerys.columbia.api.domain.ColumbiaDefinition;
import com.almerys.columbia.api.repository.DefinitionRepository;
import com.almerys.columbia.api.domain.ColumbiaTerm;
import com.almerys.columbia.api.domain.dto.DefinitionUpdater;
import com.almerys.columbia.api.domain.dto.TermUpdater;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.transaction.Transactional;
import java.util.Collection;

@Service
public class DefinitionService {
  private final DefinitionRepository repository;
  private final ContextService contextService;
  private final TermService termService;

  public DefinitionService(DefinitionRepository repository, ContextService contextService, TermService termService) {
    this.repository = repository;
    this.contextService = contextService;
    this.termService = termService;
  }

  public Iterable getByContextId(Long contextId) {
    Assert.notNull(contextId, "contextId could not be null !");
    return repository.findAllByContextId(contextId);
  }

  public void removeAllReferences(ColumbiaTerm columbiaTerm) {
    Assert.notNull(columbiaTerm, "termId could not be null !");
    //Suppression de tout les antonymes
    Iterable<ColumbiaDefinition> defs = repository.findAllByAntonymsTermListContains(columbiaTerm);
    defs.forEach(e -> {
      Collection<ColumbiaTerm> terms = e.getAntonymsTermList();
      terms.remove(columbiaTerm);
      e.setAntonymsTermList(terms);
      repository.save(e);
    });

    //Suppression de tout les synonymes
    defs = repository.findAllBySynonymsTermListContains(columbiaTerm);
    defs.forEach(e -> {
      Collection<ColumbiaTerm> terms = e.getSynonymsTermList();
      terms.remove(columbiaTerm);
      e.setSynonymsTermList(terms);
      repository.save(e);
    });

    //Suppression de tout les connexes
    defs = repository.findAllByRelatedTermListContains(columbiaTerm);
    defs.forEach(e -> {
      Collection<ColumbiaTerm> terms = e.getRelatedTermList();
      terms.remove(columbiaTerm);
      e.setRelatedTermList(terms);
      repository.save(e);
    });
  }

  @Transactional
  public void deleteAllByContextId(Long contextId) {
    Assert.notNull(contextId, "contextId could not be null");
    if (contextService.getById(contextId) == null) {
      throw new IllegalArgumentException("ColumbiaContext does not exists");
    }

    if (contextService.isParent(contextId)) {
      throw new IllegalArgumentException("ColumbiaContext is parent, cannot delete.");
    }

    repository.deleteAllByContextId(contextId);
  }

  @Transactional
  public void deleteAllByTermId(Long termId) {
    Assert.notNull(termId, "contextId could not be null");

    repository.deleteAllByTermId(termId);
  }

  @Transactional
  public void deleteByTermIdAndContextId(Long termId, Long contextId) {
    Assert.notNull(termId, "termId could not be null");
    Assert.notNull(contextId, "contextId could not be null");

    repository.deleteByTermIdAndContextId(termId, contextId);
  }

  public ColumbiaDefinition update(DefinitionUpdater definitionUpdater, Long contextId, Long termId) {
    Assert.notNull(contextId, "contextId could not be null");
    Assert.hasText(definitionUpdater.getDefinition(), "columbiaDefinition cannot be null");
    Assert.notNull(contextService.getById(contextId), "context does not exist");
    Assert.notNull(termService.getById(termId), "term does not exist");
    Assert.notNull(termId, "termId could not be null");
    Assert.isTrue(repository.findByContextIdAndTermId(contextId, termId).isPresent(), "ColumbiaDefinition for this term in this context does not exist");

    ColumbiaDefinition columbiaDefinition = new ColumbiaDefinition();
    columbiaDefinition.setContext(contextService.getById(contextId));
    columbiaDefinition.setTerm(termService.getById(termId));
    columbiaDefinition.setDefinition(Utilities.escapeHtmlTags(definitionUpdater.getDefinition()));

    columbiaDefinition.setGdpr(definitionUpdater.getGdpr());

    return repository.save(insertArrays(definitionUpdater, columbiaDefinition));

  }

  public ColumbiaDefinition create(DefinitionUpdater definitionUpdater, Long contextId) {
    Assert.notNull(contextId, "contextId could not be null.");
    Assert.hasText(definitionUpdater.getDefinition(), "columbiaDefinition cannot be null.");
    Assert.notNull(contextService.getById(contextId), "context does not exist");

    if (definitionUpdater.getTerm() == null || definitionUpdater.getTerm().getId() == null) {
      throw new IllegalArgumentException("ColumbiaTerm id could not be null.");
    }

    ColumbiaTerm columbiaTerm = termService.getById(definitionUpdater.getTerm().getId());

    if (columbiaTerm == null) {
      throw new IllegalArgumentException("ColumbiaTerm does not exist in database.");
    }

    if (repository.findByContextIdAndTermId(contextId, columbiaTerm.getId()).isPresent()) {
      throw new IllegalArgumentException("ColumbiaDefinition for this context already exists; please use PUT method for update.");
    }

    ColumbiaContext definitionColumbiaContext = contextService.getById(contextId);

    //Si on est ici, c'est que le terme est récupéré/créé, que le contexte existe, et qu'il n'existe pas de définition pour le contexte en cours.
    ColumbiaDefinition columbiaDefinition = new ColumbiaDefinition();
    columbiaDefinition.setContext(definitionColumbiaContext);
    columbiaDefinition.setTerm(columbiaTerm);
    columbiaDefinition.setDefinition(Utilities.escapeHtmlTags(definitionUpdater.getDefinition()));

    columbiaDefinition.setGdpr(definitionUpdater.getGdpr());
    insertArrays(definitionUpdater, columbiaDefinition);

    //Vérification : Si le contexte dans lequel on insère est un fils, on regarde si le parent direct ne comporte pas la même définition.
    if (definitionColumbiaContext.getParentContext() != null) {
      ColumbiaDefinition parentColumbiaDefinition = repository.findByContextIdAndTermId(definitionColumbiaContext.getParentContext().getId(),
          columbiaTerm.getId()).orElse(null);

      if (parentColumbiaDefinition != null
          && parentColumbiaDefinition.getDefinition().equals(definitionUpdater.getDefinition())
          && parentColumbiaDefinition.getAntonymsTermList().equals(columbiaDefinition.getAntonymsTermList())
          && parentColumbiaDefinition.getSynonymsTermList().equals(columbiaDefinition.getSynonymsTermList())) {
        throw new IllegalArgumentException("ColumbiaDefinition is the same in parent context. Abort.");
      }
    }

    return repository.save(columbiaDefinition);
  }

  public ColumbiaDefinition getByContextIdAndTermId(Long contextId, Long termId) {
    Assert.notNull(contextId, "contextId cannot be null");
    Assert.notNull(termId, "termId cannot be null");

    return repository.findByContextIdAndTermId(contextId, termId).orElse(null);
  }

  /* METHODES PRIVEES */
  private ColumbiaDefinition insertArrays(DefinitionUpdater definitionUpdater, ColumbiaDefinition columbiaDefinition) {
    for (String a : definitionUpdater.getBibliography()) {


      columbiaDefinition.addBibliography(Utilities.escapeHtmlTags(a));
    }

    for (String a : definitionUpdater.getSources()) {
      columbiaDefinition.addSources(Utilities.escapeHtmlTags(a));
    }

    for (TermUpdater a : definitionUpdater.getSynonymsTermList()) {
      if (a.getId() != null && termService.getById(a.getId()) != null) {
        columbiaDefinition.addSynonym(termService.getById(a.getId()));
      } else {
        throw new IllegalArgumentException("ColumbiaTerm " + a + " in Synonyms list does not exist in the glossary.");
      }
    }

    for (TermUpdater a : definitionUpdater.getAntonymsTermList()) {
      if (a.getId() != null && termService.getById(a.getId()) != null) {
        columbiaDefinition.addAntonym(termService.getById(a.getId()));
      } else {
        throw new IllegalArgumentException("ColumbiaTerm " + a + " in Antonyms list does not exist in the glossary.");
      }
    }

    for (TermUpdater a : definitionUpdater.getRelatedTermList()) {
      if (a.getId() != null && termService.getById(a.getId()) != null) {
        columbiaDefinition.addRelated(termService.getById(a.getId()));
      } else {
        throw new IllegalArgumentException("ColumbiaTerm " + a + " in Related list does not exist in the glossary.");
      }
    }

    return columbiaDefinition;
  }
}
