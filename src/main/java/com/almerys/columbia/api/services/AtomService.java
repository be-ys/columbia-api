package com.almerys.columbia.api.services;

import com.almerys.columbia.api.domain.ColumbiaDefinition;
import com.almerys.columbia.api.domain.ColumbiaRevision;
import com.rometools.rome.feed.atom.Content;
import com.rometools.rome.feed.atom.Entry;
import com.rometools.rome.feed.atom.Feed;
import com.rometools.rome.feed.atom.Person;
import io.jsonwebtoken.lang.Assert;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.RevisionType;
import org.hibernate.envers.query.AuditEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class AtomService {

  @PersistenceContext
  private EntityManager entityManager;

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public Feed getLastModificationsAsAtomFeed() {
    final AuditReader reader = AuditReaderFactory.get(entityManager);

    List list = reader.createQuery()
                      .forRevisionsOfEntity(ColumbiaDefinition.class, ColumbiaDefinition.class.getName(), false, true)
                      .addOrder(AuditEntity.revisionProperty("id").desc())
                      .setMaxResults(50)
                      .getResultList();

    return builder(list);
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public Feed getLastModificationsAsAtomFeedForSpecificContext(Long contextId) {
    Assert.notNull(contextId, "contextId cannot be null.");

    final AuditReader reader = AuditReaderFactory.get(entityManager);

    List list = reader.createQuery()
                      .forRevisionsOfEntity(ColumbiaDefinition.class, ColumbiaDefinition.class.getName(), false, true)
                      .addOrder(AuditEntity.revisionProperty("id").desc())
                      .add(AuditEntity.property("context_id").eq(contextId))
                      .setMaxResults(50)
                      .getResultList();

    return builder(list);
  }

  /////// METHODES PRIVEES
  private Feed builder(List list) {
    Feed feed = new Feed();
    feed.setFeedType("atom_1.0");
    feed.setTitle("Columbia - ATOM");

    Content subtitle = new Content();
    subtitle.setType("text/plain");
    subtitle.setValue("Updates from Columbia.");
    feed.setSubtitle(subtitle);

    //Le code suivant est probablement un des plus sales possibles.
    //Un grand merci aux développeurs de Hibernate Envers qui n'ont créé aucun type standard.
    feed.setUpdated(((ColumbiaRevision) ((Object[]) list.get(0))[1]).getRevisionDate());

    List<Entry> entries = new ArrayList<>();

    list.forEach(ob -> {
      Entry entry = new Entry();

      Person author = new Person();
      author.setName(((ColumbiaRevision) ((Object[]) ob)[1]).getUsername());
      entry.setAuthors(Collections.singletonList(author));

      entry.setCreated(((ColumbiaRevision) ((Object[]) ob)[1]).getRevisionDate());
      entry.setPublished(((ColumbiaRevision) ((Object[]) ob)[1]).getRevisionDate());
      entry.setUpdated(((ColumbiaRevision) ((Object[]) ob)[1]).getRevisionDate());

      String titleBuilder = "Définition ";
      RevisionType getUpdateType = ((RevisionType) ((Object[]) ob)[2]);

      ColumbiaDefinition def = (ColumbiaDefinition) ((Object[]) ob)[0];
      Content summary = new Content();
      summary.setType("text/plain");

      String build = "La définition du terme " + def.getTerm().getName();

      switch (getUpdateType) {
        case ADD:
          titleBuilder = titleBuilder + "ajoutée au contexte ";
          summary.setValue(build + " a été ajoutée au contexte "
              + def.getContext().getName() + ". Sa définition est : " + def.getDefinition() + ".");
          break;
        case DEL:
          titleBuilder = titleBuilder + "supprimée du contexte ";
          summary.setValue(build + " a été supprimée du contexte "
              + def.getContext().getName() + ".");
          break;
        default:
          titleBuilder = titleBuilder + "modifiée dans le contexte ";
          summary.setValue(build + " a été mise à jour dans le contexte "
              + def.getContext().getName() + ". Sa nouvelle définition est : " + def.getDefinition() + ".");
          break;
      }
      titleBuilder = titleBuilder + def.getContext() .getName() + " : " + def.getTerm().getName();

      entry.setContents(Collections.singletonList(summary));
      entry.setTitle(titleBuilder);

      entries.add(entry);
    });

    feed.setEntries(entries);

    return feed;
  }

}
