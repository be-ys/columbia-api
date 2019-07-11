package com.almerys.columbia.api.services;

import com.almerys.columbia.api.domain.ColumbiaContext;
import com.almerys.columbia.api.domain.ColumbiaDefinition;
import com.almerys.columbia.api.domain.ColumbiaTerm;
import io.jsonwebtoken.lang.Assert;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.query.AuditEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Date;
import java.util.List;

@Service
public class HistoryService {

  @PersistenceContext
  private EntityManager entityManager;

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public List getPastWeekDefinitionModifications(Long contextId) {
    Assert.notNull(contextId, "contextId cannot be null.");

    final AuditReader reader = AuditReaderFactory.get(entityManager);

    return reader.createQuery()
                 .forRevisionsOfEntity(ColumbiaDefinition.class, ColumbiaDefinition.class.getName(), true, false)
                 .addOrder(AuditEntity.revisionProperty("id").desc())
                 .add(AuditEntity.property("context_id").eq(contextId))
                 .add(AuditEntity.revisionProperty("timestamp").between(new Date().getTime() - 604800000, new Date().getTime()))
                 .getResultList();
  }

  public List getLastDefinitionModifications(Pageable page) {
    AuditReader reader = AuditReaderFactory.get(entityManager);

    return reader.createQuery()
                 .forRevisionsOfEntity(ColumbiaDefinition.class, ColumbiaDefinition.class.getName(), false, true)
                 .addOrder(AuditEntity.revisionProperty("id").desc())
                 .setFirstResult((int) page.getOffset())
                 .setMaxResults( page.getPageSize())
                 .getResultList();
  }

  public List getLastTermsModifications(Pageable page) {
    AuditReader reader = AuditReaderFactory.get(entityManager);

    return reader.createQuery()
                 .forRevisionsOfEntity(ColumbiaTerm.class, ColumbiaTerm.class.getName(), false, true)
                 .addOrder(AuditEntity.revisionProperty("id").desc())
                 .setFirstResult((int) page.getOffset())
                 .setMaxResults( page.getPageSize())
                 .getResultList();
  }

  public List getLastTermModifications(Long termId, Pageable page) {
    Assert.notNull(termId, "termId cannot be null.");

    AuditReader reader = AuditReaderFactory.get(entityManager);

    return reader.createQuery()
                 .forRevisionsOfEntity(ColumbiaTerm.class, ColumbiaTerm.class.getName(), false, true)
                 .add(AuditEntity.property("id").eq(termId))
                 .addOrder(AuditEntity.revisionProperty("id").desc())
                 .setFirstResult((int) page.getOffset())
                 .setMaxResults( page.getPageSize())
                 .getResultList();
  }

  public List getLastContextesModifications(Pageable page) {
    AuditReader reader = AuditReaderFactory.get(entityManager);

    return reader.createQuery()
                 .forRevisionsOfEntity(ColumbiaContext.class, ColumbiaContext.class.getName(), false, true)
                 .addOrder(AuditEntity.revisionProperty("id").desc())
                 .setFirstResult((int) page.getOffset())
                 .setMaxResults( page.getPageSize())
                 .getResultList();
  }

  public List getLastModificationsForTermAndContext(Long termId, Long contextId, Pageable page) {
    Assert.notNull(contextId, "contextId cannot be null.");
    Assert.notNull(termId, "termId cannot be null.");

    AuditReader reader = AuditReaderFactory.get(entityManager);

    return reader.createQuery()
                 .forRevisionsOfEntity(ColumbiaDefinition.class, ColumbiaDefinition.class.getName(), false, true)
                 .add(AuditEntity.property("context_id").eq(contextId))
                 .add(AuditEntity.property("term_id").eq(termId))
                 .addOrder(AuditEntity.revisionProperty("id").desc())
                 .setFirstResult((int) page.getOffset())
                 .setMaxResults( page.getPageSize())
                 .getResultList();
  }

  public List getLastContextModifications(Long contextId, Pageable page) {
    Assert.notNull(contextId, "contextId cannot be null.");

    AuditReader reader = AuditReaderFactory.get(entityManager);

    return reader.createQuery()
                 .forRevisionsOfEntity(ColumbiaContext.class, ColumbiaContext.class.getName(), false, true)
                 .add(AuditEntity.property("id").eq(contextId))
                 .addOrder(AuditEntity.revisionProperty("id").desc())
                 .setFirstResult((int) page.getOffset())
                 .setMaxResults( page.getPageSize())
                 .getResultList();
  }

  public List getLastTermsModificationInContext(Long contextId, Pageable page) {
    Assert.notNull(contextId, "contextId cannot be null.");

    AuditReader reader = AuditReaderFactory.get(entityManager);

    return reader.createQuery()
                 .forRevisionsOfEntity(ColumbiaDefinition.class, ColumbiaDefinition.class.getName(), false, true)
                 .add(AuditEntity.property("context_id").eq(contextId))
                 .addOrder(AuditEntity.revisionProperty("id").desc())
                 .setFirstResult((int) page.getOffset())
                 .setMaxResults( page.getPageSize())
                 .getResultList();
  }

  public List getLastTermModificationInContexts(Long termId, Pageable page) {
    Assert.notNull(termId, "termId cannot be null.");

    AuditReader reader = AuditReaderFactory.get(entityManager);

    return reader.createQuery()
                 .forRevisionsOfEntity(ColumbiaDefinition.class, ColumbiaDefinition.class.getName(), false, true)
                 .add(AuditEntity.property("term_id").eq(termId))
                 .addOrder(AuditEntity.revisionProperty("id").desc())
                 .setFirstResult((int) page.getOffset())
                 .setMaxResults( page.getPageSize())
                 .getResultList();
  }
}
