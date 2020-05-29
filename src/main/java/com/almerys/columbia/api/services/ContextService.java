package com.almerys.columbia.api.services;

import com.almerys.columbia.api.ColumbiaConfiguration;
import com.almerys.columbia.api.domain.ColumbiaContext;
import com.almerys.columbia.api.domain.ColumbiaTerm;
import com.almerys.columbia.api.repository.ContextRepository;
import com.almerys.columbia.api.repository.TermRepository;
import com.almerys.columbia.api.domain.dto.ContextUpdater;
import org.apache.commons.codec.language.Metaphone;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import javax.transaction.Transactional;

import static com.almerys.columbia.api.services.Utilities.isUpperCase;

@Service
public class ContextService {
  private final ContextRepository repository;
  private final TermRepository termRepository;
  private final ColumbiaConfiguration columbiaConfiguration;

  public ContextService(ContextRepository repository, TermRepository termRepository, ColumbiaConfiguration columbiaConfiguration) {
    this.repository = repository;
    this.termRepository = termRepository;
    this.columbiaConfiguration = columbiaConfiguration;
  }

    public Page<ColumbiaTerm> research(Long idcontext, String name, Pageable pageable) {
        return research(idcontext, name, pageable, Boolean.FALSE);
    }


  public Page<ColumbiaTerm> research(Long idcontext, String name, Pageable pageable, Boolean disableMetaphone) {
    Assert.notNull(getById(idcontext), "ColumbiaContext does not exist");

    ColumbiaContext context = getById(idcontext);

      name = (name == null) ? "" : name;

      if (name.isEmpty()) {
          return termRepository.findForSpecificContext(context, pageable);
      }

      //Metaphone
      Metaphone meta = new Metaphone();
      meta.setMaxCodeLen(10);

      Page<ColumbiaTerm> list;
      boolean isStart=false;

      if(isUpperCase(name)){
          return termRepository.findByAbbreviations(name, pageable);
      }

      if(name.endsWith("*")) {
          name=name.substring(0, name.length() - 1);
          isStart=true;
      }

          if(isStart){
              if(disableMetaphone) {
                  list= termRepository.findByNameStartingWithForSpecificContext(context, name, pageable);
              } else {
                  list= termRepository.findByNameStartingWithAndMetaphoneStartingWithForSpecificContext(context, name, meta.metaphone(name), pageable);
              }
          } else {
              if(disableMetaphone) {
                  list= termRepository.findByNameForSpecificContext(context, name, pageable);
              } else {
                  list= termRepository.findByNameAndMetaphoneForSpecificContext(context, name, meta.metaphone(name), pageable);
              }
          }

      return list;
  }

  public ColumbiaContext update(Long id, ContextUpdater contextUpdater) {
    Assert.hasText(contextUpdater.getName(), "ColumbiaContext must have a name.");

    if (contextUpdater.getParentContext() != null && id.equals(contextUpdater.getParentContext().getId())) {
      throw new IllegalArgumentException("ColumbiaContext could not be it's own parent");
    }

    //On vérifie que ça ne fait pas une bonne vieille boucle des familles.
    if (contextUpdater.getParentContext() != null) {
      Long onCheck = contextUpdater.getParentContext().getId();
      while (onCheck != null) {
        ColumbiaContext columbiaContext = repository.findById(onCheck).orElse(null);
        if (columbiaContext != null && columbiaContext.getParentContext() != null) {
          onCheck = columbiaContext.getParentContext().getId();
          if (onCheck.equals(id)) {
            throw new IllegalArgumentException("Cyclic dependency ! Aborting.");
          }
        } else {
          onCheck = null;
        }
      }
    }

    ColumbiaContext currentColumbiaContext = repository.findById(id).orElse(null);

    if (currentColumbiaContext == null) {
      throw new IllegalArgumentException("context does not exist in database");
    }

    currentColumbiaContext.setParentContext(null);
    currentColumbiaContext.setDescription(Utilities.escapeHtmlTags(contextUpdater.getDescription()));
    currentColumbiaContext.setName(Utilities.escapeHtmlTags(contextUpdater.getName()));

    ColumbiaContext parentColumbiaContext = null;

    //Check if parent context exist.
    if (contextUpdater.getParentContext() != null && contextUpdater.getParentContext().getId() != null) {

      parentColumbiaContext = getById(contextUpdater.getParentContext().getId());

      if (parentColumbiaContext == null) {
        throw new IllegalArgumentException("New parent context does not exist.");
      }

      if (!isValidParent(parentColumbiaContext, columbiaConfiguration.getMaxContextLevel() - 1)) {
        throw new IllegalArgumentException("ColumbiaContext is already a parent; it could not be a subcontext.");
      }

      currentColumbiaContext.setParentContext(parentColumbiaContext);
    }

    if (getByNameAndParentContext(contextUpdater.getName(), parentColumbiaContext) != null
        && !getByNameAndParentContext(contextUpdater.getName(), null).getId().equals(currentColumbiaContext.getId())) {
      throw new IllegalArgumentException("New parent already have a subdirectory called like this.");
    }

    return repository.save(currentColumbiaContext);
  }

