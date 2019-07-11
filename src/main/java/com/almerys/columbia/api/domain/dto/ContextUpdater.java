package com.almerys.columbia.api.domain.dto;

public class ContextUpdater {
  private Long id;

  private String description;

  private String name;

  private ContextUpdater parentContext;

  public ContextUpdater() {
    //Ignoré
  }

  public ContextUpdater(Long id, String name, String description, ContextUpdater parentContext) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.parentContext = parentContext;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public ContextUpdater getParentContext() {
    return parentContext;
  }

  public void setParentContext(ContextUpdater parentContext) {
    this.parentContext = parentContext;
  }
}
