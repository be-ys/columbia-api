package com.almerys.columbia.api.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity
@Table(name = "shedlock")
public class ShedLock {
  @Id
  private String name;

  private Timestamp lock_until;

  private Timestamp locked_at;

  private String locked_by;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Timestamp getLock_until() {
    return lock_until;
  }

  public void setLock_until(Timestamp lock_until) {
    this.lock_until = lock_until;
  }

  public Timestamp getLocked_at() {
    return locked_at;
  }

  public void setLocked_at(Timestamp locked_at) {
    this.locked_at = locked_at;
  }

  public String getLocked_by() {
    return locked_by;
  }

  public void setLocked_by(String locked_by) {
    this.locked_by = locked_by;
  }
}
