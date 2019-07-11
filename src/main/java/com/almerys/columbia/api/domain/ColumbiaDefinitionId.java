package com.almerys.columbia.api.domain;

import org.springframework.util.Assert;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ColumbiaDefinitionId implements Serializable {

  private Long termId;

  private Long contextId;

  public ColumbiaDefinitionId() {
  }

  public ColumbiaDefinitionId(Long termId, Long contextId) {
    Assert.notNull(termId, "termId could not be null");
    Assert.notNull(contextId, "contextId could not be null");
    this.termId = termId;
    this.contextId = contextId;
  }

  public Long getTermId() {
    return termId;
  }

  public Long getContextId() {
    return contextId;
  }

  public void setTermId(Long termId) {
    this.termId = termId;
  }

  public void setContextId(Long contextId) {
    this.contextId = contextId;
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof ColumbiaDefinitionId)) {
      return false;
    }
    ColumbiaDefinitionId that = (ColumbiaDefinitionId) obj;
    return Objects.equals(getTermId(), that.getTermId()) && Objects.equals(getContextId(), that.getContextId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getTermId(), getContextId());
  }
}
