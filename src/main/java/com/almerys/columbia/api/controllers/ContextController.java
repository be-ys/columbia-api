package com.almerys.columbia.api.controllers;

import com.almerys.columbia.api.domain.ColumbiaContext;
import com.almerys.columbia.api.domain.View;
import com.almerys.columbia.api.domain.dto.ContextUpdater;

import com.almerys.columbia.api.services.ContextService;
import com.almerys.columbia.api.services.GlobalService;
import com.almerys.columbia.api.services.Utilities;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.Pageable;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/contexts")
public class ContextController extends AbstractRestController {

  private final ContextService service;
  private final GlobalService globalService;
  private final Utilities utilities;

  public ContextController(ContextService service, GlobalService globalService, Utilities utilities) {
    this.globalService = globalService;
    this.service = service;
    this.utilities = utilities;
  }

  //---------- GET
  //Récupére tout les contextes
  @JsonView(View.DefaultDisplay.class)
  @GetMapping()
  public ResponseEntity getAllContexts(Pageable page) {
    ObjectMapper mapper = new ObjectMapper();
    mapper.enable(MapperFeature.DEFAULT_VIEW_INCLUSION);
    return ResponseEntity.ok(service.getAll(page));
  }

  //filtrage des termes dispos dans un contexte
  @JsonView(View.MinimalDisplay.class)
  @GetMapping(value = "/{contextId}/terms")
  public ResponseEntity getTermsInContext(@PathVariable("contextId") Long contextId, @RequestParam(name = "search", required = false) String search, Pageable page) {
    return ResponseEntity.ok(service.research(contextId, search, page));
  }

  //Récupére un contexte
  @JsonView(View.DefaultDisplay.class)
  @GetMapping(value = "/{contextId}")
  public ResponseEntity getContextById(@NotNull @PathVariable("contextId") Long contextId) {
    ColumbiaContext result = service.getById(contextId);
    return (result == null) ? notFound() : ResponseEntity.ok(result);
  }

  //---------- POST
  //Créer un contexte
  @PostMapping()
  public ResponseEntity createContext(@NotNull @Validated @RequestBody ContextUpdater contextUpdater, UriComponentsBuilder ucb) {
    ucb = ucb.scheme(utilities.getScheme());
    ColumbiaContext savedColumbiaContext = service.create(contextUpdater);

    return (savedColumbiaContext == null)
        ? badRequest()
        : ResponseEntity.created(getLocation(ucb, "/contexts/{id}", savedColumbiaContext.getId())).build();
  }

  //---------- PUT
  //Met à jour un contexte
  @PutMapping(value = "/{contextId}")
  public ResponseEntity updateContext(@NotNull @PathVariable("contextId") Long contextId, @NotNull @Validated @RequestBody ContextUpdater contextUpdater) {
    service.update(contextId, contextUpdater);
    return ResponseEntity.ok().build();
  }

  //---------- DELETE
  //Supprime un contexte
  @DeleteMapping(value = "/{contextId}")
  public ResponseEntity<Void> deleteContext(@NotNull @PathVariable("contextId") Long contextId) {
    globalService.deleteContext(contextId);
    return ResponseEntity.ok().build();
  }

}
