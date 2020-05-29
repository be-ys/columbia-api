package com.almerys.columbia.api.controllers;

import com.almerys.columbia.api.services.StatsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.util.Map;

@RestController
@Api(tags = "Stats Controller", description = "Get statistics from Columbia")
public class StatsController extends AbstractRestController {
  private final StatsService statsService;

  public StatsController(StatsService statsService) {
    this.statsService = statsService;
  }

  //---------- GET
  @ApiOperation(value = "Get stats for Columbia")
  @GetMapping(path = "/stats")
  public ResponseEntity<Map<String, Long>> getStatsForSpecificContext() {
    Map<String, Long> response = statsService.getStats();

    return (response == null)
        ? badRequest()
        : ResponseEntity.ok(response);
  }

  @ApiOperation(value = "Get stats from a specified context")
  @GetMapping(path = { "/contexts/{contextId}/stats", "/stats/contexts/{contextId}" })
  public ResponseEntity<Map<String, Long>> getStatsForSpecificContext(@ApiParam(value = "Id of the context", required = true) @PathVariable @Validated @NotNull Long contextId) {
    Map<String, Long> response = statsService.getStatsForContext(contextId);

    return (response == null)
        ? badRequest()
        : ResponseEntity.ok(response);
  }

}
