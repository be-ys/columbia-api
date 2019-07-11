package com.almerys.columbia.api.controllers;

import com.almerys.columbia.api.ColumbiaConfiguration;
import com.almerys.columbia.api.domain.ColumbiaUser;
import com.almerys.columbia.api.domain.dto.UserUpdater;
import com.almerys.columbia.api.services.UserService;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class SetupController extends AbstractRestController {
  private ColumbiaConfiguration columbiaConfiguration;
  private UserService userService;

  public SetupController(ColumbiaConfiguration columbiaConfiguration, UserService userService) {
    this.columbiaConfiguration = columbiaConfiguration;
    this.userService = userService;
  }

  @GetMapping("/setup")
  public ResponseEntity initialization() {
    if (!userService.getAll(PageRequest.of(0, 2)).isEmpty()) {
      return forbidden();
    }

    UserUpdater user = new UserUpdater();
    user.setUsername("columbialocal_admin");
    String password = RandomStringUtils.randomAlphanumeric(25);
    user.setPassword(password);
    user.setEmail("admin@localhost");
    user.setDomain("local");
    user.setActiv(true);
    user.setRole(columbiaConfiguration.getAdminRoleName());

    ColumbiaUser us =  userService.createUser(user);

    if (us == null) {
      return error();
    }

    Map<String, String> infos = new LinkedHashMap<>();
    infos.put("user", us.getUsername());
    infos.put("password", password);

    return ResponseEntity.ok(infos);

  }

}
