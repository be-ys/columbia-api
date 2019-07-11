package com.almerys.columbia.api.domain;

import com.almerys.columbia.api.listener.ColumbiaRevisionListener;
import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.RevisionEntity;

import javax.persistence.Entity;

@Entity
@RevisionEntity(ColumbiaRevisionListener.class)
public class ColumbiaRevision extends DefaultRevisionEntity {

  private String username;

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  @Override
  public boolean equals(Object obj) {
    if ( !super.equals(obj) ) {
      return false;
    }
    ColumbiaRevision columbiaRevision = (ColumbiaRevision) obj;
    return username.equals(columbiaRevision.getUsername());
  }

  @Override
  public int hashCode() {
    return super.hashCode();
  }
}