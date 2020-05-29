package com.almerys.columbia.api.controllers;

import com.almerys.columbia.api.services.AtomService;
import com.rometools.rome.feed.atom.Feed;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;

@Api(tags = "Atom/RSS Feed Controller", description = "Controller to get context updates in Atom format for your newsreader")
@RestController
public class AtomController extends AbstractRestController {
  private final AtomService atomService;

  public AtomController(AtomService atomService) {
    this.atomService = atomService;
  }

  @ApiOperation(value = "Get feed for Columbia")
  @GetMapping(path = "/feed")
  public ResponseEntity<Feed> getAtomFeed() {
    Feed feed = atomService.getLastModificationsAsAtomFeed();
    return (feed == null) ? notFound() : ResponseEntity.ok(feed);
  }

  @ApiOperation(value = "Get feed for a specified context")
  @GetMapping(path = { "/feed/contexts/{contextId}", "/contexts/{contextId}/feed" })
  public ResponseEntity<Feed> getAtomFeedForSpecificContext(@ApiParam(value="Context identifier", required = true) @NotNull @Validated @PathVariable Long contextId) {
    try {
      Feed feed = atomService.getLastModificationsAsAtomFeedForSpecificContext(contextId);
      return (feed == null) ? notFound() : ResponseEntity.ok(feed);
    } catch (IndexOutOfBoundsException e){
      return notFound();
    }
  }
}
