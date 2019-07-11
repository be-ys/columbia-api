package com.almerys.columbia.api.controllers;

import com.almerys.columbia.api.domain.ColumbiaTerm;
import com.almerys.columbia.api.domain.View;
import com.almerys.columbia.api.services.Utilities;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.almerys.columbia.api.domain.dto.TermUpdater;
import com.almerys.columbia.api.services.GlobalService;
import com.almerys.columbia.api.services.TermService;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/terms")
public class TermController extends AbstractRestController {

  private final TermService service;
  private final GlobalService globalService;
  private final Utilities utilities;

  public TermController(TermService service, GlobalService globalService, Utilities utilities) {
    this.globalService = globalService;
    this.service = service;
    this.utilities = utilities;
  }

  //---------- GET
  //Get terms from all glossary.
  @GetMapping(value = { "/", "" })
  @JsonView(View.MinimalDisplay.class)
  public ResponseEntity getTerms(@RequestParam(value = "search", required = false) String name,
      Pageable page, UriComponentsBuilder ucb) {
    ucb = ucb.scheme(utilities.getScheme());

    if (name != null && name.equals("random")) {
      HttpHeaders headers = new HttpHeaders();
      UriComponents uriComponents = ucb.scheme(utilities.getScheme()).path("/terms/{id}").buildAndExpand(service.getRandomTerm().getId());
      headers.setLocation(uriComponents.toUri());

      return ResponseEntity.status(HttpStatus.TEMPORARY_REDIRECT).headers(headers).build();
    }

    Page result = service.research(name, page);

    return ResponseEntity.ok(result);
  }

  //Récupére un terme
  @GetMapping(value = "/{termId}")
  public ResponseEntity getTermById(@NotNull @PathVariable("termId") Long contextId) {

    ColumbiaTerm result = service.getById(contextId);

    return (result == null)
        ? notFound()
        : ResponseEntity.ok(result);
  }

  //---------- POST
  //Créer un terme
  @PostMapping(value = { "/", "" })
  public ResponseEntity createTerm(@NotNull @Validated @RequestBody TermUpdater updater, UriComponentsBuilder ucb) {
    ColumbiaTerm result = service.create(updater);
    ucb = ucb.scheme(utilities.getScheme());

    return (result == null)
        ? badRequest()
        : ResponseEntity.created(getLocation(ucb, "/terms/{id}", result.getId())).build();

  }

  //---------- PUT
  //Met à jour un terme
  @PutMapping(value = "/{termId}")
  public ResponseEntity updateTerm(@NotNull @PathVariable("termId") Long termId,
      @NotNull @Validated @RequestBody TermUpdater updater) {

    service.update(termId, updater);

    return ResponseEntity.ok().build();

  }

  //---------- DELETE
  //Supprimer une terme
  @DeleteMapping(value = "/{termId}")
  public ResponseEntity deleteTerm(@NotNull @PathVariable("termId") Long termId) {

    globalService.deleteTerm(termId);

    return ResponseEntity.ok().build();

  }

}
