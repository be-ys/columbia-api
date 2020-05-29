package com.almerys.columbia.api.controllers;

import com.almerys.columbia.api.controllers.conf.ApiPageable;
import com.almerys.columbia.api.services.HistoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

@RestController
@Api(tags = "History controller", description = "Get history for entities. Use Hibernate Envers. Reponse scheme could not be defined here, so please have a look at the documentation.")
@RequestMapping("/history")
public class HistoryController extends AbstractRestController {

  private final HistoryService service;

  public HistoryController(HistoryService service) {
    this.service = service;
  }

  @GetMapping(value = { "/definitions" })
  @ApiOperation(value = "Get definitions history")
  @ApiPageable
  public ResponseEntity<List> getLastDefinitionsModifications(@ApiIgnore Pageable page) {
    List result = service.getLastDefinitionModifications(page);
    return ResponseEntity.status(HttpStatus.OK).body(result);
  }

  @GetMapping(value = { "/contexts" })
  @ApiOperation(value = "Get contexts history")
  @ApiPageable
  public ResponseEntity<List> getLastContextesModifications(@ApiIgnore Pageable page) {
    List result = service.getLastContextesModifications(page);
    return ResponseEntity.status(HttpStatus.OK).body(result);
  }

  @GetMapping(value = { "/contexts/{contextId}" })
  @ApiOperation(value = "Get last updates for a specified context")
  @ApiPageable
  public ResponseEntity<List> getLastContextModifications(@ApiParam(value = "Context ID", required = true) @PathVariable("contextId") Long contextId, @ApiIgnore Pageable page) {
    List result = service.getLastContextModifications(contextId, page);
    return ResponseEntity.status(HttpStatus.OK).body(result);
  }

  @GetMapping(value = { "/contexts/{contextId}/terms" })
  @ApiOperation(value = "Get last updates for terms in a context")
  @ApiPageable
  public ResponseEntity<List> getLastDefinitionsModificationInContext(@ApiParam(value = "Context ID", required = true) @PathVariable("contextId") Long contextId, @ApiIgnore  Pageable page) {
    List result = service.getLastTermsModificationInContext(contextId, page);
    return ResponseEntity.status(HttpStatus.OK).body(result);
  }

  @GetMapping(value = { "/contexts/{contextId}/terms/{termId}", "/terms/{termId}/contexts/{contextId}" })
  @ApiPageable
  @ApiOperation(value = "Get last updates for a specified definition")
  public ResponseEntity<List> getLastModificationForSpecificContextAndTerm(@ApiParam(value = "Term ID", required = true) @PathVariable("termId") Long termId,
                                                                           @ApiParam(value = "Context ID", required = true) @PathVariable("contextId") Long contextId,
                                                                           @ApiIgnore Pageable page) {
    List result = service.getLastModificationsForTermAndContext(termId, contextId, page);
    return ResponseEntity.status(HttpStatus.OK).body(result);
  }

  @GetMapping(value = { "/terms" })
  @ApiPageable
  @ApiOperation("Get last terms updates.")
  public ResponseEntity<List> getLastTermsModifications(@ApiIgnore Pageable page) {
    List result = service.getLastTermsModifications(page);
    return ResponseEntity.status(HttpStatus.OK).body(result);
  }

  @GetMapping(value = { "/terms/{termId}" })
  @ApiPageable
  @ApiOperation("Get updates for a specified term.")
  public ResponseEntity<List> getLastTermModifications(@ApiParam(value = "Term ID", required = true) @PathVariable("termId") Long termId, @ApiIgnore Pageable page) {
    List result = service.getLastTermModifications(termId, page);
    return ResponseEntity.status(HttpStatus.OK).body(result);
  }

  @GetMapping(value = { "/terms/{termId}/contexts" })
  @ApiPageable
  @ApiOperation("Get updates for a specified term definitions.")
  public ResponseEntity<List> getLastTermModificationInContexts(@ApiParam(value = "Term ID", required = true) @PathVariable("termId") Long termId, @ApiIgnore Pageable page) {
    List result = service.getLastTermModificationInContexts(termId, page);
    return ResponseEntity.status(HttpStatus.OK).body(result);
  }

}
