package com.almerys.columbia.api.controllers;

import com.almerys.columbia.api.services.HistoryService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/history")
public class HistoryController extends AbstractRestController {

  private final HistoryService service;

  public HistoryController(HistoryService service) {
    this.service = service;
  }

  @GetMapping(value = { "/definitions" })
  public ResponseEntity<List> getLastDefinitionsModifications(Pageable page) {
    List result = service.getLastDefinitionModifications(page);
    return ResponseEntity.status(HttpStatus.OK).body(result);
  }

  @GetMapping(value = { "/contexts" })
  public ResponseEntity<List> getLastContextesModifications(Pageable page) {
    List result = service.getLastContextesModifications(page);
    return ResponseEntity.status(HttpStatus.OK).body(result);
  }

  @GetMapping(value = { "/contexts/{contextId}" })
  public ResponseEntity<List> getLastContextModifications(@PathVariable("contextId") Long contextId, Pageable page) {
    List result = service.getLastContextModifications(contextId, page);
    return ResponseEntity.status(HttpStatus.OK).body(result);
  }

  @GetMapping(value = { "/contexts/{contextId}/terms" })
  public ResponseEntity<List> getLastDefinitionsModificationInContext(@PathVariable("contextId") Long contextId, Pageable page) {
    List result = service.getLastTermsModificationInContext(contextId, page);
    return ResponseEntity.status(HttpStatus.OK).body(result);
  }

  @GetMapping(value = { "/contexts/{contextId}/terms/{termId}", "/terms/{termId}/contexts/{contextId}" })
  public ResponseEntity<List> getLastModificationForSpecificContextAndTerm(@PathVariable("termId") Long termId, @PathVariable("contextId") Long contextId, Pageable page) {
    List result = service.getLastModificationsForTermAndContext(termId, contextId, page);
    return ResponseEntity.status(HttpStatus.OK).body(result);
  }

  @GetMapping(value = { "/terms" })
  public ResponseEntity<List> getLastTermsModifications(Pageable page) {
    List result = service.getLastTermsModifications(page);
    return ResponseEntity.status(HttpStatus.OK).body(result);
  }

  @GetMapping(value = { "/terms/{termId}" })
  public ResponseEntity<List> getLastTermModifications(@PathVariable("termId") Long termId, Pageable page) {
    List result = service.getLastTermModifications(termId, page);
    return ResponseEntity.status(HttpStatus.OK).body(result);
  }

  @GetMapping(value = { "/terms/{termId}/contexts" })
  public ResponseEntity<List> getLastTermModificationInContexts(@PathVariable("termId") Long termId, Pageable page) {
    List result = service.getLastTermModificationInContexts(termId, page);
    return ResponseEntity.status(HttpStatus.OK).body(result);
  }

}
