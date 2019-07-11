package com.almerys.columbia.api.domain.dto;

public class DefinitionUpdater {

  private ContextUpdater context;

  private TermUpdater term;

  private String definition;

  private TermUpdater[] synonymsTermList = {};

  private TermUpdater[] antonymsTermList = {};

  private TermUpdater[] relatedTermList = {};

  private String[] bibliography = {};

  private String[] sources = {};

  private Boolean gdpr;

  public DefinitionUpdater() {
    //Ignoré
  }

  public Boolean getGdpr() {
    return gdpr;
  }

  public void setGdpr(Boolean gdpr) {
    this.gdpr = ( gdpr == null ) ? Boolean.FALSE : gdpr;
  }

  public ContextUpdater getContext() {
    return context;
  }

  public void setContext(ContextUpdater context) {
    this.context = context;
  }

  public TermUpdater getTerm() {
    return term;
  }

  public void setTerm(TermUpdater term) {
    this.term = term;
  }

  public String getDefinition() {
    return definition;
  }

  public void setDefinition(String definition) {
    this.definition = definition;
  }

  public TermUpdater[] getSynonymsTermList() {
    return synonymsTermList;
  }

  public void setSynonymsTermList(TermUpdater[] synonymsTermList) {
    this.synonymsTermList = (synonymsTermList == null) ? new TermUpdater[] {} : synonymsTermList;

  }

  public TermUpdater[] getAntonymsTermList() {
    return antonymsTermList;
  }

  public void setAntonymsTermList(TermUpdater[] antonymsTermList) {
    this.antonymsTermList = (antonymsTermList == null) ? new TermUpdater[] {} : antonymsTermList;
  }

  public TermUpdater[] getRelatedTermList() {
    return relatedTermList;
  }

  public void setRelatedTermList(TermUpdater[] relatedTermList) {
    this.relatedTermList = (relatedTermList == null) ? new TermUpdater[] {} : relatedTermList;
  }

  public String[] getBibliography() {
    return bibliography;
  }

  public void setBibliography(String[] bibliography) {
    this.bibliography = (bibliography == null) ? new String[] {} : bibliography;
  }

  public String[] getSources() {
    return sources;
  }

  public void setSources(String[] sources) {
    this.sources = (sources == null) ? new String[] {} : sources;
  }
}