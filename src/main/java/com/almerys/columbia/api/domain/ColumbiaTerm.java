package com.almerys.columbia.api.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.springframework.util.Assert;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumns;
import javax.persistence.JoinColumn;
import javax.persistence.ElementCollection;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.apache.commons.codec.language.Metaphone;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Audited
public class ColumbiaTerm {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @JsonView(View.MinimalDisplay.class)
  private Long id;

  @NotBlank(message = "ColumbiaTerm name cannot be blank")
  @Size(max = 250, message = "ColumbiaTerm name is too long.")
  @JsonView(View.MinimalDisplay.class)
  private String name;

  @OneToMany
  @JoinColumns( @JoinColumn(name = "termId", referencedColumnName = "id") )
  @JsonView(View.DefaultDisplay.class)
  @NotAudited
  private Collection<ColumbiaDefinition> definitionList = new ArrayList<>();

  @JsonView(View.MinimalDisplay.class)
  @ElementCollection
  private Collection<String> abbreviations = new ArrayList<>();

  @JsonIgnore
  private String metaphone;

  public ColumbiaTerm() {
    //Ignoré
  }

  public ColumbiaTerm(Long id, String name) {
    Assert.hasText(name, "ColumbiaContext name cannot be empty");
    this.id = id;
    this.name = name;

    Metaphone meta = new Metaphone();
    meta.setMaxCodeLen(10);
    this.metaphone = meta.metaphone(this.name);
  }

  //Getters & setters
  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public Collection<ColumbiaDefinition> getDefinitionList() {
    return definitionList;
  }

  public String getMetaphone() {
    return metaphone;
  }

  public void addDefinition(ColumbiaDefinition def) {
    definitionList.add(def);
  }

  public void setName(String name) {
    Assert.hasText(name, "ColumbiaContext name cannot be empty");
    this.name = name;

    Metaphone meta = new Metaphone();
    meta.setMaxCodeLen(10);
    this.metaphone = meta.metaphone(this.name);
  }

  public Collection<String> getAbbreviations() {
    return abbreviations;
  }

  public void setAbbreviations(Collection<String> abbreviations) {
    this.abbreviations = abbreviations == null ? new HashSet<>() : abbreviations;
  }
}