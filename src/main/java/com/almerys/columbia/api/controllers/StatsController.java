package com.almerys.columbia.api.controllers;

import com.almerys.columbia.api.services.StatsService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.util.Map;

@RestController
public class StatsController extends AbstractRestController {
  private final StatsService statsService;

  public StatsController(StatsService statsService) {
    this.statsService = statsService;
  }

  //---------- GET
  @GetMapping(path = "/stats")
  public ResponseEntity getStatsForSpecificContext() {
    Map<String, Long> response = statsService.getStats();

    return (response == null)
        ? badRequest()
        : ResponseEntity.ok(response);
  }

  @GetMapping(path = { "/contexts/{contextId}/stats", "/stats/contexts/{contextId}" })
  public ResponseEntity getStatsForSpecificContext(@PathVariable @Validated @NotNull Long contextId) {
    Map<String, Long> response = statsService.getStatsForContext(contextId);

    return (response == null)
        ? badRequest()
        : ResponseEntity.ok(response);
  }

}
