package com.almerys.columbia.api.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import org.hibernate.envers.Audited;
import org.springframework.util.Assert;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Audited
public class ColumbiaContext {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @JsonView(View.DefaultDisplay.class)
  private Long id;

  @JsonView(View.DefaultDisplay.class)
  private String description;

  @NotBlank(message = "ColumbiaContext name cannot be blank")
  @Size(max = 250, message = "ColumbiaContext name is too long.")
  @JsonView(View.DefaultDisplay.class)
  private String name;

  @OneToOne
  @JsonIgnoreProperties("termList")
  @JsonView(View.DefaultDisplay.class)
  private ColumbiaContext parentContext;

  public ColumbiaContext() {
    //Ignoré
  }

  public ColumbiaContext(Long id, String name, String description, ColumbiaContext parentContext) {
    Assert.hasText(name, "ColumbiaContext name cannot be empty");
    this.id = id;
    this.name = name;
    this.description = description;
    this.parentContext = parentContext;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) { this.id=id; }

  public String getDescription() {
    return description;
  }

  public String getName() {
    return name;
  }

  public ColumbiaContext getParentContext() {
    return parentContext;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setName(String name) {
    Assert.hasText(name, "ColumbiaContext name cannot be empty");
    this.name = name;
  }

  public void setParentContext(ColumbiaContext parentContext) {
    this.parentContext = parentContext;
  }

}


