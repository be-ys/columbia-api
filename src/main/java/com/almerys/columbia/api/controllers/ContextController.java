package com.almerys.columbia.api.controllers;

import com.almerys.columbia.api.controllers.conf.ApiPageable;
import com.almerys.columbia.api.domain.ColumbiaContext;
import com.almerys.columbia.api.domain.ColumbiaTerm;
import com.almerys.columbia.api.domain.View;
import com.almerys.columbia.api.domain.dto.ContextUpdater;

import com.almerys.columbia.api.services.ContextService;
import com.almerys.columbia.api.services.GlobalService;
import com.almerys.columbia.api.services.Utilities;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;
import org.springframework.data.domain.Page;
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
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.constraints.NotNull;

@RestController
@Api(tags = "Context Controller", description = "Controller to get contexts")
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
  @ApiOperation(value = "Get all contexts")
  @ApiPageable
  public ResponseEntity<Page<ColumbiaContext>> getAllContexts(@ApiIgnore Pageable page) {
    ObjectMapper mapper = new ObjectMapper();
    mapper.enable(MapperFeature.DEFAULT_VIEW_INCLUSION);
    return ResponseEntity.ok(service.getAll(page));
  }

  //filtrage des termes dispos dans un contexte
  @JsonView(View.MinimalDisplay.class)
  @GetMapping(value = "/{contextId}/terms")
  @ApiOperation(value = "Get terms for a specified context")
  @ApiPageable
  public ResponseEntity<Page<ColumbiaTerm>> getTermsInContext(@ApiParam(value = "Id of the context", required = true) @PathVariable("contextId") Long contextId,
                                                              @ApiParam(value = "Search string to display only matching terms") @RequestParam(name = "search", required = false) String search,
                                                              @ApiParam(value = "Define if we disable metaphone comparision") @RequestParam(name = "disableMetaphone", required = false) Boolean disableMetaphone,
                                                              @ApiIgnore Pageable page) {
    if(disableMetaphone==null){disableMetaphone= Boolean.FALSE;}
      return ResponseEntity.ok(service.research(contextId, search, page, disableMetaphone));
  }

  //Récupére un contexte
  @JsonView(View.DefaultDisplay.class)
  @ApiOperation(value = "Get a context")
  @GetMapping(value = "/{contextId}")
  public ResponseEntity<ColumbiaContext> getContextById(@ApiParam(value = "Id of the context", required = true) @NotNull @PathVariable("contextId") Long contextId) {
    ColumbiaContext result = service.getById(contextId);
    return (result == null) ? notFound() : ResponseEntity.ok(result);
  }

  //---------- POST
  //Créer un contexte
  @PostMapping()
  @ApiOperation(value = "Create a new context", authorizations = @Authorization(value="Authentication", scopes = {}))
  public ResponseEntity<Void> createContext(@NotNull @Validated @RequestBody ContextUpdater contextUpdater, @ApiIgnore UriComponentsBuilder ucb) {
    ucb = ucb.scheme(utilities.getScheme());
    ColumbiaContext savedColumbiaContext = service.create(contextUpdater);

    return (savedColumbiaContext == null)
        ? badRequest()
        : ResponseEntity.created(getLocation(ucb, "/contexts/{id}", savedColumbiaContext.getId())).build();
  }

  //---------- PUT
  //Met à jour un contexte
  @PutMapping(value = "/{contextId}")
  @ApiOperation(value = "Update a context", authorizations = @Authorization(value="Authentication", scopes = {}))
  public ResponseEntity<Void> updateContext(@ApiParam(value = "Id of the context", required = true) @NotNull @PathVariable("contextId") Long contextId, @NotNull @Validated @RequestBody ContextUpdater contextUpdater) {
    service.update(contextId, contextUpdater);
    return ResponseEntity.ok().build();
  }

  //---------- DELETE
  //Supprime un contexte
  @DeleteMapping(value = "/{contextId}")
  @ApiOperation(value = "Delete a context", authorizations = @Authorization(value="Authentication", scopes = {}))
  public ResponseEntity<Void> deleteContext(@NotNull @PathVariable("contextId") Long contextId) {
    globalService.deleteContext(contextId);
    return ResponseEntity.ok().build();
  }

}
