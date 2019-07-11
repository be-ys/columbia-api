package com.almerys.columbia.api.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import org.hibernate.envers.Audited;
import org.springframework.util.Assert;

import javax.persistence.Entity;
import javax.persistence.EmbeddedId;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.ManyToMany;
import javax.persistence.JoinColumn;
import javax.persistence.ElementCollection;
import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Collection;
import java.util.HashSet;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Audited
public class ColumbiaDefinition {

  @EmbeddedId
  @JsonIgnore
  private ColumbiaDefinitionId id = new ColumbiaDefinitionId();

  @ManyToOne
  @NotNull(message = "ColumbiaContext must exist")
  @MapsId("contextId")
  @JoinColumn(name = "contextId")
  @JsonIgnoreProperties({ "termList" })
  @JsonView(View.DefaultDisplay.class)
  private ColumbiaContext context;

  @ManyToOne
  @NotNull(message = "ColumbiaTerm must exist")
  @MapsId("termId")
  @JoinColumn(name = "termId")
  @JsonIgnoreProperties({ "definitionList", "abbreviations" })
  @JsonView(View.DefaultDisplay.class)
  private ColumbiaTerm term;

  @NotBlank(message = "ColumbiaDefinition cannot be blank")
  @Size(max = 25000, message = "ColumbiaDefinition is too long.")
  @Lob
  @JsonView(View.DefaultDisplay.class)
  private String definition;

  @ManyToMany
  @JsonIgnoreProperties("definitionList")
  @JsonView(View.DefaultDisplay.class)
  private Collection<ColumbiaTerm> synonymsTermList = new HashSet<>();

  @ManyToMany
  @JsonIgnoreProperties("definitionList")
  @JsonView(View.DefaultDisplay.class)
  private Collection<ColumbiaTerm> antonymsTermList = new HashSet<>();

  @ManyToMany
  @JsonIgnoreProperties("definitionList")
  @JsonView(View.DefaultDisplay.class)
  private Collection<ColumbiaTerm> relatedTermList = new HashSet<>();

  @ElementCollection
  @Column(length = 500)
  @JsonView(View.DefaultDisplay.class)
  private Collection<String> bibliography = new HashSet<>();

  @ElementCollection
  @Column(length = 500)
  @JsonView(View.DefaultDisplay.class)
  private Collection<String> sources = new HashSet<>();

  private Boolean gdpr = false;

  public ColumbiaDefinition() {
    //Ignoré
  }

  public void setId(ColumbiaDefinitionId id) {
    this.id = id;
  }

  //Non-standard methods
  public void addSynonym(ColumbiaTerm columbiaTerm) {
    Assert.notNull(columbiaTerm, "term cannot be null.");
    this.synonymsTermList.add(columbiaTerm);
  }

  public void addAntonym(ColumbiaTerm columbiaTerm) {
    Assert.notNull(columbiaTerm, "term cannot be null.");
    this.antonymsTermList.add(columbiaTerm);
  }

  public void addRelated(ColumbiaTerm columbiaTerm) {
    Assert.notNull(columbiaTerm, "term cannot be null.");
    this.relatedTermList.add(columbiaTerm);
  }

  public void addBibliography(String string) {
    Assert.hasText(string, "cannot be empty");
    this.bibliography.add(string);
  }

  public void addSources(String string) {
    Assert.hasText(string, "cannot be empty");
    this.sources.add(string);
  }

  public void removeSynonym(ColumbiaTerm columbiaTerm) {
    Assert.notNull(columbiaTerm, "term cannot be null.");
    this.synonymsTermList.removeIf(n -> (n.getId().equals(columbiaTerm.getId())));
  }

  public void removeAntonym(ColumbiaTerm columbiaTerm) {
    Assert.notNull(columbiaTerm, "term cannot be null.");
    this.antonymsTermList.removeIf(n -> (n.getId().equals(columbiaTerm.getId())));
  }

  public void removeRelated(ColumbiaTerm columbiaTerm) {
    Assert.notNull(columbiaTerm, "term cannot be null.");
    this.relatedTermList.removeIf(n -> (n.getId().equals(columbiaTerm.getId())));
  }

  public void removeBibliography(String string) {
    Assert.hasText(string, "cannot be empty");
    this.bibliography.remove(string);
  }

  public void removeSources(String string) {
    Assert.hasText(string, "cannot be empty");
    this.sources.remove(string);
  }

  //Getters & setters

  public Boolean getGdpr() {
    return gdpr;
  }

  public void setGdpr(Boolean gdpr) {
    this.gdpr = ( gdpr == null ) ? Boolean.FALSE : gdpr;
  }

  public ColumbiaTerm getTerm() {
    return term;
  }

  public void setTerm(ColumbiaTerm term) {
    Assert.notNull(term, "term cannot be null.");
    this.id.setTermId(term.getId());
    this.term = term;
  }

  public ColumbiaContext getContext() {
    return context;
  }

  public void setContext(ColumbiaContext context) {
    Assert.notNull(context, "context cannot be null.");
    this.id.setContextId(context.getId());
    this.context = context;
  }

  public ColumbiaDefinitionId getId() {
    return this.id;
  }

  public String getDefinition() {
    return definition;
  }

  public void setDefinition(String definition) {
    Assert.hasText(definition, "definition cannot be empty");
    this.definition = definition;
  }

  public Collection<ColumbiaTerm> getSynonymsTermList() {
    return synonymsTermList;
  }

  public void setSynonymsTermList(Collection<ColumbiaTerm> synonymsTermList) {
    this.synonymsTermList = (synonymsTermList != null) ? synonymsTermList : new HashSet<>();
  }

  public Collection<ColumbiaTerm> getAntonymsTermList() {
    return antonymsTermList;
  }

  public void setAntonymsTermList(Collection<ColumbiaTerm> antonymsTermList) {
    this.antonymsTermList = (antonymsTermList != null) ? antonymsTermList : new HashSet<>();
  }

  public Collection<ColumbiaTerm> getRelatedTermList() {
    return relatedTermList;
  }

  public void setRelatedTermList(Collection<ColumbiaTerm> relatedTermList) {
    this.relatedTermList = (relatedTermList != null) ? relatedTermList : new HashSet<>();
  }

  public Collection<String> getBibliography() {
    return bibliography;
  }

  public void setBibliography(Collection<String> bibliography) {
    this.bibliography = (bibliography != null) ? bibliography : new HashSet<>();
  }

  public Collection<String> getSources() {
    return sources;
  }

  public void setSources(Collection<String> sources) {
    this.sources = (sources != null) ? sources : new HashSet<>();
  }
}