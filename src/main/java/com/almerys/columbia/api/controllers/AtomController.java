package com.almerys.columbia.api.controllers;

import com.almerys.columbia.api.services.AtomService;
import com.rometools.rome.feed.atom.Feed;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;

@RestController
public class AtomController extends AbstractRestController {
  private final AtomService atomService;

  public AtomController(AtomService atomService) {
    this.atomService = atomService;
  }

  @GetMapping(path = "/feed")
  public ResponseEntity getAtomFeed() {
    Feed feed = atomService.getLastModificationsAsAtomFeed();
    return (feed == null) ? notFound() : ResponseEntity.ok(feed);
  }

  @GetMapping(path = { "/feed/contexts/{contextId}", "/contexts/{contextId}/feed" })
  public ResponseEntity getAtomFeedForSpecificContext(@NotNull @Validated @PathVariable Long contextId) {
    Feed feed = atomService.getLastModificationsAsAtomFeedForSpecificContext(contextId);
    return (feed == null) ? notFound() : ResponseEntity.ok(feed);
  }
}