  public ColumbiaContext create(ContextUpdater contextUpdater) {
    Assert.hasText(contextUpdater.getName(), "ColumbiaContext must have a name.");

    ColumbiaContext parentColumbiaContext = null;

    if (contextUpdater.getParentContext() != null) {
      parentColumbiaContext = repository.findById(contextUpdater.getParentContext().getId()).orElse(null);

      if (parentColumbiaContext == null) {
        throw new IllegalArgumentException("parent columbiaContext does not exist");
      }

      if (!isValidParent(parentColumbiaContext, columbiaConfiguration.getMaxContextLevel() - 1)) {
        throw new IllegalArgumentException("parent is already a subcontext.");
      }

      if (repository.findByNameIgnoreCaseAndParentContextId(contextUpdater.getName(), contextUpdater.getParentContext().getId()).isPresent()) {
        throw new IllegalArgumentException("columbiaContext already exists in specified columbiaContext.");
      }
    } else {
      if (repository.findByNameIgnoreCaseAndParentContextId(contextUpdater.getName(), null).isPresent()) {
        throw new IllegalArgumentException("columbiaContext already exists");
      }
    }

    ColumbiaContext columbiaContext = new ColumbiaContext();
    columbiaContext.setName(Utilities.escapeHtmlTags(contextUpdater.getName()));
    columbiaContext.setDescription(Utilities.escapeHtmlTags(contextUpdater.getDescription()));
    columbiaContext.setParentContext(parentColumbiaContext);

    return repository.save(columbiaContext);
  }

  public Iterable<ColumbiaContext> getAll() {
    return repository.findAll();
  }

  public Page<ColumbiaContext> getAll(Pageable pageable) {
    return repository.findAll(pageable);
  }

  public ColumbiaContext getById(Long id) {
    Assert.notNull(id, "id cannot be null");

    return repository.findById(id).orElse(null);
  }

  public ColumbiaContext getByNameAndParentContext(String name, ColumbiaContext co) {
    Assert.notNull(name, "name cannot be empty");

    return repository.findByNameIgnoreCaseAndParentContextId(name, ((co == null) ? null : co.getId())).orElse(null);
  }

  @Transactional
  public void delete(Long id) {
    Assert.notNull(id, "id cannot be null");
    Assert.notNull(getById(id), "context does not exist");
    Assert.isTrue(!repository.isParent(id), "context is a parent");

    ColumbiaContext toDelete = getById(id);
    repository.delete(toDelete);
  }

  public boolean isParent(Long contextId) {
    return repository.isParent(contextId);
  }

  public boolean isValidParent(ColumbiaContext columbiaContext, int level) {
    if (level == 1) {
      return (columbiaContext.getParentContext() == null);
    }

    if (columbiaContext.getParentContext() == null) {
      return true;
    }

    return isValidParent(columbiaContext.getParentContext(), level - 1);
  }

}