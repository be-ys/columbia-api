package com.almerys.columbia.api.controllers;

import com.almerys.columbia.api.ColumbiaConfiguration;
import com.almerys.columbia.api.domain.ColumbiaDefinition;
import com.almerys.columbia.api.domain.dto.DefinitionUpdater;
import com.almerys.columbia.api.services.DefinitionService;
import com.almerys.columbia.api.services.Utilities;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.constraints.NotNull;

@RestController
public class DefinitionController extends AbstractRestController {
  private final DefinitionService service;
  private final ColumbiaConfiguration columbiaConfiguration;
  private final Utilities utilities;

  public DefinitionController(DefinitionService service, Utilities utilities, ColumbiaConfiguration columbiaConfiguration) {
    this.service = service;
    this.utilities = utilities;
    this.columbiaConfiguration = columbiaConfiguration;
  }

  //---------- GET
  //Récupère la définition d'un terme selon le contexte
  @GetMapping(value = { "/contexts/{contextId}/terms/{termId}", "/terms/{termId}/contexts/{contextId}" })
  public ResponseEntity getTermDefinitionByContext(@PathVariable("contextId") Long contextId, @PathVariable("termId") Long termId) {
    ColumbiaDefinition result = service.getByContextIdAndTermId(contextId, termId);

    return (result == null)
        ? notFound()
        : ResponseEntity.ok(result);
  }

  //---------- POST
  //Créé la définition dans le contexte.
  @PostMapping(value = "/contexts/{contextId}/terms")
  public ResponseEntity createDefinitionInContext(@NotNull @PathVariable("contextId") Long contextId,
      @NotNull @Validated @RequestBody DefinitionUpdater definitionUpdater, UriComponentsBuilder ucb, Authentication authentication) {

    ucb = ucb.scheme(utilities.getScheme());

    if (!hasRole(authentication, columbiaConfiguration.getAdminRoleName(), "CONTEXT_" + contextId)) {
      return forbidden();
    }

    ColumbiaDefinition result = service.create(definitionUpdater, contextId);


    return (result == null)
        ? badRequest()
        : ResponseEntity.created(getLocation(ucb, "/contexts/{contextId}/terms/{termId}", contextId, result.getTerm().getId())).build();
  }

  //---------- PUT
  //Met à jour la définition dans le contexte.
  @PutMapping(value = { "/terms/{termId}/contexts/{contextId}", "/contexts/{contextId}/terms/{termId}" })
  public ResponseEntity updateDefinitionInContext(@NotNull @PathVariable("contextId") Long contextId,
      @NotNull @PathVariable("termId") Long termId, @NotNull @Validated @RequestBody DefinitionUpdater definitionUpdater, Authentication authentication) {

    if (!hasRole(authentication, columbiaConfiguration.getAdminRoleName(), "CONTEXT_" + contextId)) {
      return forbidden();
    }

    ColumbiaDefinition result = service.update(definitionUpdater, contextId, termId);

    return (result == null)
        ? badRequest()
        : ResponseEntity.ok().build();
  }

  //---------- DELETE
  //Supprime la définition d'un terme selon le contexte
  @DeleteMapping(value = { "/contexts/{contextId}/terms/{termId}", "/terms/{termId}/contexts/{contextId}" })
  public ResponseEntity deleteTermDefinitionByContext(@NotNull @PathVariable("contextId") Long contextId,
      @NotNull @PathVariable("termId") Long termId, Authentication authentication) {

    if (!hasRole(authentication, columbiaConfiguration.getAdminRoleName(), "CONTEXT_" + contextId)) {
      return forbidden();
    }

    service.deleteByTermIdAndContextId(termId, contextId);

    return ResponseEntity.ok().build();
  }
}
