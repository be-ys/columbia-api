package com.almerys.columbia.api.controllers;

import com.almerys.columbia.api.ColumbiaConfiguration;
import com.almerys.columbia.api.domain.dto.DTOConfig;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Api(tags = "Configuration Controller", description = "Controller to get configuration from the server")
@RestController
public class ConfigController extends AbstractRestController {
  private final ColumbiaConfiguration columbiaConfiguration;

  public ConfigController(ColumbiaConfiguration columbiaConfiguration) {
    this.columbiaConfiguration = columbiaConfiguration;
  }

  //---------- GET
  @ApiOperation(value = "Get configuration")
  @GetMapping(path = "/config")
  public ResponseEntity<DTOConfig> getConfiguration() {
    DTOConfig config = new DTOConfig();

    config.adminRole = columbiaConfiguration.getAdminRoleName();
    config.moderatorRole = columbiaConfiguration.getModeratorRoleName();
    config.userRole = columbiaConfiguration.getUserRoleName();
    config.maxContextLevel = columbiaConfiguration.getMaxContextLevel();
    config.delegatedAuth = columbiaConfiguration.getDelegatedAuthentication();
    config.openRegistration = columbiaConfiguration.getOpenRegistration();

    return ResponseEntity.ok(config);
  }

}
