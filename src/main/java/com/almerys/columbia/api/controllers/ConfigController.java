package com.almerys.columbia.api.controllers;

import com.almerys.columbia.api.ColumbiaConfiguration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class ConfigController extends AbstractRestController {
  private final ColumbiaConfiguration columbiaConfiguration;

  public ConfigController(ColumbiaConfiguration columbiaConfiguration) {
    this.columbiaConfiguration = columbiaConfiguration;
  }

  //---------- GET
  @GetMapping(path = "/config")
  public ResponseEntity getConfiguration() {
    Map<String, Object> response = new HashMap<>();
    response.put("adminRole", columbiaConfiguration.getAdminRoleName());
    response.put("moderatorRole", columbiaConfiguration.getModeratorRoleName());
    response.put("userRole", columbiaConfiguration.getUserRoleName());
    response.put("maxContextLevel", columbiaConfiguration.getMaxContextLevel());
    response.put("delegatedAuth", columbiaConfiguration.getDelegatedAuthentication());
    response.put("openRegistration", columbiaConfiguration.getOpenRegistration());

    return ResponseEntity.ok(response);
  }

}
