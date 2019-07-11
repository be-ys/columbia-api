package com.almerys.columbia.api.domain.dto;

import java.util.Collection;
import java.util.HashSet;

public class TermUpdater {
  private Long id;

  private String name;

  private DefinitionUpdater[] definitionList;

  private Collection<String> abbreviations = new HashSet<>();

  public TermUpdater() {
    //Ignoré
  }

  public TermUpdater(Long id, String name, DefinitionUpdater[] definitionList) {
    this.id = id;
    this.name = name;
    this.definitionList = definitionList;
  }

  public Collection<String> getAbbreviations() {
    return abbreviations;
  }

  public void setAbbreviations(Collection<String> abbreviations) {
    this.abbreviations = (abbreviations == null) ? new HashSet<>() : abbreviations;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public DefinitionUpdater[] getDefinitionList() {
    return definitionList;
  }

  public void setDefinitionList(DefinitionUpdater[] definitionList) {
    this.definitionList = definitionList;
  }
}
