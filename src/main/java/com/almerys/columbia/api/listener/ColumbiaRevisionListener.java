package com.almerys.columbia.api.listener;

import com.almerys.columbia.api.domain.ColumbiaRevision;
import org.hibernate.envers.RevisionListener;
import org.springframework.security.core.context.SecurityContextHolder;

public class ColumbiaRevisionListener implements RevisionListener {

  @Override
  public void newRevision(Object revisionEntity) {
    ColumbiaRevision rev = (ColumbiaRevision) revisionEntity;
    rev.setUsername(SecurityContextHolder.getContext().getAuthentication().getName());
  }
}